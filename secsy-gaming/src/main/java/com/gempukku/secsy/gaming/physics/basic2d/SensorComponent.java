package com.gempukku.secsy.gaming.physics.basic2d;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.component.Container;

import java.util.List;

public interface SensorComponent extends Component {
    @Container(Sensor.class)
    List<Sensor> getSensors();

    @Container(Sensor.class)
    void setSensors(List<Sensor> sensors);
}
