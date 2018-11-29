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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RegisterSystem(
        profiles = "reflectionsComponentFieldConverter",
        shared = ComponentFieldConverter.class)
public class ReflectionsComponentFieldConverter extends AbstractLifeCycleSystem implements ComponentFieldConverter {
    @Inject
    private JavaPackageProvider javaPackageProvider;

    private Map<Class<?>, ComponentFieldTypeConverter> converterMap;

    @Override
    public float getPriority() {
        return 150;
    }

    @Override
    public void initialize() {
        converterMap = new HashMap<Class<?>, ComponentFieldTypeConverter>();
        Set<URL> contextLocations = new HashSet<URL>();
        for (String javaPackage : javaPackageProvider.getJavaPackages()) {
            contextLocations.addAll(ClasspathHelper.forPackage(javaPackage, ClasspathHelper.contextClassLoader()));
        }

        Configuration scanConfiguration = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner())
                .setUrls(contextLocations);

        Reflections reflections = new Reflections(scanConfiguration);
        Set<Class<? extends ComponentFieldTypeConverter>> fieldConverters = reflections.getSubTypesOf(ComponentFieldTypeConverter.class);
        for (Class<? extends ComponentFieldTypeConverter> fieldConverter : fieldConverters) {
            for (Type type : fieldConverter.getGenericInterfaces()) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    Type rawType = parameterizedType.getRawType();
                    if (rawType == ComponentFieldTypeConverter.class) {
                        Type paramType = parameterizedType.getActualTypeArguments()[0];
                        if (paramType instanceof Class) {
                            try {
                                try {
                                    Constructor<? extends ComponentFieldTypeConverter> constructorWithParam = fieldConverter.getConstructor(ComponentFieldConverter.class);
                                    converterMap.put((Class) paramType, constructorWithParam.newInstance(this));
                                } catch (NoSuchMethodException exp) {
                                    converterMap.put((Class) paramType, fieldConverter.newInstance());
                                }
                            } catch (InstantiationException e) {
                                throw new IllegalStateException("Unable to create converter of type " + fieldConverter.getSimpleName(), e);
                            } catch (IllegalAccessException e) {
                                throw new IllegalStateException("Unable to create converter of type " + fieldConverter.getSimpleName(), e);
                            } catch (InvocationTargetException e) {
                                throw new IllegalStateException("Unable to create converter of type " + fieldConverter.getSimpleName(), e);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean hasConverterForType(Class<?> clazz) {
        return converterMap.containsKey(clazz);
    }

    @Override
    public <T> Object convertFrom(T value, Class<T> clazz) {
        ComponentFieldTypeConverter converter = converterMap.get(clazz);
        if (converter == null)
            throw new IllegalStateException("Unable to find converter for type " + clazz.getSimpleName());
        return converter.convertFrom(value);
    }

    @Override
    public <T> T convertTo(Object value, Class<T> clazz) {
        ComponentFieldTypeConverter converter = converterMap.get(clazz);
        if (converter == null)
            throw new IllegalStateException("Unable to find converter for type " + clazz.getSimpleName());
        return (T) converter.convertTo(value);
    }

    @Override
    public <T> T convertTo(Object value, Class<T> clazz, Class<?> containedClass) {
        ComponentFieldTypeConverter converter = converterMap.get(clazz);
        if (converter == null)
            throw new IllegalStateException("Unable to find converter for type " + clazz.getSimpleName());
        return (T) converter.convertTo(value, containedClass);
    }

    @Override
    public <T> T getDefaultValue(Class<T> clazz) {
        ComponentFieldTypeConverter converter = converterMap.get(clazz);
        if (converter == null)
            throw new IllegalStateException("Unable to find converter for type " + clazz.getSimpleName());
        return (T) converter.getDefaultValue();
    }
}
