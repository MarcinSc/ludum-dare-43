package com.gempukku.secsy.gaming.input2d;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.component.GroundedComponent;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.physics.box2d.Box2dPhysics;
import com.gempukku.secsy.gaming.physics.box2d.Box2dSensorContactBegin;
import com.gempukku.secsy.gaming.physics.box2d.Box2dSensorContactEnd;

@RegisterSystem(profiles = {"platformer2dMovement", "box2dPhysics"})
public class PlatformerBox2dMovementSystem extends AbstractLifeCycleSystem {
    public static final String GROUNDED_SENSOR_NAME = "groundSensor";

    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private Box2dPhysics physicsEngine;
    @Inject
    private InputScheme2dProvider inputScheme2DProvider;

    private EntityIndex controlledEntities;
    private boolean jumpPressedLastFrame;

    @Override
    public void initialize() {
        controlledEntities = entityIndexManager.addIndexOnComponents(ControlledByInputComponent.class);
    }

    @ReceiveEvent
    public void sensorContactBegin(Box2dSensorContactBegin contactBegin, EntityRef entity, ControlledByInputComponent controlled) {
        if (contactBegin.getSensorFixture().getUserData().equals(GROUNDED_SENSOR_NAME)) {
            controlled.setJumpCount(0);
            entity.saveChanges();
        }
    }

    @ReceiveEvent
    public void sensorContactBegin(Box2dSensorContactBegin contactBegin, EntityRef entity, GroundedComponent controlled) {
        if (contactBegin.getSensorFixture().getUserData().equals(GROUNDED_SENSOR_NAME)) {
            controlled.setGrounded(true);
            entity.saveChanges();
        }
    }

    @ReceiveEvent
    public void sensorContactEnd(Box2dSensorContactEnd contactEnd, EntityRef entity, GroundedComponent controlled) {
        if (contactEnd.getSensorFixture().getUserData().equals(GROUNDED_SENSOR_NAME)) {
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
                    physicsEngine.setSpeedY(controlledEntity, 0);
                    physicsEngine.applyPulse(controlledEntity, 0, controlled.getJumpImpulse());
                    controlled.setJumpCount(jumpCount + 1);
                    controlledEntity.saveChanges();
                }
            }
            jumpPressedLastFrame = true;
        } else if (jumpPressedLastFrame && !jumpActivated) {
            jumpPressedLastFrame = false;
        }

        if (rightActivated && !leftActivated) {
            for (EntityRef controlledEntity : controlledEntities.getEntities()) {
                ControlledByInputComponent controlled = controlledEntity.getComponent(ControlledByInputComponent.class);
                physicsEngine.setSpeedX(controlledEntity, controlled.getMoveSpeed());
                HorizontalOrientationComponent horizontalOrientation = controlledEntity.getComponent(HorizontalOrientationComponent.class);
                if (!horizontalOrientation.isFacingRight()) {
                    horizontalOrientation.setFacingRight(true);
                    controlledEntity.saveChanges();
                }
            }
        } else if (leftActivated && !rightActivated) {
            for (EntityRef controlledEntity : controlledEntities.getEntities()) {
                ControlledByInputComponent controlled = controlledEntity.getComponent(ControlledByInputComponent.class);
                physicsEngine.setSpeedX(controlledEntity, -controlled.getMoveSpeed());
                HorizontalOrientationComponent horizontalOrientation = controlledEntity.getComponent(HorizontalOrientationComponent.class);
                if (horizontalOrientation.isFacingRight()) {
                    horizontalOrientation.setFacingRight(false);
                    controlledEntity.saveChanges();
                }
            }
        } else {
            for (EntityRef controlledEntity : controlledEntities.getEntities()) {
                physicsEngine.setSpeedX(controlledEntity, 0);
            }
        }
    }
}
