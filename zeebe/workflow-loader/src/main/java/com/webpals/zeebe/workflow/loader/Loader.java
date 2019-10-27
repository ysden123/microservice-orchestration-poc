/*
 * Copyright (c) 2019. Webpals
 */

package com.webpals.zeebe.workflow.loader;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.response.DeploymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Yuriy Stul
 */
final class Loader {
    private static Logger logger = LoggerFactory.getLogger(Loader.class);
    private final String repositoryPath;
    private final String serviceName;
    private final String wfName;
    private final ZeebeClient client;

    Loader(String repositoryPath, String serviceName, String wfName, ZeebeClient client) {
        Objects.requireNonNull(repositoryPath, "repositoryPath should be specified");
        Objects.requireNonNull(serviceName, "serviceName should be specified");
        Objects.requireNonNull(client, "client should be specified");
        this.repositoryPath = repositoryPath;
        this.serviceName = serviceName;
        this.wfName = wfName;
        this.client = client;
    }

    void deployWorkflows() {
        List<String> fileNames = workflowFileNames();
        logger.info("Deploying {} workflows...", fileNames.size());
        int successCount = 0;
        int failedCount = 0;
        for (String fileName : fileNames) {
            try {
                logger.info("Deploying {} ...", fileName);
                DeploymentEvent deploymentEvent = client.newDeployCommand()
                        .addResourceFile(fileName)
                        .send()
                        .join();
                logger.info("Deployed {}", fileName);
                deploymentEvent.getWorkflows().forEach(wf -> logger.info("BPMN Process ID: {}, resource name: {}, version: {}, workflow key={}",
                        wf.getBpmnProcessId(),
                        wf.getResourceName(),
                        wf.getVersion(),
                        wf.getWorkflowKey()));
                ++successCount;
            } catch (Exception ex) {
                String msg = "Deployment of " + fileName + " failed. " + ex.getMessage();
                logger.error(msg);
                ++failedCount;
            }
        }

        logger.info("From repository {} for service {} {} succeeded, {} failed",
                repositoryPath, serviceName, successCount, failedCount);
    }

    private List<String> workflowFileNames() {
        String pathName = repositoryPath + "/" + serviceName;
        File repository = new File(pathName);
        if (!repository.exists()) {
            String msg = String.format("Folder with %s name doesn't exist.", pathName);
            logger.error(msg);
            throw new RuntimeException(msg);
        }

        Stream<String> fileNames;

        if (wfName == null || wfName.isEmpty()) {
            fileNames = Arrays.stream(Objects.requireNonNull(repository.list((dir, name) -> name.endsWith(".bpmn"))));
        } else {
            fileNames = Arrays.stream(Objects.requireNonNull(repository.list((dir, name) -> name.equals(wfName))));
        }
        return fileNames.map(n -> pathName + "/" + n).collect(Collectors.toList());
    }
}
