package com.gempukku.secsy.entity.component;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.EntityRef;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RegisterSystem(profiles = "nameConventionComponents", shared = {ComponentManager.class, InternalComponentManager.class})
public class MapNamingConventionProxyComponentManager implements ComponentManager, InternalComponentManager {
    @Inject
    private EntityComponentFieldHandler entityComponentFieldHandler;
    @Inject
    private ComponentFieldConverter componentFieldConverter;

    private static final Object NULL_VALUE = new Object();
    private Map<Class<? extends Component>, ComponentDef> componentDefinitions = new HashMap<Class<? extends Component>, ComponentDef>();

    @Override
    public <T extends Component> T createComponent(EntityRef entity, Class<T> clazz) {
        ComponentDef componentDef = getComponentDef(clazz);
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},
                new ComponentView(entity, clazz, new HashMap<String, Object>(), false, componentDef));
    }

    private <T extends Component> ComponentDef getComponentDef(Class<T> clazz) {
        ComponentDef componentDef = componentDefinitions.get(clazz);
        if (componentDef == null) {
            componentDef = new ComponentDef(clazz);
            componentDefinitions.put(clazz, componentDef);
        }
        return componentDef;
    }

    @Override
    public <T extends Component> T copyComponent(EntityRef entity, T originalComponent) {
        return copyComponentInternal(entity, originalComponent, false, true);
    }

    @Override
    public <T extends Component> T copyComponentUnmodifiable(T originalComponent, boolean useOriginalReference) {
        return copyComponentInternal(null, originalComponent, true, useOriginalReference);
    }

    private <T extends Component> T copyComponentInternal(EntityRef entity, T originalComponent,
                                                          boolean readOnly, boolean useOriginalReference) {
        final ComponentView componentView = extractComponentView(originalComponent);

        Map<String, Object> values = componentView.storedValues;
        if (!useOriginalReference) {
            Map<String, Object> result = new HashMap<String, Object>();
            Map<String, Class<?>> fieldTypes = componentView.componentDef.fieldTypes;
            for (Map.Entry<String, Object> field : values.entrySet()) {
                String fieldName = field.getKey();
                result.put(fieldName, entityComponentFieldHandler.<Object>copyFromEntity(field.getValue(), (Class<Object>) fieldTypes.get(fieldName)));
            }

            values = result;
        }

        //noinspection unchecked
        return (T) Proxy.newProxyInstance(componentView.clazz.getClassLoader(), new Class[]{componentView.clazz},
                new ComponentView(entity, componentView.clazz, values, readOnly, componentView.componentDef));
    }

    @Override
    public <T extends Component> void saveComponent(T originalComponent, T changedComponent) {
        ComponentView destination = extractComponentView(originalComponent);
        ComponentView source = extractComponentView(changedComponent);
        for (Map.Entry<String, Object> changeEntry : source.changes.entrySet()) {
            String fieldName = changeEntry.getKey();
            Object fieldValue = changeEntry.getValue();

            Object oldValue = destination.storedValues.get(fieldName);
            Class<?> fieldClass = destination.componentDef.getFieldTypes().get(fieldName);

            if (fieldValue == NULL_VALUE)
                destination.storedValues.remove(fieldName);
            else
                destination.storedValues.put(fieldName, entityComponentFieldHandler.<Object>storeIntoEntity(oldValue, fieldValue, (Class<Object>) fieldClass));
        }

        source.changes.clear();
    }

    @Override
    public void invalidateComponent(Component component) {
        extractComponentView(component).invalidate();
    }

    @Override
    public boolean hasSameValues(Component component1, Component component2) {
        ComponentView componentView1 = extractComponentView(component1);
        ComponentView componentView2 = extractComponentView(component2);
        return componentView1.clazz == componentView2.clazz
                && createConsolidatedFieldMap(componentView1).
                equals(createConsolidatedFieldMap(componentView2));
    }

    @Override
    public EntityRef getComponentEntity(Component component) {
        return extractComponentView(component).entity;
    }

    @Override
    public <T extends Component> Class<T> getComponentClass(T component) {
        //noinspection unchecked
        return (Class<T>) extractComponentView(component).clazz;
    }

    @Override
    public Map<String, Class<?>> getComponentFieldTypes(Component component) {
        Class<Component> clazz = getComponentClass(component);
        return Collections.unmodifiableMap(getComponentDef(clazz).getFieldTypes());
    }

    @Override
    public Map<String, Class<?>> getComponentFieldTypes(Class<? extends Component> component) {
        return Collections.unmodifiableMap(getComponentDef(component).getFieldTypes());
    }

    @Override
    public Map<String, Class<?>> getComponentFieldContainedClasses(Class<? extends Component> component) {
        return Collections.unmodifiableMap(getComponentDef(component).getFieldContainers());
    }

    @Override
    public <T> T getComponentFieldValue(Component component, String fieldName, Class<T> clazz) {
        //noinspection unchecked
        return (T) extractComponentView(component).storedValues.get(fieldName);
    }

    @Override
    public void setComponentFieldValue(Component component, String fieldName, Object fieldValue) {
        ComponentView componentView = extractComponentView(component);
        componentView.storedValues.put(fieldName, fieldValue);
    }

    private Map<String, Object> createConsolidatedFieldMap(ComponentView componentView) {
        Map<String, Object> values = new HashMap<String, Object>();

        values.putAll(componentView.storedValues);

        for (Map.Entry<String, Object> changedEntry : componentView.changes.entrySet()) {
            if (changedEntry.getValue() == NULL_VALUE)
                values.remove(changedEntry.getKey());
            else
                values.put(changedEntry.getKey(), changedEntry.getValue());
        }
        return values;
    }

    private <T extends Component> ComponentView extractComponentView(T originalComponent) {
        return (ComponentView) Proxy.getInvocationHandler(originalComponent);
    }

    private String getFieldName(String methodName, int startIndex) {
        return methodName.substring(startIndex, startIndex + 1).toLowerCase() + methodName.substring(startIndex + 1);
    }

    private class ComponentDef {
        private Map<String, Class<?>> fieldTypes = new HashMap<String, Class<?>>();
        private Map<String, Class<?>> fieldContainers = new HashMap<String, Class<?>>();
        private Map<String, MethodHandler> handlerMap = new HashMap<String, MethodHandler>();

        private ComponentDef(Class<? extends Component> clazz) {
            processDeclaredMethods(clazz);
        }

        private void processDeclaredMethods(Class<?> clazz) {
            for (Method method : clazz.getDeclaredMethods()) {
                Class<?> containedClass = null;
                Container annotation = method.getAnnotation(Container.class);
                if (annotation != null)
                    containedClass = annotation.value();
                String methodName = method.getName();
                if (methodName.startsWith("get")) {
                    addGetMethod(method, getFieldName(methodName, 3), containedClass);
                } else if (methodName.startsWith("is")) {
                    addGetMethod(method, getFieldName(methodName, 2), containedClass);
                } else if (methodName.startsWith("set")) {
                    String fieldName = getFieldName(methodName, 3);
                    addFieldType(fieldName, method.getParameterTypes()[0], containedClass);
                    handlerMap.put(methodName, new SetMethodHandler(fieldName));
                } else {
                    throw new IllegalStateException("Invalid component definition, component uses unrecognized method name: " + methodName);
                }
            }
            for (Class<?> parentInterface : clazz.getInterfaces()) {
                processDeclaredMethods(parentInterface);
            }
        }

        private void addGetMethod(Method method, String fieldName, Class<?> containedClass) {
            final Class<?> fieldType = method.getReturnType();

            addFieldType(fieldName, fieldType, containedClass);
            handlerMap.put(method.getName(), new GetMethodHandler(fieldName, fieldType));
        }

        private void addFieldType(String fieldName, Class<?> fieldType, Class<?> containedClass) {
            final Class<?> existingType = fieldTypes.get(fieldName);
            if (existingType != null) {
                if (existingType != fieldType) {
                    throw new IllegalStateException("Invalid component definition, field " + fieldName + " uses different value types");
                }
            } else {
                fieldContainers.put(fieldName, containedClass);
                fieldTypes.put(fieldName, fieldType);
            }
        }

        private Map<String, Class<?>> getFieldTypes() {
            return fieldTypes;
        }

        public Map<String, Class<?>> getFieldContainers() {
            return fieldContainers;
        }
    }

    private class ComponentView implements InvocationHandler {
        private EntityRef entity;
        private Class<? extends Component> clazz;
        private Map<String, Object> storedValues;
        private Map<String, Object> changes = new HashMap<String, Object>();
        private Map<String, MethodHandler> handlers = new HashMap<String, MethodHandler>();
        private boolean readOnly;
        private ComponentDef componentDef;
        private boolean invalid;

        private ComponentView(EntityRef entity, Class<? extends Component> clazz, Map<String, Object> storedValues,
                              boolean readOnly, ComponentDef componentDef) {
            this.entity = entity;
            this.clazz = clazz;
            this.storedValues = storedValues;
            this.readOnly = readOnly;
            this.componentDef = componentDef;
            this.handlers = componentDef.handlerMap;
        }

        public void invalidate() {
            invalid = true;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (invalid)
                throw new IllegalStateException("Attempted to invoke a method on a Component that had it's state saved");

            MethodHandler methodHandler = handlers.get(method.getName());
            if (methodHandler != null)
                return methodHandler.handleInvocation(proxy, method, storedValues, changes, readOnly, args);
            throw new UnsupportedOperationException("Component method invoked without property defined: " + clazz.getName() + ":" + method.getName());
        }
    }

    private class GetMethodHandler implements MethodHandler {
        private String fieldName;
        private Class<?> resultClass;

        public GetMethodHandler(String fieldName, Class<?> resultClass) {
            this.fieldName = fieldName;
            this.resultClass = resultClass;
        }

        @Override
        public Object handleInvocation(Object proxy, Method method, Map<String, Object> storedValues, Map<String, Object> changes, boolean readOnly, Object[] args) {
            final Object changedValue = changes.get(fieldName);
            if (changedValue != null) {
                if (changedValue == NULL_VALUE) {
                    return convertToResult(proxy, method, null, resultClass);
                } else {
                    return convertToResult(proxy, method, changedValue, resultClass);
                }
            } else {
                Object value = storedValues.get(fieldName);
                if (value != null)
                    value = entityComponentFieldHandler.copyFromEntity(value, (Class<Object>) resultClass);
                return convertToResult(proxy, method, value, resultClass);
            }
        }

        private Object getDefaultValue(Object proxy, Method method, Class<?> resultClass) {
            DefaultValue defaultValue = method.getAnnotation(DefaultValue.class);
            if (defaultValue != null) {
                if (resultClass == String.class)
                    return defaultValue.value();

                if (resultClass.isPrimitive()) {
                    if (resultClass == boolean.class) {
                        return Boolean.parseBoolean(defaultValue.value());
                    } else if (resultClass == float.class) {
                        return Float.parseFloat(defaultValue.value());
                    } else if (resultClass == double.class) {
                        return Double.parseDouble(defaultValue.value());
                    } else if (resultClass == long.class) {
                        return Long.parseLong(defaultValue.value());
                    } else if (resultClass == int.class) {
                        return Integer.parseInt(defaultValue.value());
                    } else if (resultClass == short.class) {
                        return Short.parseShort(defaultValue.value());
                    } else if (resultClass == char.class) {
                        return defaultValue.value().charAt(0);
                    } else if (resultClass == byte.class) {
                        return Byte.parseByte(defaultValue.value());
                    }
                    throw new IllegalStateException("Unable to find default value for this type");
                } else if (componentFieldConverter.hasConverterForType(resultClass)) {
                    return componentFieldConverter.convertTo(defaultValue.value(), resultClass);
                }
                throw new IllegalStateException("@DefaultValue specified but cannot convert via known means");
            } else {
                if (resultClass.isPrimitive()) {
                    if (resultClass == boolean.class) {
                        return false;
                    } else if (resultClass == float.class) {
                        return 0f;
                    } else if (resultClass == double.class) {
                        return 0d;
                    } else if (resultClass == long.class) {
                        return 0L;
                    } else if (resultClass == int.class) {
                        return 0;
                    } else if (resultClass == short.class) {
                        return (short) 0;
                    } else if (resultClass == char.class) {
                        return (char) 0;
                    } else if (resultClass == byte.class) {
                        return (byte) 0;
                    }
                    throw new IllegalStateException("Unable to find default value for this type");
                } else if (componentFieldConverter.hasConverterForType(resultClass)) {
                    return componentFieldConverter.getDefaultValue(resultClass);
                }
                return null;
            }
        }

        private Object convertToResult(Object proxy, Method method, Object value, Class<?> resultClass) {
            if (value == null)
                return getDefaultValue(proxy, method, resultClass);
            if (resultClass.isPrimitive() || resultClass.isAssignableFrom(Number.class)) {
                if (resultClass == boolean.class || resultClass == Boolean.class) {
                    return value;
                }
                Number numberValue = (Number) value;
                if (resultClass == float.class || resultClass == Float.class) {
                    return numberValue.floatValue();
                } else if (resultClass == double.class || resultClass == Double.class) {
                    return numberValue.doubleValue();
                } else if (resultClass == long.class || resultClass == Long.class) {
                    return numberValue.longValue();
                } else if (resultClass == int.class || resultClass == Integer.class) {
                    return numberValue.intValue();
                } else if (resultClass == short.class || resultClass == Short.class) {
                    return numberValue.shortValue();
                } else if (resultClass == char.class || resultClass == Character.class) {
                    return (char) numberValue.intValue();
                } else if (resultClass == byte.class || resultClass == Byte.class) {
                    return numberValue.byteValue();
                }
            }
            return value;
        }
    }

    private static class SetMethodHandler implements MethodHandler {
        private String fieldName;

        public SetMethodHandler(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public Object handleInvocation(Object proxy, Method method, Map<String, Object> storedValues, Map<String, Object> changes, boolean readOnly, Object[] args) {
            if (readOnly)
                throw new UnsupportedOperationException("This is a read only component");
            if (args[0] == null) {
                args[0] = NULL_VALUE;
            }
            changes.put(fieldName, args[0]);
            return null;
        }
    }

    private interface MethodHandler {
        Object handleInvocation(Object proxy, Method method, Map<String, Object> storedValues, Map<String, Object> changes, boolean readOnly, Object[] args);
    }
}
