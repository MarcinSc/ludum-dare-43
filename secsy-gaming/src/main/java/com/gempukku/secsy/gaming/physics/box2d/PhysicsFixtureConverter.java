package com.gempukku.secsy.gaming.physics.box2d;

import com.gempukku.secsy.entity.component.ComponentFieldTypeConverter;
import org.json.simple.JSONObject;

public class PhysicsFixtureConverter implements ComponentFieldTypeConverter<PhysicsFixture, JSONObject> {
    @Override
    public PhysicsFixture convertTo(JSONObject value) {
        String fixtureType = (String) value.get("type");
        if (fixtureType.equals("box"))
            return new BoxFixture(convertToFloat(value.get("x")), convertToFloat(value.get("y")),
                    convertToFloat(value.get("width")), convertToFloat(value.get("height")),
                    convertToFloat(value.get("density")), convertToFloat(value.get("friction")),
                    convertToShort(value.get("category")), convertToShort(value.get("contactMask")),
                    (Boolean) value.get("sensor"), convertToFloat(value.get("angle")), (String) value.get("userData"));
        else if (fixtureType.equals("circle"))
            return new CircleFixture(convertToFloat(value.get("x")), convertToFloat(value.get("y")),
                    convertToFloat(value.get("radius")),
                    convertToFloat(value.get("density")), convertToFloat(value.get("friction")),
                    convertToShort(value.get("category")), convertToShort(value.get("contactMask")),
                    (Boolean) value.get("sensor"), (String) value.get("userData"));
        else
            throw new RuntimeException("Unkown PhysicsFixture type - " + fixtureType);
    }

    private short convertToShort(Object value) {
        return ((Number) value).shortValue();
    }

    private float convertToFloat(Object value) {
        if (value == null)
            return 0;
        return ((Number) value).floatValue();
    }

    @Override
    public PhysicsFixture convertTo(JSONObject value, Class<?> containedClass) {
        return convertTo(value);
    }

    @Override
    public JSONObject convertFrom(PhysicsFixture value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BoxFixture getDefaultValue() {
        return null;
    }
}
