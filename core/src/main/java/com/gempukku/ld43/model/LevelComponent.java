package com.gempukku.ld43.model;

import com.gempukku.secsy.entity.Component;

public interface LevelComponent extends Component {
    int getLevelIndex();

    void setLevelIndex(int levelIndex);
}
