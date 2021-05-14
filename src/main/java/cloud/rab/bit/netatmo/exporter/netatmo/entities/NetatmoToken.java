package cloud.rab.bit.netatmo.exporter.netatmo.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NetatmoToken {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    private String[] scope;

    @JsonProperty("refresh_token")
    private String refreshToken;

}
