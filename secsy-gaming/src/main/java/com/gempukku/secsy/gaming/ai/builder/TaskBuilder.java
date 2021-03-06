package com.gempukku.secsy.gaming.ai.builder;

import com.gempukku.secsy.gaming.ai.AIReference;
import com.gempukku.secsy.gaming.ai.AITask;

import java.util.Map;

public interface TaskBuilder<Reference extends AIReference> {
    AITask<Reference> loadBehavior(AITask parent, String behaviorName);

    AITask<Reference> buildTask(AITask parent, Map<String, Object> behaviorData);
}
