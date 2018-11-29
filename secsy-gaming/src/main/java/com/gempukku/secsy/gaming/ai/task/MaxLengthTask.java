package com.gempukku.secsy.gaming.ai.task;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.gaming.ai.AITask;
import com.gempukku.secsy.gaming.ai.AITaskResult;
import com.gempukku.secsy.gaming.ai.EntityRefReference;
import com.gempukku.secsy.gaming.ai.builder.TaskBuilder;
import com.gempukku.secsy.gaming.time.TimeManager;

import java.util.Map;

public class MaxLengthTask extends AbstractWrapperAITask<EntityRefReference> {
    @Inject
    private TimeManager timeManager;

    private long length;

    public MaxLengthTask(String id, AITask parent, TaskBuilder<EntityRefReference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);
        length = ((Number) taskData.get("length")).longValue();
    }

    @Override
    public AITaskResult startTask(EntityRefReference reference) {
        long time = timeManager.getTime();
        AITaskResult result = getTask().startTask(reference);
        if (result == AITaskResult.RUNNING)
            reference.setValue(getId(), "taskStart", time);
        return result;
    }

    @Override
    public AITaskResult continueTask(EntityRefReference reference) {
        long time = timeManager.getTime();
        long lastExecution = reference.getValue(getId(), "taskStart", Long.class);
        if (lastExecution + length < time) {
            getTask().cancelTask(reference);
            reference.removeValue(getId(), "taskStart");
            return AITaskResult.SUCCESS;
        } else {
            AITaskResult result = getTask().continueTask(reference);
            if (result != AITaskResult.RUNNING)
                reference.removeValue(getId(), "taskStart");
            return result;
        }
    }

    @Override
    public void cancelTask(EntityRefReference reference) {
        reference.removeValue(getId(), "taskStart");
        getTask().cancelTask(reference);
    }
}
