/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.netflix.conductor.err1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.client.http.MetadataClient;
import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.client.http.WorkflowClient;
import com.netflix.conductor.client.task.WorkflowTaskCoordinator;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Yuriy Stul
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final MetadataClient metadataClient;

    static {
        metadataClient = new MetadataClient();
        metadataClient.setRootURI(AppConfig.getInstance().rootURI());
    }

    public static void main(String[] args) {
        logger.info("==>main");

        loadTaskDefs();

        loadWFDefs();

        runWorkers();

        runWorkflow();

        logger.info("<==main");
    }

    private static void loadTaskDefs() {
        logger.info("==>loadTaskDefs");

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            TaskDef[] taskDefs = objectMapper.readValue(Main.class.getClassLoader().getResourceAsStream("taskDefinitions.json"), TaskDef[].class);
            List<TaskDef> taskDefsToRegister = new LinkedList<>();

            for (TaskDef taskDef : taskDefs) {
                try {
                    // Throws exception if task doesn't exist.
                    if (metadataClient.getTaskDef(taskDef.getName()) == null)
                        taskDefsToRegister.add(taskDef);
                    else
                        metadataClient.updateTaskDef(taskDef);
                } catch (Exception ignore) {
                    taskDefsToRegister.add(taskDef);
                }
            }
            logger.info("Registering {} from {} tasks ...", taskDefsToRegister.size(), taskDefs.length);
            if (!taskDefsToRegister.isEmpty())
                metadataClient.registerTaskDefs(taskDefsToRegister);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            System.exit(1);
        }

        logger.info("<==loadTaskDefs");
    }

    private static void loadWFDefs() {
        logger.info("==>loadWFDefs");

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            WorkflowDef[] wfDefs = objectMapper.readValue(Main.class.getClassLoader().getResourceAsStream("wfDefinition.json"), WorkflowDef[].class);
//            WorkflowDef[] wfDefs = objectMapper.readValue(Main.class.getClassLoader().getResourceAsStream("wfDefinition1.json"), WorkflowDef[].class);
            logger.info("Registering {} workflows ...", wfDefs.length);
            for (WorkflowDef wfDef : wfDefs) {
                try {
                    logger.info("Registering {}", wfDef.getName());
                    metadataClient.registerWorkflowDef(wfDef);
                } catch (Exception ignore) {
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            System.exit(1);
        }

        logger.info("<==loadWFDefs");
    }

    private static void runWorkers() {
        logger.info("==>runWorkers");
        TaskClient taskClient = new TaskClient();
        taskClient.setRootURI(AppConfig.getInstance().rootURI());

        // Create workflow coordinator
        System.setProperty("conductor.worker.pollInterval", "100");
        //    System.setProperty("conductor.worker.pollInterval", "1000") // Default
        WorkflowTaskCoordinator.Builder builder = new WorkflowTaskCoordinator.Builder();
        WorkflowTaskCoordinator coordinator = builder
                .withWorkers(new Worker1(), new Worker2())
                .withThreadCount(5)
                .withTaskClient(taskClient)
                .build();

        //Start for polling and execution of the tasks
        coordinator.init();
        logger.info("<==runWorkers");
    }

    private static void runWorkflow() {
        logger.info("==>runWorkflow");

        try {
            WorkflowClient workflowClient = new WorkflowClient();
            workflowClient.setRootURI(AppConfig.getInstance().rootURI());
            StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest();
            startWorkflowRequest.setName("Error_test_1_workflow");
//            startWorkflowRequest.setVersion(5);
            // Use last workflow definition
            Map<String, Object> inputData = new HashMap<>();
            inputData.put("sourceRequestId", 1);
            inputData.put("qcElementType", "test");
            startWorkflowRequest.getInput().put("eventData", inputData);
            String workflowId = workflowClient.startWorkflow(startWorkflowRequest);
            logger.info("Started workflow with id {}", workflowId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            System.exit(2);
        }

        logger.info("<==runWorkflow");
    }
}
