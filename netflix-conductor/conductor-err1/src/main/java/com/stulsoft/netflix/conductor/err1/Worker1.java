/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.netflix.conductor.err1;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Yuriy Stul
 */
public class Worker1 implements Worker {
    private static final Logger logger = LoggerFactory.getLogger(Worker1.class);

    Worker1() {
        logger.info("Initializing Worker1");
    }

    @Override
    public String getTaskDefName() {
        return "errorTestTask1";
    }

    @Override
    public TaskResult execute(Task task) {
        logger.debug("task.getInputData: {}", task.getInputData());
        task.getInputData().forEach((k, v) -> logger.info("Key: {}, value: {}", k, v));
        Map<String, Object> eventData = task.getInputData();
        TaskResult result = new TaskResult(task);
        logger.debug("eventData: {}", eventData.toString());
        try {
            Thread.sleep(300);
        } catch (Exception ignore) {
        }

        result.setReasonForIncompletion("Test1 worker: test exception ");
        // Fails the task, repeat is allowed
        result.setStatus(TaskResult.Status.FAILED);
        // Terminates the task and workflow, no repeat allowed
//        result.setStatus(TaskResult.Status.FAILED_WITH_TERMINAL_ERROR);
//        result.setStatus(TaskResult.Status.COMPLETED);
        return result;
    }
}
