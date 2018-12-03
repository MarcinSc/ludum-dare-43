package com.gempukku.ld43.logic.dust;

import com.badlogic.gdx.math.MathUtils;
import com.gempukku.ld43.logic.level.LevelSystem;
import com.gempukku.ld43.logic.spawn.SpawnEntity;
import com.gempukku.ld43.model.DustLayerComponent;
import com.gempukku.ld43.model.SweeperComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.component.GroundedComponent;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.physics.basic2d.Basic2dPhysics;
import com.gempukku.secsy.gaming.physics.basic2d.EntityMoved;
import com.gempukku.secsy.gaming.time.TimeManager;
import com.google.common.base.Predicate;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Random;

@RegisterSystem
public class DustSystem extends AbstractLifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private TimeManager timeManager;
    @Inject
    private Basic2dPhysics basic2dPhysics;

    private EntityIndex dustLayerEntities;
    private Random rnd = new Random();

    @Override
    public void initialize() {
        dustLayerEntities = entityIndexManager.addIndexOnComponents(DustLayerComponent.class);
    }

    @ReceiveEvent
    public void update(GameLoopUpdate gameLoopUpdate) {
        float seconds = timeManager.getTimeSinceLastUpdate() / 1000f;
        for (EntityRef dustLayerEntity : dustLayerEntities.getEntities()) {
            DustLayerComponent dustLayer = dustLayerEntity.getComponent(DustLayerComponent.class);
            float dustGrowthPerSecond = dustLayer.getDustGrowthPerSecond();
            float[] layerDepth = dustLayer.getLayerDepth();

            int spawnedIndex = findSpawnedIndex(layerDepth);
            if (spawnedIndex != -1) {
                float width = dustLayer.getRight() - dustLayer.getLeft();
                Position2DComponent position = dustLayerEntity.getComponent(Position2DComponent.class);
                float x = position.getX() + dustLayer.getLeft() + (spawnedIndex + 0.5f) * width / layerDepth.length;
                float y = position.getY() + dustLayer.getDown();

                Arrays.fill(layerDepth, spawnedIndex - 2, spawnedIndex + 3, 0);
                dustLayerEntity.send(new SpawnEntity(dustLayer.getSpawnedEntity(), x, y));
            } else {
                for (int i = 0; i < layerDepth.length; i++) {
                    float growth = dustGrowthPerSecond * seconds * rnd.nextFloat();
                    layerDepth[i] = Math.min(1f, layerDepth[i] + growth);
                }
            }
            dustLayer.setLayerDepth(layerDepth);
            dustLayerEntity.saveChanges();
        }
    }

    private int findSpawnedIndex(float[] layerDepth) {
        for (int i = 2; i < layerDepth.length - 3; i++) {
            if (layerDepth[i - 2] == 1 && layerDepth[i - 1] == 1 && layerDepth[i] == 1
                    && layerDepth[i + 1] == 1 && layerDepth[i + 2] == 1)
                return i;
        }
        return -1;
    }

    @ReceiveEvent
    public void sweeperMoved(EntityMoved entityMoved, EntityRef sweeperEntity, SweeperComponent sweeper, Position2DComponent sweeperPosition, GroundedComponent grounded) {
        if (grounded.isGrounded()) {
            float x = sweeperPosition.getX();

            Iterable<EntityRef> dustLayersSweeped = basic2dPhysics.getContactsForSensor(sweeperEntity, "groundSensor",
                    new Predicate<EntityRef>() {
                        @Override
                        public boolean apply(@Nullable EntityRef input) {
                            return input.hasComponent(DustLayerComponent.class);
                        }
                    });

            for (EntityRef dustLayerSweeped : dustLayersSweeped) {
                DustLayerComponent dustLayer = dustLayerSweeped.getComponent(DustLayerComponent.class);
                Position2DComponent position = dustLayerSweeped.getComponent(Position2DComponent.class);

                int index = findClosestPileIndex(x, position.getX() + dustLayer.getLeft(), dustLayer.getRight() - dustLayer.getLeft());

                float[] layerDepth = dustLayer.getLayerDepth();
                layerDepth[index] = 0;
                dustLayer.setLayerDepth(layerDepth);
                dustLayerSweeped.saveChanges();
            }
        }
    }

    private int findClosestPileIndex(float sweeperPosition, float sweepedPosition, float sweepedWidth) {
        float sweepedPerc = (sweeperPosition - sweepedPosition) / sweepedWidth;
        sweepedPerc = MathUtils.clamp(sweepedPerc, 0f, 1f);
        int pileCount = MathUtils.floor(sweepedWidth * LevelSystem.PILE_COUNT_PER_METER) - 1;
        return MathUtils.round(sweepedPerc * pileCount);

    }
}
