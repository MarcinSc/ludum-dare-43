package com.gempukku.secsy.gaming;

import com.badlogic.gdx.ApplicationAdapter;
import com.gempukku.secsy.context.SECSyContext;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.ReflectionsAnnotatedTypesSystemProducer;
import com.gempukku.secsy.entity.game.InternalGameLoop;
import com.gempukku.secsy.gaming.physics.PhysicsSystem;
import com.gempukku.secsy.gaming.rendering.RenderingSystem;
import com.gempukku.secsy.gaming.time.InternalTimeManager;
import com.google.common.base.Predicate;
import org.reflections.Reflections;

import javax.annotation.Nullable;
import java.util.Set;

public class SecsyGameApplication extends ApplicationAdapter {
    private int width;
    private int height;

    private SECSyContext context;
    private long lastUpdateTime;

    public SecsyGameApplication(int width, int height, final Set<String> activeProfiles) {
        this.width = width;
        this.height = height;

        ReflectionsAnnotatedTypesSystemProducer systemProducer = new ReflectionsAnnotatedTypesSystemProducer(
                RegisterSystem.class,
                new Predicate<Class<?>>() {
                    @Override
                    public boolean apply(@Nullable Class<?> aClass) {
                        RegisterSystem annotation = aClass.getAnnotation(RegisterSystem.class);
                        if (annotation == null)
                            return false;
                        return hasAllProfiles(annotation.profiles(), activeProfiles);
                    }
                });
        systemProducer.scanReflections(new Reflections());
        context = new SECSyContext(systemProducer);
        context.setDebug(true);
    }

    @Override
    public void create() {
        context.startup();

        lastUpdateTime = System.currentTimeMillis();
    }

    private boolean hasAllProfiles(String[] requiredProfiles, Set<String> activeProfiles) {
        for (String systemProfile : requiredProfiles) {
            if (!activeProfiles.contains(systemProfile))
                return false;
        }

        return true;
    }

    @Override
    public void render() {
        long currentTime = System.currentTimeMillis();
        long timeDiff = System.currentTimeMillis() - lastUpdateTime;
        lastUpdateTime = currentTime;

        InternalTimeManager internalTimeManager = context.getSystem(InternalTimeManager.class);
        internalTimeManager.updateTime(timeDiff);

        // Basic game loop - first notify all systems about the game tick
        InternalGameLoop internalGameLoop = context.getSystem(InternalGameLoop.class);
        internalGameLoop.processUpdate();

        // Process physics (based on input)
        PhysicsSystem physicsSystem = context.getSystem(PhysicsSystem.class);
        if (physicsSystem != null)
            physicsSystem.processPhysics();

        // Render result
        RenderingSystem renderingSystem = context.getSystem(RenderingSystem.class);
        renderingSystem.render(width, height);
    }

    @Override
    public void dispose() {
        context.shutdown();
    }
}
