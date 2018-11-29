package com.gempukku.secsy.entity.serialization;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.JavaPackageProvider;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RegisterSystem(
        profiles = "reflectionsNameComponentManager",
        shared = NameComponentManager.class)
public class ReflectionsNameComponentManager extends AbstractLifeCycleSystem implements NameComponentManager {
    @Inject
    private JavaPackageProvider javaPackageProvider;

    private Map<String, Class<? extends Component>> componentsByName = new HashMap<String, Class<? extends Component>>();
    private Map<Class<? extends Component>, String> namesByComponent = new HashMap<Class<? extends Component>, String>();

    @Override
    public float getPriority() {
        return 200;
    }

    @Override
    public void initialize() {
        Set<URL> contextLocations = new HashSet<URL>();
        for (String javaPackage : javaPackageProvider.getJavaPackages()) {
            contextLocations.addAll(ClasspathHelper.forPackage(javaPackage, ClasspathHelper.contextClassLoader()));
        }

        Configuration scanConfiguration = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner())
                .setUrls(contextLocations);

        Reflections reflections = new Reflections(scanConfiguration);
        Set<Class<? extends Component>> components = reflections.getSubTypesOf(Component.class);
        for (Class<? extends Component> component : components) {
            String simpleName = component.getSimpleName();
            if (simpleName.endsWith("Component"))
                componentsByName.put(simpleName.substring(0, simpleName.length() - 9), component);
            componentsByName.put(simpleName, component);
            namesByComponent.put(component, simpleName);
        }
    }

    @Override
    public Class<? extends Component> getComponentByName(String name) {
        return componentsByName.get(name);
    }

    @Override
    public String getNameByComponent(Class<? extends Component> componentClass) {
        return namesByComponent.get(componentClass);
    }
}
