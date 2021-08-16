package cloud.rab.bit.netatmo.exporter.services;

import cloud.rab.bit.netatmo.exporter.netatmo.NetatmoClient;
import cloud.rab.bit.netatmo.exporter.netatmo.entities.Body;
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

        Body body = response.getBody();
        if (body != null) {
            for (Device device : body.getDevices()) {

                Map<String, String> deviceLabels = Map.of(
                        "deviceId", device.getId(),
                        "stationName", device.getStationName(),
                        "moduleName", device.getModuleName()
                );

                addMetric("netatmo_firmware", device::getFirmware, deviceLabels, metricList);
                addMetric("netatmo_diagnostics", device::getWifiStatus, deviceLabels, Map.of("type", "wifi_status"), metricList);

                DeviceData deviceData = device.getDeviceData();
                addMetric("netatmo_temperature_c", deviceData::getTemperature, deviceLabels, metricList);
                addMetric("netatmo_humidity_percent", deviceData::getHumidity, deviceLabels, metricList);
                addMetric("netatmo_co2_ppm", deviceData::getCo2, deviceLabels, metricList);
                addMetric("netatmo_noise_db", deviceData::getNoise, deviceLabels, metricList);
                addMetric("netatmo_pressure_mbar", deviceData::getPressure, deviceLabels, Map.of("type", "relative"), metricList);
                addMetric("netatmo_pressure_mbar", deviceData::getAbsolutePressure, deviceLabels, Map.of("type", "absolute"), metricList);

                for (Module module : device.getModules()) {

                    if (module != null) {
                        Map<String, String> moduleLabels = Map.of(
                                "deviceId", String.valueOf(device.getId()),
                                "stationName", String.valueOf(device.getStationName()),
                                "moduleName", String.valueOf(module.getModuleName()),
                                "moduleId", String.valueOf(module.getId())
                        );

                        addMetric("netatmo_firmware", module::getFirmware, moduleLabels, metricList);
                        addMetric("netatmo_firmware", module::getFirmware, moduleLabels, metricList);
                        addMetric("netatmo_battery_percent", module::getBatteryPercent, moduleLabels, metricList);
                        addMetric("netatmo_diagnostics", module::getRfStatus, moduleLabels, Map.of("type", "rf_status"), metricList);

                        ModuleData moduleData = module.getDashboardData();
                        if (moduleData != null) {
                            addMetric("netatmo_temperature_c", moduleData::getTemperature, moduleLabels, metricList);
                            addMetric("netatmo_humidity_percent", moduleData::getHumidity, moduleLabels, metricList);
                            addMetric("netatmo_co2_ppm", moduleData::getCo2, moduleLabels, metricList);
                        }
                    }
                }

            }
        }

        return metricList;
    }

    private void addMetric(String metricName, Supplier<Number> valueSupplier, Map<String, String> labels, List<Metric> metricList) {
        addMetric(metricName, valueSupplier, labels, new TreeMap<>(), metricList);
    }

    private void addMetric(String metricName, Supplier<Number> valueSupplier, Map<String, String> labels, Map<String, String> additionalLabels, List<Metric> metricList) {
        if (valueSupplier.get() != null) {
            metricList.add(createMetric(metricName, valueSupplier, labels, additionalLabels));
        }
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
