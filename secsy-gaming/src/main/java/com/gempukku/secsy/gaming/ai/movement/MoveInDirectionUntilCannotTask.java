package com.gempukku.secsy.gaming.ai.movement;

import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.ai.AITask;
import com.gempukku.secsy.gaming.ai.AITaskResult;
import com.gempukku.secsy.gaming.ai.EntityRefReference;
import com.gempukku.secsy.gaming.ai.builder.TaskBuilder;
import com.gempukku.secsy.gaming.ai.task.AbstractAITask;

import java.util.Map;

public class MoveInDirectionUntilCannotTask extends AbstractAITask<EntityRefReference> {
    private static final String CANT_MOVE = "cantMove";

    public MoveInDirectionUntilCannotTask(String id, AITask parent, TaskBuilder<EntityRefReference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);
    }

    @Override
    public AITaskResult startTask(EntityRefReference reference) {
        EntityRef entityRef = reference.getEntityRef();
        entityRef.createComponent(AIApplyMovementIfPossibleComponent.class);
        entityRef.saveChanges();
        return AITaskResult.RUNNING;
    }

    @Override
    public AITaskResult continueTask(EntityRefReference reference) {
        if (reference.getValue(getId(), CANT_MOVE, Boolean.class) != null) {
            reference.removeValue(getId(), CANT_MOVE);

            EntityRef entityRef = reference.getEntityRef();
            entityRef.removeComponents(AIApplyMovementIfPossibleComponent.class);
            entityRef.saveChanges();

            return AITaskResult.SUCCESS;
        }
        return AITaskResult.RUNNING;
    }

    @Override
    public void cancelTask(EntityRefReference reference) {
        reference.removeValue(getId(), CANT_MOVE);

        EntityRef entityRef = reference.getEntityRef();
        entityRef.removeComponents(AIApplyMovementIfPossibleComponent.class);
        entityRef.saveChanges();
    }

    public void notifyCantMove(EntityRefReference reference) {
        reference.setValue(getId(), CANT_MOVE, true);
        reference.storeValues();
    }
}
