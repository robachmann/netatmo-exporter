package cloud.rab.bit.netatmo.exporter.controllers;

import cloud.rab.bit.netatmo.exporter.services.NetatmoService;
import cloud.rab.bit.netatmo.exporter.services.entities.Metric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Timed;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MetricsController {

    private final NetatmoService netatmoService;

    @GetMapping(value = "metrics", produces = "plain/text;version=0.0.4")
    public Mono<String> getMetrics() {
        return netatmoService.getCurrentValues()
                .timed()
                .map(this::addScrapeMetric)
                .map(this::toPrometheusString);
    }

    private List<Metric> addScrapeMetric(Timed<List<Metric>> timed) {
        long millis = timed.elapsed().toMillis();

        Metric scrapeMetric = Metric.builder()
                .name("netatmo_scrape_duration_ms")
                .type("gauge")
                .value(millis)
                .build();

        if (millis > 0) {
            log.info("Scraping source took {} ms", millis);
        }

        List<Metric> metrics = timed.get();
        metrics.add(scrapeMetric);
        return metrics;
    }

    private String toPrometheusString(List<Metric> metrics) {
        return metrics.stream()
                .map(metric -> String.format("# TYPE %s %s\n%s%s %s", metric.getName(), metric.getType(), metric.getName(), formatTags(metric.getLabels()), metric.getValue()))
                .collect(Collectors.joining("\n"));
    }

    private String formatTags(Map<String, String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "";
        } else {
            return tags.entrySet().stream().map(entry -> entry.getKey() + "=\"" + entry.getValue() + "\"").collect(Collectors.joining(", ", "{", "}"));
        }
    }

}
