package com.gempukku.secsy.gaming.rendering.postprocess.rain;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.secsy.entity.Component;

public interface RainComponent extends Component {
    Color getRainColor();

    float getRainAngle();

    float getRainAngleVariance();

    // This should be really small, usually no bigger than 0.001, unless you want to simulate some crazy changing winds
    float getRainAngleVarianceSpeed();
}
