package com.gempukku.secsy.gaming.easing;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.JavaPackageProvider;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RegisterSystem(profiles = "easing", shared = EasingResolver.class)
public class ReflectionsEasingResolver extends AbstractLifeCycleSystem implements EasingResolver {
    @Inject
    private JavaPackageProvider javaPackageProvider;

    private Map<String, EasingFunction> easingFunctionMap = new HashMap<String, EasingFunction>();

    @Override
    public void initialize() {
        scanInterpolations();

        Set<URL> contextLocations = new HashSet<URL>();
        for (String javaPackage : javaPackageProvider.getJavaPackages()) {
            contextLocations.addAll(ClasspathHelper.forPackage(javaPackage, ClasspathHelper.contextClassLoader()));
        }

        Configuration scanConfiguration = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner())
                .setUrls(contextLocations);

        Reflections reflections = new Reflections(scanConfiguration);
        Set<Class<? extends EasingFunction>> easingFunctions = reflections.getSubTypesOf(EasingFunction.class);
        for (Class<? extends EasingFunction> easingFunction : easingFunctions) {
            if (easingFunction != InterpolationEasingFunction.class) {
                EasingFunction easingFunctionObj = null;
                try {
                    easingFunctionObj = easingFunction.newInstance();
                    easingFunctionMap.put(easingFunctionObj.getFunctionTrigger(), easingFunctionObj);
                } catch (InstantiationException e) {
                    throw new RuntimeException("Unable to instantiate EasingFunction", e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Unable to instantiate EasingFunction", e);
                }
            }
        }
    }

    private void scanInterpolations() {
        for (Field field : Interpolation.class.getDeclaredFields()) {
            if (Interpolation.class.isAssignableFrom(field.getType())) {
                try {
                    String name = field.getName();
                    addInterpolationToMap(name, (Interpolation) field.get(null));
                } catch (IllegalAccessException exp) {
                    throw new RuntimeException("Unable to get Interpolation", exp);
                }
            }
        }

    }

    private void addInterpolationToMap(String trigger, Interpolation interpolation) {
        easingFunctionMap.put(trigger, new InterpolationEasingFunction(trigger, interpolation));
    }

    @Override
    public float resolveValue(String recipe, float value) {
        String[] recipeChain = recipe.split(",");
        float result = value;
        for (String trigger : recipeChain) {
            String parameter = null;
            int openingBracket = trigger.indexOf("(");
            int closingBracket = trigger.lastIndexOf(")");
            if (openingBracket > 0 && closingBracket > openingBracket) {
                parameter = trigger.substring(openingBracket + 1, closingBracket);
                trigger = trigger.substring(0, openingBracket);
            }
            result = easingFunctionMap.get(trigger).evaluateFunction(parameter, MathUtils.clamp(result, 0, 1));
        }
        return result;
    }

    @Override
    public float resolveValue(EasedValue easedValue, float value) {
        if (easedValue.getRecipe() == null)
            return easedValue.getMultiplier();
        return easedValue.getMultiplier() * resolveValue(easedValue.getRecipe(), value);
    }

    private class InterpolationEasingFunction implements EasingFunction {
        private String trigger;
        private Interpolation interpolation;

        public InterpolationEasingFunction(String trigger, Interpolation interpolation) {
            this.trigger = trigger;
            this.interpolation = interpolation;
        }

        @Override
        public String getFunctionTrigger() {
            return trigger;
        }

        @Override
        public float evaluateFunction(String parameter, float input) {
            return interpolation.apply(input);
        }
    }
}
