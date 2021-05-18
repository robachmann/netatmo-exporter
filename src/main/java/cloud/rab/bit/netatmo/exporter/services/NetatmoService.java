package cloud.rab.bit.netatmo.exporter.services;

import cloud.rab.bit.netatmo.exporter.netatmo.NetatmoClient;
import cloud.rab.bit.netatmo.exporter.netatmo.entities.Device;
import cloud.rab.bit.netatmo.exporter.netatmo.entities.DeviceData;
import cloud.rab.bit.netatmo.exporter.netatmo.entities.Module;
import cloud.rab.bit.netatmo.exporter.netatmo.entities.ModuleData;
import cloud.rab.bit.netatmo.exporter.netatmo.entities.NetatmoResponse;
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

            Map<String, String> deviceLabels = Map.of(
                    "deviceId", device.getId(),
                    "stationName", device.getStationName(),
                    "moduleName", device.getModuleName()
            );

            metricList.add(createMetric("netatmo_firmware", device::getFirmware, deviceLabels));
            metricList.add(createMetric("netatmo_diagnostics", device::getWifiStatus, deviceLabels, Map.of("type", "wifi_status")));

            DeviceData deviceData = device.getDeviceData();
            metricList.add(createMetric("netatmo_temperature_c", deviceData::getTemperature, deviceLabels));
            metricList.add(createMetric("netatmo_humidity_percent", deviceData::getHumidity, deviceLabels));
            metricList.add(createMetric("netatmo_co2_ppm", deviceData::getCo2, deviceLabels));
            metricList.add(createMetric("netatmo_noise_db", deviceData::getNoise, deviceLabels));
            metricList.add(createMetric("netatmo_pressure_mbar", deviceData::getPressure, deviceLabels, Map.of("type", "relative")));
            metricList.add(createMetric("netatmo_pressure_mbar", deviceData::getAbsolutePressure, deviceLabels, Map.of("type", "absolute")));

            for (Module module : device.getModules()) {

                Map<String, String> moduleLabels = Map.of(
                        "deviceId", device.getId(),
                        "stationName", device.getStationName(),
                        "moduleName", module.getModuleName(),
                        "moduleId", module.getId()
                );

                metricList.add(createMetric("netatmo_firmware", module::getFirmware, moduleLabels));
                metricList.add(createMetric("netatmo_battery_percent", module::getBatteryPercent, moduleLabels));
                metricList.add(createMetric("netatmo_diagnostics", module::getRfStatus, moduleLabels, Map.of("type", "rf_status")));

                ModuleData moduleData = module.getDashboardData();
                metricList.add(createMetric("netatmo_temperature_c", moduleData::getTemperature, moduleLabels));
                metricList.add(createMetric("netatmo_humidity_percent", moduleData::getHumidity, moduleLabels));
                if (moduleData.getCo2() != null) {
                    metricList.add(createMetric("netatmo_co2_ppm", moduleData::getCo2, moduleLabels));
                }
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
