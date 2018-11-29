package com.gempukku.secsy.entity.component;

public interface ComponentFieldTypeConverter<T, U> {
    T convertTo(U value);

    T convertTo(U value, Class<?> containedClass);

    U convertFrom(T value);

    T getDefaultValue();
}
