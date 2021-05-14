package cloud.rab.bit.netatmo.exporter.services.entities;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Metric {

    private String name;
    private String type;
    private Number value;
    private Map<String, String> labels;

}
