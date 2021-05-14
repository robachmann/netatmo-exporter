package cloud.rab.bit.netatmo.exporter.netatmo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("netatmo")
public class NetatmoProperties {

    private String baseUrl = "https://api.netatmo.com";
    private String clientId;
    private String clientSecret;
    private String refreshToken;

}
