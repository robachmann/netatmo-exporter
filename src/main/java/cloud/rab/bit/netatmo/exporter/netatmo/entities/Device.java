package cloud.rab.bit.netatmo.exporter.netatmo.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Device {

    @JsonProperty("_id")
    private String id;
    @JsonProperty("cipher_id")
    private String cipherId;
    @JsonProperty("date_setup")
    private Long dateSetup;
    @JsonProperty("last_setup")
    private Long lastSetup;
    private String type;
    @JsonProperty("last_status_store")
    private Long lastStatusStore;
    @JsonProperty("module_name")
    private String moduleName;
    private Long firmware;
    @JsonProperty("last_upgrade")
    private Long lastUpgrade;
    @JsonProperty("wifi_status")
    private Long wifiStatus;
    private Boolean reachable;
    @JsonProperty("co2_calibrating")
    private Boolean co2Calibrating;
    @JsonProperty("station_name")
    private String stationName;
    @JsonProperty("data_type")
    private List<String> dataType = new ArrayList<>();
    @JsonProperty("place")
    private Place place;
    @JsonProperty("dashboard_data")
    private IndoorData indoorData;
    private List<Module> modules = new ArrayList<>();

}
