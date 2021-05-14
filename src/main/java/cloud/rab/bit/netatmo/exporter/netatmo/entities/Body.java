package cloud.rab.bit.netatmo.exporter.netatmo.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Body {

    private List<Device> devices = new ArrayList<>();
    private User user;

}
