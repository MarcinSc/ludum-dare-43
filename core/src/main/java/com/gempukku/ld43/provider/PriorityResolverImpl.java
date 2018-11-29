package com.gempukku.ld43.provider;

import com.badlogic.gdx.Gdx;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.dispatch.PriorityResolver;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

@RegisterSystem(shared = PriorityResolver.class)
public class PriorityResolverImpl extends AbstractLifeCycleSystem implements PriorityResolver {
    private Properties properties;

    @Override
    public void preInitialize() {
        properties = new Properties();
        try {
            Reader reader = Gdx.files.internal("properties/priorities.properties").reader();
            try {
                properties.load(reader);
            } finally {
                reader.close();
            }
        } catch (IOException exp) {
            throw new RuntimeException("Unable to load properties", exp);
        }
    }

    @Override
    public Float getPriority(String priorityName) {
        String result = properties.getProperty(priorityName);
        if (result != null)
            return Float.parseFloat(result);
        return null;
    }
}
