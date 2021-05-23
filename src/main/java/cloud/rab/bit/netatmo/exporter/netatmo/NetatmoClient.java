package cloud.rab.bit.netatmo.exporter.netatmo;

import cloud.rab.bit.netatmo.exporter.netatmo.entities.Body;
import cloud.rab.bit.netatmo.exporter.netatmo.entities.Device;
import cloud.rab.bit.netatmo.exporter.netatmo.entities.NetatmoResponse;
import cloud.rab.bit.netatmo.exporter.netatmo.entities.NetatmoToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
public class NetatmoClient {

    private final NetatmoProperties netatmoProperties;
    private final WebClient webClient;
    private final Mono<String> getAccessTokenMono;
    private final Mono<NetatmoResponse> getStationDataMono;

    public NetatmoClient(NetatmoProperties netatmoProperties, WebClient.Builder webClientBuilder) {
        this.netatmoProperties = netatmoProperties;
        this.webClient = webClientBuilder.baseUrl(netatmoProperties.getBaseUrl()).build();
        this.getAccessTokenMono = getAccessTokenMono().cache(this::extractTokenExpiration, throwable -> Duration.ZERO, () -> Duration.ZERO).map(NetatmoToken::getAccessToken);
        this.getStationDataMono = getStationDataMono().cache(this::extractStationTime, throwable -> Duration.ZERO, () -> Duration.ZERO);
    }

    private Duration extractTokenExpiration(NetatmoToken response) {
        return Duration.ofSeconds(response.getExpiresIn());
    }

    private Duration extractStationTime(NetatmoResponse netatmoResponse) {
        if (netatmoResponse != null) {
            Body body = netatmoResponse.getBody();
            if (body != null) {
                List<Device> devices = body.getDevices();
                if (!devices.isEmpty()) {
                    Device device = devices.get(0);
                    Long timeUtc = device.getLastStatusStore();
                    if (timeUtc != null) {
                        Instant stationInstant = Instant.ofEpochSecond(timeUtc);
                        return Duration.between(Instant.now(), stationInstant).plusMinutes(10);
                    }
                }
            }
        }
        return Duration.ZERO;
    }

    public Mono<NetatmoResponse> getStationData() {
        return this.getStationDataMono;
    }

    private Mono<NetatmoToken> getAccessTokenMono() {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("client_id", netatmoProperties.getClientId());
        formData.add("client_secret", netatmoProperties.getClientSecret());
        formData.add("refresh_token", netatmoProperties.getRefreshToken());

        return webClient.post()
                .uri("/oauth2/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().isError()) {
                        log.warn("Token: {}", clientResponse.statusCode());
                    } else {
                        log.info("Token: {}", clientResponse.statusCode());
                    }
                    return clientResponse.bodyToMono(NetatmoToken.class);
                });

    }

    private Mono<NetatmoResponse> getStationDataMono() {
        return getAccessTokenMono.flatMap(this::getStationData);
    }

    private Mono<NetatmoResponse> getStationData(String accessToken) {
        return webClient.get()
                .uri("/api/getstationsdata?access_token={accessToken}", accessToken)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().isError()) {
                        log.warn("StationsData: {}", clientResponse.statusCode());
                    } else {
                        log.info("StationsData: {}", clientResponse.statusCode());
                    }
                    return clientResponse.bodyToMono(NetatmoResponse.class);
                });
    }

}
