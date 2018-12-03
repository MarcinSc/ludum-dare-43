package com.gempukku.ld43.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gempukku.ld43.model.ParticleEffectComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;
import com.gempukku.secsy.gaming.time.TimeManager;

import java.util.HashMap;
import java.util.Map;

@RegisterSystem
public class ParticleRenderer extends AbstractLifeCycleSystem {
    @Inject
    private EntityManager entityManager;
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private TimeManager timeManager;

    private Map<String, ParticleEffect> particleEffectMap = new HashMap<String, ParticleEffect>();
    private EntityIndex particleEffects;
    private SpriteBatch spriteBatch;

    @Override
    public void initialize() {
        particleEffects = entityIndexManager.addIndexOnComponents(ParticleEffectComponent.class);
        spriteBatch = new SpriteBatch();
    }


    @ReceiveEvent(priority = -4)
    public void renderParticles(RenderToPipeline renderToPipeline, EntityRef camera) {
        float delta = timeManager.getTimeSinceLastUpdate() / 1000f;

        renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();
        spriteBatch.setProjectionMatrix(renderToPipeline.getCamera().combined);
        spriteBatch.begin();
        for (EntityRef particleEffectEntity : particleEffects) {
            ParticleEffectComponent effect = particleEffectEntity.getComponent(ParticleEffectComponent.class);
            Position2DComponent position = particleEffectEntity.getComponent(Position2DComponent.class);
            ParticleEffect pe = effect.getParticleEffect();
            if (pe == null) {
                pe = new ParticleEffect(getParticleEffect(effect.getPath()));
                pe.start();
                pe.setPosition(position.getX(), position.getY());
                pe.scaleEffect(0.005f);
                effect.setParticleEffect(pe);
                Color color = effect.getColor();
                for (ParticleEmitter emitter : pe.getEmitters()) {
                    float[] colors = emitter.getTint().getColors();
                    colors[0] = color.r;
                    colors[1] = color.g;
                    colors[2] = color.b;
                }

                particleEffectEntity.saveChanges();
            }
            if (pe.isComplete() && effect.isDestroyOnEffectEnd()) {
                entityManager.destroyEntity(particleEffectEntity);
            }
            if (!pe.isComplete())
                pe.draw(spriteBatch, delta);
        }
        spriteBatch.end();
        renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
    }

    private ParticleEffect getParticleEffect(String path) {
        ParticleEffect particleEffect = particleEffectMap.get(path);
        if (particleEffect == null) {
            particleEffect = new ParticleEffect();
            particleEffect.load(Gdx.files.internal(path), Gdx.files.internal(""));
            particleEffectMap.put(path, particleEffect);
        }
        return particleEffect;
    }

    @Override
    public void destroy() {
        for (ParticleEffect value : particleEffectMap.values()) {
            value.dispose();
        }
        spriteBatch.dispose();
    }
}
