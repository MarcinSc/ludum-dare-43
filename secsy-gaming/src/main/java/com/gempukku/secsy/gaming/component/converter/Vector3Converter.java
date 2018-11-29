package com.gempukku.secsy.gaming.component.converter;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.component.ComponentFieldTypeConverter;

public class Vector3Converter implements ComponentFieldTypeConverter<Vector3, String> {
    @Override
    public String convertFrom(Vector3 value) {
        return value.x + "," + value.y + "," + value.z;
    }

    @Override
    public Vector3 convertTo(String value) {
        String[] split = value.split(",");
        return new Vector3(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]));
    }

    @Override
    public Vector3 convertTo(String value, Class<?> containedClass) {
        return convertTo(value);
    }

    @Override
    public Vector3 getDefaultValue() {
        return new Vector3();
    }
}
