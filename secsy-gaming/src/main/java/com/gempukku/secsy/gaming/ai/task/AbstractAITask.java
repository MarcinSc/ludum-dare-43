package com.gempukku.secsy.gaming.ai.task;

import com.gempukku.secsy.gaming.ai.AIReference;
import com.gempukku.secsy.gaming.ai.AITask;
import com.gempukku.secsy.gaming.ai.builder.TaskBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public abstract class AbstractAITask<Reference extends AIReference> implements AITask<Reference> {
    private AITask parent;
    private String id;

    public AbstractAITask(String id, AITask parent, TaskBuilder<Reference> taskBuilder, Map<String, Object> taskData) {
        this.id = id;
        this.parent = parent;
    }

    protected final String getId() {
        return this.id;
    }

    @Override
    public final AITask getParent() {
        return parent;
    }

    @Override
    public Collection<AITask<Reference>> getRunningTasks(Reference reference) {
        return Collections.<AITask<Reference>>singleton(this);
    }
}
