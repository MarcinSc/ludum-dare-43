package com.gempukku.secsy.entity.component;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.JavaPackageProvider;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RegisterSystem(
        profiles = "reflectionsEntityComponentFieldHandler",
        shared = EntityComponentFieldHandler.class)
public class ReflectionsEntityComponentFieldHandler extends AbstractLifeCycleSystem implements EntityComponentFieldHandler {
    @Inject
    private JavaPackageProvider javaPackageProvider;

    private Map<Class<?>, EntityComponentFieldTypeHandler> handlerMap;

    @Override
    public float getPriority() {
        return 160;
    }

    @Override
    public void initialize() {
        handlerMap = new HashMap<Class<?>, EntityComponentFieldTypeHandler>();
        Set<URL> contextLocations = new HashSet<URL>();
        for (String javaPackage : javaPackageProvider.getJavaPackages()) {
            contextLocations.addAll(ClasspathHelper.forPackage(javaPackage, ClasspathHelper.contextClassLoader()));
        }

        Configuration scanConfiguration = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner())
                .setUrls(contextLocations);

        Reflections reflections = new Reflections(scanConfiguration);
        Set<Class<? extends EntityComponentFieldTypeHandler>> fieldHandlers = reflections.getSubTypesOf(EntityComponentFieldTypeHandler.class);
        for (Class<? extends EntityComponentFieldTypeHandler> fieldHandler : fieldHandlers) {
            for (Type type : fieldHandler.getGenericInterfaces()) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    Type rawType = parameterizedType.getRawType();
                    if (rawType == EntityComponentFieldTypeHandler.class) {
                        Type paramType = parameterizedType.getActualTypeArguments()[0];
                        if (paramType instanceof Class) {
                            try {
                                handlerMap.put((Class) paramType, fieldHandler.newInstance());
                            } catch (InstantiationException e) {
                                throw new IllegalStateException("Unable to create handler of type " + fieldHandler.getSimpleName(), e);
                            } catch (IllegalAccessException e) {
                                throw new IllegalStateException("Unable to create handler of type " + fieldHandler.getSimpleName(), e);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public <T> T copyFromEntity(T value, Class<T> clazz) {
        EntityComponentFieldTypeHandler handler = handlerMap.get(clazz);
        if (handler == null)
            return value;
        return (T) handler.copyFromEntity(value);
    }

    @Override
    public <T> T storeIntoEntity(T oldValue, T newValue, Class<T> clazz) {
        EntityComponentFieldTypeHandler handler = handlerMap.get(clazz);
        if (handler == null)
            return newValue;
        return (T) handler.storeIntoEntity(oldValue, newValue);
    }
}
