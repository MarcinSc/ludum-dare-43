package com.gempukku.ld43.logic.damage;

import com.gempukku.ld43.logic.level.EntityOutOfBounds;
import com.gempukku.ld43.logic.level.PlayerDied;
import com.gempukku.ld43.model.DestroyOnCollisionComponent;
import com.gempukku.ld43.model.DustBunnyComponent;
import com.gempukku.ld43.model.DustComponent;
import com.gempukku.ld43.model.PlayerComponent;
import com.gempukku.ld43.render.RenderText;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.particle2d.ParticleEngine;
import com.gempukku.secsy.gaming.physics.basic2d.EntityCollided;
import com.gempukku.secsy.gaming.time.TimeEntityProvider;

import java.util.Iterator;

@RegisterSystem
public class DeathSystem extends AbstractLifeCycleSystem {
    @Inject
    private TimeEntityProvider timeEntityProvider;
    @Inject
    private EntityManager entityManager;
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private ParticleEngine particleEngine;

    private EntityIndex dustBunnies;

    @Override
    public void initialize() {
        dustBunnies = entityIndexManager.addIndexOnComponents(DustBunnyComponent.class);
    }

    @ReceiveEvent
    public void playerDamaged(EntityDamaged entityDamaged, EntityRef entity, PlayerComponent player) {
        playerDeath(entity);
    }

    @ReceiveEvent
    public void playerOutOfBounds(EntityOutOfBounds entityOutOfBounds, EntityRef entity, PlayerComponent player) {
        playerDeath(entity);
    }

    @ReceiveEvent
    public void dustBunnyOutOfBounds(EntityOutOfBounds entityOutOfBounds, EntityRef entity, DustBunnyComponent dustBunny) {
        entityManager.destroyEntity(entity);
    }

    @ReceiveEvent
    public void checkForTooManyBunnies(GameLoopUpdate gameLoopUpdate) {
        int count = 0;
        for (EntityRef dustBunny : dustBunnies) {
            count++;
        }

        if (count > 50) {
            Iterator<EntityRef> iterator = entityManager.getEntitiesWithComponents(PlayerComponent.class).iterator();
            if (iterator.hasNext()) {
                EntityRef player = iterator.next();
                player.send(new RenderText("The house has too much dust, try again.", 5000));
                player.send(new PlayerDied());
            }
        }
    }

    @ReceiveEvent
    public void dustBunnyDamaged(EntityDamaged entityDamaged, EntityRef entity, DustBunnyComponent dustBunny, DustComponent dust, Position2DComponent position) {
        particleEngine.addParticleEffect("particles/explosion.p", position.getX(), position.getY(), dust.getColor(), null);
        entityManager.destroyEntity(entity);
    }

    @ReceiveEvent
    public void destroyOnCollision(EntityCollided entityCollided, EntityRef entity, DestroyOnCollisionComponent destroyOnCollision, Position2DComponent position) {
        particleEngine.addParticleEffect(destroyOnCollision.getParticleEffect(), position.getX(), position.getY(), destroyOnCollision.getParticleColor(), null);
        entityManager.destroyEntity(entity);
    }

    private void playerDeath(EntityRef player) {
        player.send(new RenderText("You've been home all day, and you did not\neven clean the house?", 5000));
        player.send(new PlayerDied());
    }
}
