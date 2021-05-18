package cloud.rab.bit.netatmo.exporter.netatmo.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeviceData {

    @JsonProperty("time_utc")
    private Long timeUtc;
    @JsonProperty("Temperature")
    private Double temperature;
    @JsonProperty("CO2")
    private Long co2;
    @JsonProperty("Humidity")
    private Long humidity;
    @JsonProperty("Noise")
    private Long noise;
    @JsonProperty("Pressure")
    private Double pressure;
    @JsonProperty("AbsolutePressure")
    private Double absolutePressure;
    @JsonProperty("min_temp")
    private Double minTemp;
    @JsonProperty("max_temp")
    private Double maxTemp;
    @JsonProperty("date_min_temp")
    private Long dateMinTemp;
    @JsonProperty("date_max_temp")
    private Long dateMaxTemp;
    @JsonProperty("temp_trend")
    private String tempTrend;
    @JsonProperty("pressure_trend")
    private String pressureTrend;


}
