package com.gempukku.secsy.gaming.rendering.splash;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.component.Container;

import java.util.List;

public interface SplashSeriesComponent extends Component {
    long getStartTime();

    String getTextureAtlasId();

    @Container(SplashDefinition.class)
    List<SplashDefinition> getSplashDefinitions();

    @Container(SplashDefinition.class)
    void setSplashDefinitions(List<SplashDefinition> splashDefinitions);
}
