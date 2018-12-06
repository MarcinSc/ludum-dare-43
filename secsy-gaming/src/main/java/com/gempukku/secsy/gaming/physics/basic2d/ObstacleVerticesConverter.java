package com.gempukku.secsy.gaming.physics.basic2d;

import com.gempukku.secsy.entity.component.ComponentFieldTypeConverter;
import org.json.simple.JSONArray;

public class ObstacleVerticesConverter implements ComponentFieldTypeConverter<ObstacleVertices, JSONArray> {
    @Override
    public ObstacleVertices convertTo(JSONArray value) {
        float[] vertices = new float[value.size() * 2];
        for (int i = 0; i < vertices.length; i += 2) {
            String vertexStr = (String) value.get(i / 2);
            String[] split = vertexStr.split(",", 2);

            vertices[i] = Float.parseFloat(split[0]);
            vertices[i + 1] = Float.parseFloat(split[1]);
        }
        return new ObstacleVertices(vertices);
    }

    @Override
    public ObstacleVertices convertTo(JSONArray value, Class<?> containedClass) {
        return convertTo(value);
    }

    @Override
    public JSONArray convertFrom(ObstacleVertices value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObstacleVertices getDefaultValue() {
        return null;
    }
}
