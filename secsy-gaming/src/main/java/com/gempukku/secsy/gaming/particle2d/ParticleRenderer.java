package com.gempukku.secsy.gaming.particle2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;
import com.gempukku.secsy.gaming.time.TimeManager;

import java.util.*;

@RegisterSystem(profiles = "particle2d", shared = ParticleEngine.class)
public class ParticleRenderer extends AbstractLifeCycleSystem implements ParticleEngine {
    @Inject
    private EntityManager entityManager;
    @Inject
    private TimeManager timeManager;

    private Map<String, ParticleEffect> loadedEffectMap = new HashMap<String, ParticleEffect>();
    private List<ActiveParticleEffect> activeParticleEffects = new LinkedList<ActiveParticleEffect>();
    private SpriteBatch spriteBatch;

    @Override
    public void initialize() {
        spriteBatch = new SpriteBatch();
    }

    @Override
    public void addParticleEffect(String path, float x, float y, Color color, CompletionCallback callback) {
        ParticleEffect original = getParticleEffect(path);
        ParticleEffect copy = new ParticleEffect(original);
        copy.setPosition(x, y);
        if (color != null) {
            for (ParticleEmitter particleEmitter : copy.getEmitters()) {
                float[] colors = particleEmitter.getTint().getColors();
                colors[0] = color.r;
                colors[1] = color.g;
                colors[2] = color.b;
            }
        }
        copy.scaleEffect(0.005f);
        copy.start();
        activeParticleEffects.add(new ActiveParticleEffect(path, copy, callback));
    }

    @ReceiveEvent(priorityName = "gaming.renderer.particle")
    public void renderParticles(RenderToPipeline renderToPipeline, EntityRef camera) {
        if (!activeParticleEffects.isEmpty()) {
            float delta = timeManager.getTimeSinceLastUpdate() / 1000f;

            renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();
            spriteBatch.setProjectionMatrix(renderToPipeline.getCamera().combined);
            spriteBatch.begin();
            Iterator<ActiveParticleEffect> iterator = activeParticleEffects.iterator();
            while (iterator.hasNext()) {
                ActiveParticleEffect pe = iterator.next();
                if (pe.particleEffect.isComplete()) {
                    if (pe.callback != null)
                        pe.callback.effectEnded(pe.path);
                    pe.particleEffect.dispose();
                    iterator.remove();
                } else {
                    pe.particleEffect.draw(spriteBatch, delta);
                }
            }
            spriteBatch.end();
            renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
        }
    }

    private ParticleEffect getParticleEffect(String path) {
        ParticleEffect particleEffect = loadedEffectMap.get(path);
        if (particleEffect == null) {
            particleEffect = new ParticleEffect();
            particleEffect.load(Gdx.files.internal(path), Gdx.files.internal(""));
            loadedEffectMap.put(path, particleEffect);
        }
        return particleEffect;
    }

    @Override
    public void destroy() {
        for (ParticleEffect value : loadedEffectMap.values()) {
            value.dispose();
        }
        spriteBatch.dispose();
    }

    private class ActiveParticleEffect {
        private String path;
        private ParticleEffect particleEffect;
        private CompletionCallback callback;

        public ActiveParticleEffect(String path, ParticleEffect particleEffect, CompletionCallback callback) {
            this.path = path;
            this.particleEffect = particleEffect;
            this.callback = callback;
        }
    }
}
