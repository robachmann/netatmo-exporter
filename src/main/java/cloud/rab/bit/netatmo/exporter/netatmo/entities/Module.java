package cloud.rab.bit.netatmo.exporter.netatmo.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Module {

    @JsonProperty("_id")
    private String id;
    private String type;
    @JsonProperty("module_name")
    private String moduleName;
    @JsonProperty("data_type")
    private List<String> dataType = new ArrayList<>();
    @JsonProperty("last_setup")
    private Long lastSetup;
    private Boolean reachable;
    @JsonProperty("dashboard_data")
    private OutdoorData dashboardData;
    private Long firmware;
    @JsonProperty("last_message")
    private Long lastMessage;
    @JsonProperty("last_seen")
    private Long lastSeen;
    @JsonProperty("rf_status")
    private Long rfStatus;
    @JsonProperty("battery_vp")
    private Long batteryVp;
    @JsonProperty("battery_percent")
    private Long batteryPercent;

}
