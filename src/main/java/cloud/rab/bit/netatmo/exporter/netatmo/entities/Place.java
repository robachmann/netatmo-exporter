package cloud.rab.bit.netatmo.exporter.netatmo.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Place {

    private Long altitude;
    private String city;
    private String country;
    private String timezone;
    private List<Double> location = new ArrayList<>();
}
