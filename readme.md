# Netatmo Exporter

This is a simple Prometheus exporter for [Netatmo weather stations](https://dev.netatmo.com/apidocumentation/weather).

## Format

The /metrics endpoint returns metrics in this format:

```
# TYPE netatmo_firmware gauge
netatmo_firmware{deviceId="70:ee:50:xx:xx:xx", deviceName="Indoor", location="indoor", stationName="MyStation (Indoor)"} 181
# TYPE netatmo_diagnostics gauge
netatmo_diagnostics{deviceId="70:ee:50:xx:xx:xx", deviceName="Indoor", location="indoor", stationName="MyStation (Indoor)", type="wifi_status"} 52
# TYPE netatmo_temperature_c gauge
netatmo_temperature_c{deviceId="70:ee:50:xx:xx:xx", deviceName="Indoor", location="indoor", stationName="MyStation (Indoor)"} 22.9
# TYPE netatmo_humidity_percent gauge
netatmo_humidity_percent{deviceId="70:ee:50:xx:xx:xx", deviceName="Indoor", location="indoor", stationName="MyStation (Indoor)"} 45
# TYPE netatmo_co2_ppm gauge
netatmo_co2_ppm{deviceId="70:ee:50:xx:xx:xx", deviceName="Indoor", location="indoor", stationName="MyStation (Indoor)"} 678
# TYPE netatmo_noise_db gauge
netatmo_noise_db{deviceId="70:ee:50:xx:xx:xx", deviceName="Indoor", location="indoor", stationName="MyStation (Indoor)"} 49
# TYPE netatmo_pressure_mbar gauge
netatmo_pressure_mbar{deviceId="70:ee:50:xx:xx:xx", deviceName="Indoor", location="indoor", stationName="MyStation (Indoor)", type="relative"} 1010.9
# TYPE netatmo_pressure_mbar gauge
netatmo_pressure_mbar{deviceId="70:ee:50:xx:xx:xx", deviceName="Indoor", location="indoor", stationName="MyStation (Indoor)", type="absolute"} 960.3
# TYPE netatmo_firmware gauge
netatmo_firmware{deviceId="70:ee:50:xx:xx:xx", location="outdoor", moduleId="02:00:00:xx:xx:xx", moduleName="Outdoor", stationName="MyStation (Indoor)"} 50
# TYPE netatmo_battery_percent gauge
netatmo_battery_percent{deviceId="70:ee:50:xx:xx:xx", location="outdoor", moduleId="02:00:00:xx:xx:xx", moduleName="Outdoor", stationName="MyStation (Indoor)"} 94
# TYPE netatmo_diagnostics gauge
netatmo_diagnostics{deviceId="70:ee:50:xx:xx:xx", location="outdoor", moduleId="02:00:00:xx:xx:xx", moduleName="Outdoor", stationName="MyStation (Indoor)", type="rf_status"} 84
# TYPE netatmo_temperature_c gauge
netatmo_temperature_c{deviceId="70:ee:50:xx:xx:xx", location="outdoor", moduleId="02:00:00:xx:xx:xx", moduleName="Outdoor", stationName="MyStation (Indoor)"} 13.3
# TYPE netatmo_humidity_percent gauge
netatmo_humidity_percent{deviceId="70:ee:50:xx:xx:xx", location="outdoor", moduleId="02:00:00:xx:xx:xx", moduleName="Outdoor", stationName="MyStation (Indoor)"} 64
# TYPE netatmo_scrape_duration_ms gauge
netatmo_scrape_duration_ms 369
```

## Configuration

Before deploying, you need to set the following properties:

- NETATMO_CLIENT_ID
- NETATMO_CLIENT_SECRET
- NETATMO_REFRESH_TOKEN

If you don't set them, this error log is printed upon startup of the exporter:

```
2021-05-14 18:07:07.152 ERROR 1 --- [main] c.r.b.n.e.services.PropertiesChecker: Missing property: NETATMO_CLIENT_ID
2021-05-14 18:07:07.153 ERROR 1 --- [main] c.r.b.n.e.services.PropertiesChecker: Missing property: NETATMO_CLIENT_SECRET
2021-05-14 18:07:07.153 ERROR 1 --- [main] c.r.b.n.e.services.PropertiesChecker: Missing property: NETATMO_REFRESH_TOKEN
```

You can read more about this authentication method in the official [Netatmo docs](https://dev.netatmo.com/apidocumentation/oauth).

An easy way to obtain the required refresh-token is by using this prepared [httpie](https://httpie.io/) command:

```bash
http --form POST https://api.netatmo.com/oauth2/token grant_type=password scope=read_station client_id=<clientId> client_secret=<clientSecret> username=<email|user> password=<password>

{
    "access_token": "<accessToken>",
    "expire_in": 10800,
    "expires_in": 10800,
    "refresh_token": "<refreshToken>",
    "scope": [
        "read_station"
    ]
}
```

Store the values in the [secret](k8s/02_exporter-secret.yml) provided.

## Deployment

With the Netatmo config applied, simply run `kubectl apply`:

```bash
$ cd k8s
$ kubectl apply -f .
namespace/netatmo-exporter configured
secret/netatmo-exporter-secret configured
deployment.apps/netatmo-exporter configured
service/netatmo-exporter configured
servicemonitor.monitoring.coreos.com/netatmo-exporter configured
```

Note that [05_exporter-service-monitor.yml](k8s/05_exporter-service-monitor.yml) also installs a `ServiceMonitor` in order to seamlessly integrate with the [kube-prometheus-stack operator](https://github.com/prometheus-community/helm-charts/tree/main/charts/kube-prometheus-stack).

Afterwards, check the logs if everything works as expected:

```bash
$ kubectl logs $(kubectl get pods -o name) 

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.5)

2021-05-14 18:18:56.292  INFO 1 --- [           main] c.r.b.n.e.NetatmoExporterApplication     : Starting NetatmoExporterApplication using Java 11.0.11 on netatmo-exporter-7857cff4fb-bjk6r with PID 1 (/workspace/BOOT-INF/classes started by cnb in /workspace)
2021-05-14 18:18:56.295  INFO 1 --- [           main] c.r.b.n.e.NetatmoExporterApplication     : No active profile set, falling back to default profiles: default
2021-05-14 18:18:58.894  INFO 1 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8080
2021-05-14 18:18:58.906  INFO 1 --- [           main] c.r.b.n.e.NetatmoExporterApplication     : Started NetatmoExporterApplication in 3.479 seconds (JVM running for 3.953)
2021-05-14 18:20:40.419  INFO 1 --- [or-http-epoll-3] c.r.b.n.exporter.netatmo.NetatmoClient   : Token: 200 OK
2021-05-14 18:20:40.823  INFO 1 --- [or-http-epoll-3] c.r.b.n.exporter.netatmo.NetatmoClient   : StationsData: 200 OK
```

It might take a couple of minutes for the first scrape to occur.

You should see logs similar to `Token: 200 OK` every 3 hours (the validity of an access token) and `StationsData: 200 OK` every 10 minutes (data is sent to the server every 10 minutes, according to the [Netatmo docs](https://dev.netatmo.com/apidocumentation/weather#product-details)).
This implementation will cache queried data and is thus decoupled from a shorter configured  scrape [interval](k8s/05_exporter-service-monitor.yml). 
