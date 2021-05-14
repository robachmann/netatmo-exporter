package cloud.rab.bit.netatmo.exporter.services;


import cloud.rab.bit.netatmo.exporter.netatmo.NetatmoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class PropertiesChecker {

    private final NetatmoProperties netatmoProperties;

    @EventListener(ApplicationStartedEvent.class)
    public void checkPropertiesSet() {

        if (ObjectUtils.isEmpty(netatmoProperties.getClientId())) {
            log.error("Missing property: NETATMO_CLIENT_ID");
        }

        if (ObjectUtils.isEmpty(netatmoProperties.getClientSecret())) {
            log.error("Missing property: NETATMO_CLIENT_SECRET");
        }

        if (ObjectUtils.isEmpty(netatmoProperties.getRefreshToken())) {
            log.error("Missing property: NETATMO_REFRESH_TOKEN");
        }

    }
}
