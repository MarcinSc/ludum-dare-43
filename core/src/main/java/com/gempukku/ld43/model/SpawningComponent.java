package com.gempukku.ld43.model;

import com.gempukku.secsy.entity.Component;

public interface SpawningComponent extends Component {
    long getFrequency();

    long getLastSpawnTime();

    void setLastSpawnTime(long lastSpawnTime);

    String getPrefab();
}
