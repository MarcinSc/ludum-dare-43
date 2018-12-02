package com.gempukku.secsy.gaming.component.converter;

import com.gempukku.secsy.entity.component.ComponentFieldTypeConverter;

import java.util.HashMap;
import java.util.Map;

public class MapConverter implements ComponentFieldTypeConverter<Map, Map> {
    @Override
    public Map convertTo(Map value) {
        return new HashMap(value);
    }

    @Override
    public Map convertTo(Map value, Class<?> containedClass) {
        return convertTo(value);
    }

    @Override
    public Map convertFrom(Map value) {
        return new HashMap(convertTo(value));
    }

    @Override
    public Map getDefaultValue() {
        return new HashMap();
    }
}
