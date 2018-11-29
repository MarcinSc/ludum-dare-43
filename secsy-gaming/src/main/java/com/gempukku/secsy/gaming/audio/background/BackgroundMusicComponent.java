package com.gempukku.secsy.gaming.audio.background;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.component.Container;

import java.util.List;

public interface BackgroundMusicComponent extends Component {
    long getStartTime();

    @Container(BackgroundMusicDefinition.class)
    List<BackgroundMusicDefinition> getBackgroundMusicDefinitions();
}
