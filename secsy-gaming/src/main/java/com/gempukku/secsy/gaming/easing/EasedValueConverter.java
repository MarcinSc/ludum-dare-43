package com.gempukku.secsy.gaming.easing;

import com.gempukku.secsy.entity.component.ComponentFieldTypeConverter;

public class EasedValueConverter implements ComponentFieldTypeConverter<EasedValue, String> {
    @Override
    public EasedValue convertTo(String value) {
        String[] components = value.split("\\*", 2);
        if (components.length == 1)
            return new EasedValue(Float.parseFloat(components[0]), null);
        return new EasedValue(Float.parseFloat(components[0]), components[1]);
    }

    @Override
    public EasedValue convertTo(String value, Class<?> containedClass) {
        return convertTo(value);
    }

    @Override
    public String convertFrom(EasedValue value) {
        return value.getMultiplier() + "*" + value.getRecipe();
    }

    @Override
    public EasedValue getDefaultValue() {
        return null;
    }
}
