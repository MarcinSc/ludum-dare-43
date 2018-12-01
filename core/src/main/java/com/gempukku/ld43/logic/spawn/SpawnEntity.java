package com.gempukku.ld43.logic.spawn;

import com.gempukku.secsy.entity.event.Event;

public class SpawnEntity extends Event {
    private String prefab;
    private float x;
    private float y;

    public SpawnEntity(String prefab) {
        this.prefab = prefab;
    }

    public SpawnEntity(String prefab, float x, float y) {
        this.prefab = prefab;
        this.x = x;
        this.y = y;
    }

    public String getPrefab() {
        return prefab;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
