package cloud.rab.bit.netatmo.exporter.netatmo.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NetatmoResponse {

    private Body body;
    private String status;
    @JsonProperty("time_exec")
    private Double timeExec;
    @JsonProperty("time_server")
    private Long timeServer;

}
