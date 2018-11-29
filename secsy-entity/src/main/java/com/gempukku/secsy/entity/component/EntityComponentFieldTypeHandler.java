package com.gempukku.secsy.entity.component;

public interface EntityComponentFieldTypeHandler<T> {
    T copyFromEntity(T value);

    T storeIntoEntity(T oldValue, T newValue);
}
