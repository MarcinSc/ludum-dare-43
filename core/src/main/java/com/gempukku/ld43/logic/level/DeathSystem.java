package com.gempukku.ld43.logic.level;

import com.gempukku.ld43.logic.damage.EntityDamaged;
import com.gempukku.ld43.model.DustBunnyComponent;
import com.gempukku.ld43.model.PlayerComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.time.TimeEntityProvider;

@RegisterSystem
public class DeathSystem {
    @Inject
    private TimeEntityProvider timeEntityProvider;
    @Inject
    private EntityManager entityManager;

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

    private void playerDeath(EntityRef player) {
        player.send(new PlayerDied());
    }
}
