package com.gempukku.secsy.entity.component.converter;

import com.gempukku.secsy.entity.component.ComponentFieldConverter;
import com.gempukku.secsy.entity.component.ComponentFieldTypeConverter;

import java.util.ArrayList;
import java.util.List;

public class ListConverter implements ComponentFieldTypeConverter<List, List> {
    private ComponentFieldConverter componentFieldConverter;

    public ListConverter(ComponentFieldConverter componentFieldConverter) {
        this.componentFieldConverter = componentFieldConverter;
    }

    @Override
    public List convertTo(List value) {
        return new ArrayList(value);
    }

    @Override
    public List convertTo(List value, Class<?> containedClass) {
        if (componentFieldConverter.hasConverterForType(containedClass)) {
            List result = new ArrayList(value.size());
            for (Object originalValue : value) {
                result.add(componentFieldConverter.convertTo(originalValue, containedClass));
            }
            return result;
        } else {
            return new ArrayList(value);
        }
    }

    @Override
    public List convertFrom(List value) {
        return value;
    }

    @Override
    public List getDefaultValue() {
        return new ArrayList();
    }
}
