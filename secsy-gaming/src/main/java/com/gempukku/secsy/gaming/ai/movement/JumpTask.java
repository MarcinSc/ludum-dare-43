package com.gempukku.secsy.gaming.ai.movement;

import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.ai.AITask;
import com.gempukku.secsy.gaming.ai.AITaskResult;
import com.gempukku.secsy.gaming.ai.EntityRefReference;
import com.gempukku.secsy.gaming.ai.builder.TaskBuilder;
import com.gempukku.secsy.gaming.ai.task.AbstractAITask;
import com.gempukku.secsy.gaming.physics.basic2d.MovingComponent;

import java.util.Map;

public class JumpTask extends AbstractAITask<EntityRefReference> {
    public JumpTask(String id, AITask parent, TaskBuilder<EntityRefReference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);
    }

    @Override
    public AITaskResult startTask(EntityRefReference reference) {
        EntityRef entityRef = reference.getEntityRef();
        MovingComponent moving = entityRef.getComponent(MovingComponent.class);
        AIJumpConfigurationComponent jumpConfiguration = entityRef.getComponent(AIJumpConfigurationComponent.class);
        moving.setSpeedY(jumpConfiguration.getJumpSpeed());
        entityRef.saveChanges();
        return AITaskResult.SUCCESS;
    }

    @Override
    public AITaskResult continueTask(EntityRefReference reference) {
        return null;
    }

    @Override
    public void cancelTask(EntityRefReference reference) {

    }
}
