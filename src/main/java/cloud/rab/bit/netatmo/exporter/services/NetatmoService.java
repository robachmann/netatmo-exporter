package cloud.rab.bit.netatmo.exporter.services;

import cloud.rab.bit.netatmo.exporter.netatmo.NetatmoClient;
import cloud.rab.bit.netatmo.exporter.netatmo.entities.Module;
import cloud.rab.bit.netatmo.exporter.netatmo.entities.*;
import cloud.rab.bit.netatmo.exporter.services.entities.Metric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class NetatmoService {

    private final NetatmoClient netatmoClient;

    public Mono<List<Metric>> getCurrentValues() {
        return netatmoClient.getStationData()
                .map(this::toMetrics);
    }

    private List<Metric> toMetrics(NetatmoResponse response) {

        List<Metric> metricList = new ArrayList<>();

        for (Device device : response.getBody().getDevices()) {

            Map<String, String> indoorLabels = Map.of(
                    "deviceId", device.getId(),
                    "deviceName", device.getModuleName(),
                    "stationName", device.getStationName(),
                    "location", "indoor"
            );

            metricList.add(createMetric("netatmo_firmware", device::getFirmware, indoorLabels));
            metricList.add(createMetric("netatmo_diagnostics", device::getWifiStatus, indoorLabels, Map.of("type", "wifi_status")));

            IndoorData indoorData = device.getIndoorData();
            metricList.add(createMetric("netatmo_temperature_c", indoorData::getTemperature, indoorLabels));
            metricList.add(createMetric("netatmo_humidity_percent", indoorData::getHumidity, indoorLabels));
            metricList.add(createMetric("netatmo_co2_ppm", indoorData::getCo2, indoorLabels));
            metricList.add(createMetric("netatmo_noise_db", indoorData::getNoise, indoorLabels));
            metricList.add(createMetric("netatmo_pressure_mbar", indoorData::getPressure, indoorLabels, Map.of("type", "relative")));
            metricList.add(createMetric("netatmo_pressure_mbar", indoorData::getAbsolutePressure, indoorLabels, Map.of("type", "absolute")));

            for (Module module : device.getModules()) {

                Map<String, String> outdoorLabels = Map.of(
                        "deviceId", device.getId(),
                        "stationName", device.getStationName(),
                        "moduleName", module.getModuleName(),
                        "moduleId", module.getId(),
                        "location", "outdoor"
                );

                metricList.add(createMetric("netatmo_firmware", module::getFirmware, outdoorLabels));
                metricList.add(createMetric("netatmo_battery_percent", module::getBatteryPercent, outdoorLabels));
                metricList.add(createMetric("netatmo_diagnostics", module::getRfStatus, outdoorLabels, Map.of("type", "rf_status")));

                OutdoorData outdoorData = module.getDashboardData();
                metricList.add(createMetric("netatmo_temperature_c", outdoorData::getTemperature, outdoorLabels));
                metricList.add(createMetric("netatmo_humidity_percent", outdoorData::getHumidity, outdoorLabels));

            }

        }

        return metricList;
    }

    private Metric createMetric(String metricName, Supplier<Number> valueSupplier, Map<String, String> labels) {
        return createMetric(metricName, valueSupplier, labels, new TreeMap<>());
    }

    private Metric createMetric(String metricName, Supplier<Number> valueSupplier, Map<String, String> labels, Map<String, String> additionalLabels) {
        Map<String, String> metricLabels = new TreeMap<>();
        metricLabels.putAll(labels);
        metricLabels.putAll(additionalLabels);

        return Metric.builder()
                .name(metricName)
                .type("gauge")
                .value(valueSupplier.get())
                .labels(metricLabels)
                .build();
    }

}
