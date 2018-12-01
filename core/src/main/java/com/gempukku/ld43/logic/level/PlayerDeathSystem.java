package com.gempukku.ld43.logic.level;

import com.gempukku.ld43.logic.damage.EntityDamaged;
import com.gempukku.ld43.model.PlayerComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.input2d.ControlledByInputComponent;
import com.gempukku.secsy.gaming.time.PausedComponent;
import com.gempukku.secsy.gaming.time.TimeEntityProvider;

@RegisterSystem
public class PlayerDeathSystem {
    @Inject
    private TimeEntityProvider timeEntityProvider;

    @ReceiveEvent
    public void playerDamaged(EntityDamaged entityDamaged, EntityRef entity, PlayerComponent player) {
        playerDeath(entity);
    }

    @ReceiveEvent
    public void playerOutOfBounds(EntityOutOfBounds entityOutOfBounds, EntityRef entity, PlayerComponent player) {
        playerDeath(entity);
    }

    private void playerDeath(EntityRef player) {
        EntityRef timeEntity = timeEntityProvider.getTimeEntity();
        timeEntity.createComponent(PausedComponent.class);
        timeEntity.saveChanges();

        player.removeComponents(ControlledByInputComponent.class);
        player.saveChanges();
    }
}
