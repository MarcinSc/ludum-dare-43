package com.gempukku.secsy.entity;

import java.util.Collection;

public interface EntityListener {
    void entityModified(SimpleEntity entity, Collection<Class<? extends Component>> affectedComponents);

    void entitiesModified(Iterable<SimpleEntity> entities);
}
