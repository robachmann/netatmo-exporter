package cloud.rab.bit.netatmo.exporter.netatmo.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Administrative {

    private String country;
    @JsonProperty("reg_locale")
    private String regLocale;
    private String lang;
    private Long unit;
    private Long windunit;
    private Long pressureunit;
    @JsonProperty("feel_like_algo")
    private Long feelLikeAlgo;

}
