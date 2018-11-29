package com.gempukku.secsy.gaming.ai.task;

import com.gempukku.secsy.gaming.ai.AIReference;
import com.gempukku.secsy.gaming.ai.AITask;
import com.gempukku.secsy.gaming.ai.builder.TaskBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TaskCollectionUtil {

    private TaskCollectionUtil() {
    }

    public static <Reference extends AIReference> List<AITask<Reference>> buildTasks(AITask parent, TaskBuilder<Reference> taskBuilder, List<Map<String, Object>> taskList) {
        List<AITask<Reference>> tasks = new ArrayList<AITask<Reference>>(taskList.size());
        for (Map<String, Object> taskInfo : taskList) {
            AITask<Reference> task = taskBuilder.buildTask(parent, taskInfo);
            tasks.add(task);
        }
        return tasks;
    }
}
