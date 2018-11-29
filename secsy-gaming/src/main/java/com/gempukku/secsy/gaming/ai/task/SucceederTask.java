package com.gempukku.secsy.gaming.ai.task;

import com.gempukku.secsy.gaming.ai.AIReference;
import com.gempukku.secsy.gaming.ai.AITask;
import com.gempukku.secsy.gaming.ai.AITaskResult;
import com.gempukku.secsy.gaming.ai.builder.TaskBuilder;

import java.util.Map;

public class SucceederTask<Reference extends AIReference> extends AbstractWrapperAITask<Reference> {
    public SucceederTask(String id, AITask parent, TaskBuilder<Reference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);
    }

    @Override
    public AITaskResult startTask(Reference reference) {
        return invert(getTask().startTask(reference));
    }

    @Override
    public AITaskResult continueTask(Reference reference) {
        return invert(getTask().continueTask(reference));
    }

    @Override
    public void cancelTask(Reference reference) {
        getTask().cancelTask(reference);
    }

    private AITaskResult invert(AITaskResult result) {
        if (result == AITaskResult.RUNNING)
            return result;
        else
            return AITaskResult.SUCCESS;
    }
}
