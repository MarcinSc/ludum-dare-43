package com.gempukku.secsy.gaming.ai.task.movement;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.ai.AIEngine;
import com.gempukku.secsy.gaming.component.GroundedComponent;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.physics.basic2d.MovingComponent;

@RegisterSystem(profiles = {"ai", "aiMovement"})
public class AIMovementSystem extends AbstractLifeCycleSystem {
    @Inject
    private AIEngine aiEngine;
    @Inject
    private EntityIndexManager entityIndexManager;
    private EntityIndex movingEntities;

    @Override
    public void initialize() {
        movingEntities = entityIndexManager.addIndexOnComponents(AIApplyMovementIfPossibleComponent.class);
    }

    @ReceiveEvent
    public void aiCantMove(AICantMove aiCantMove, EntityRef entity, AIApplyMovementIfPossibleComponent applyMovement) {
        notifyCantMove(entity);
    }

    private void notifyCantMove(EntityRef entity) {
        for (MoveInDirectionUntilCannotTask moveInDirectionUntilCannotTask : aiEngine.getRunningTasksOfType(entity, MoveInDirectionUntilCannotTask.class)) {
            moveInDirectionUntilCannotTask.notifyCantMove(aiEngine.getReference(entity));
        }
    }

    @ReceiveEvent
    public void setMovementSpeed(GameLoopUpdate gameLoopUpdate) {
        for (EntityRef movingEntity : movingEntities) {
            GroundedComponent grounded = movingEntity.getComponent(GroundedComponent.class);
            if (grounded.isGrounded()) {
                HorizontalOrientationComponent horizontalOrientation = movingEntity.getComponent(HorizontalOrientationComponent.class);
                AIMovementConfigurationComponent aiMovementConfiguration = movingEntity.getComponent(AIMovementConfigurationComponent.class);
                MovingComponent moving = movingEntity.getComponent(MovingComponent.class);
                boolean facingRight = horizontalOrientation.isFacingRight();
                float speedX = aiMovementConfiguration.getMovementVelocity();
                moving.setSpeedX(facingRight ? speedX : -speedX);
                movingEntity.saveChanges();
            }
        }
    }
}
