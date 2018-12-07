package com.gempukku.secsy.gaming.input2d;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.event.Event;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.component.GroundComponent;
import com.gempukku.secsy.gaming.component.GroundedComponent;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.physics.basic2d.MovingComponent;
import com.gempukku.secsy.gaming.physics.basic2d.SensorContactBegin;
import com.gempukku.secsy.gaming.physics.basic2d.SensorContactEnd;

@RegisterSystem(profiles = {"platformer2dMovement", "basic2dPhysics"})
public class PlatformerBasic2dMovementSystem extends AbstractLifeCycleSystem {
    public static final String GROUNDED_SENSOR_NAME = "groundSensor";

    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private InputScheme2dProvider inputScheme2DProvider;

    private EntityIndex controlledEntities;
    private boolean jumpPressedLastFrame;
    private boolean movingLastFrame;

    @Override
    public void initialize() {
        controlledEntities = entityIndexManager.addIndexOnComponents(ControlledByInputComponent.class);
    }

    @ReceiveEvent
    public void sensorContactBegin(SensorContactBegin contactBegin, EntityRef entity, ControlledByInputComponent controlled) {
        if (contactBegin.getSensorType().equals(GROUNDED_SENSOR_NAME)
                && contactBegin.getSensorTrigger().hasComponent(GroundComponent.class)) {
            controlled.setJumpCount(0);
            entity.saveChanges();
        }
    }

    @ReceiveEvent
    public void sensorContactBegin(SensorContactBegin contactBegin, EntityRef entity, GroundedComponent controlled) {
        if (contactBegin.getSensorType().equals(GROUNDED_SENSOR_NAME)
                && contactBegin.getSensorTrigger().hasComponent(GroundComponent.class)) {
            controlled.setGrounded(true);
            entity.saveChanges();
            entity.send(new EntityLanded());
        }
    }

    @ReceiveEvent
    public void sensorContactEnd(SensorContactEnd contactEnd, EntityRef entity, GroundedComponent controlled) {
        if (contactEnd.getSensorType().equals(GROUNDED_SENSOR_NAME)
                && contactEnd.getSensorTrigger().hasComponent(GroundComponent.class)) {
            controlled.setGrounded(false);
            entity.saveChanges();
        }
    }

    @ReceiveEvent
    public void processInput(GameLoopUpdate gameLoopUpdate) {
        boolean jumpActivated = inputScheme2DProvider.isJumpActivated();
        boolean rightActivated = inputScheme2DProvider.isRightActivated();
        boolean leftActivated = inputScheme2DProvider.isLeftActivated();

        if (!jumpPressedLastFrame && jumpActivated) {
            for (EntityRef controlledEntity : controlledEntities.getEntities()) {
                ControlledByInputComponent controlled = controlledEntity.getComponent(ControlledByInputComponent.class);
                GroundedComponent grounded = controlledEntity.getComponent(GroundedComponent.class);
                // Character can jump, if it's either grounded, or has already jumped and maxJumpCount allows that
                int jumpCount = controlled.getJumpCount();
                if (grounded.isGrounded() || (jumpCount > 0 && jumpCount < controlled.getJumpMaxCount())) {
                    MovingComponent moving = controlledEntity.getComponent(MovingComponent.class);
                    moving.setSpeedY(controlled.getJumpSpeed());
                    controlled.setJumpCount(jumpCount + 1);
                    controlledEntity.saveChanges();

                    controlledEntity.send(new EntityJumped(jumpCount + 1));
                }
            }
            jumpPressedLastFrame = true;
        } else if (jumpPressedLastFrame && !jumpActivated) {
            jumpPressedLastFrame = false;
        }

        boolean movedBefore = movingLastFrame;

        if (rightActivated && !leftActivated) {
            for (EntityRef controlledEntity : controlledEntities.getEntities()) {
                ControlledByInputComponent controlled = controlledEntity.getComponent(ControlledByInputComponent.class);
                MovingComponent moving = controlledEntity.getComponent(MovingComponent.class);
                moving.setSpeedX(controlled.getMoveSpeed());
                HorizontalOrientationComponent horizontalOrientation = controlledEntity.getComponent(HorizontalOrientationComponent.class);
                if (!horizontalOrientation.isFacingRight()) {
                    horizontalOrientation.setFacingRight(true);
                }
                controlledEntity.saveChanges();
            }
            movingLastFrame = true;
        } else if (leftActivated && !rightActivated) {
            for (EntityRef controlledEntity : controlledEntities.getEntities()) {
                ControlledByInputComponent controlled = controlledEntity.getComponent(ControlledByInputComponent.class);
                MovingComponent moving = controlledEntity.getComponent(MovingComponent.class);
                moving.setSpeedX(-controlled.getMoveSpeed());
                HorizontalOrientationComponent horizontalOrientation = controlledEntity.getComponent(HorizontalOrientationComponent.class);
                if (horizontalOrientation.isFacingRight()) {
                    horizontalOrientation.setFacingRight(false);
                }
                controlledEntity.saveChanges();
            }
            movingLastFrame = true;
        } else {
            for (EntityRef controlledEntity : controlledEntities.getEntities()) {
                MovingComponent moving = controlledEntity.getComponent(MovingComponent.class);
                moving.setSpeedX(0);
                controlledEntity.saveChanges();
            }
            movingLastFrame = false;
        }

        if (movedBefore != movingLastFrame) {
            Event event;
            if (movingLastFrame)
                event = new EntityStartedWalking();
            else
                event = new EntityStoppedWalking();

            for (EntityRef controlledEntity : controlledEntities) {
                controlledEntity.send(event);
            }
        }
    }
}
