/*
 * Copyright (c) 2019. Webpals
 */

package com.webpals.zeebe.workflow.loader;

import io.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Yuriy Stul
 */
final class Loader {
    private static Logger logger = LoggerFactory.getLogger(Loader.class);
    private String repositoryPath;
    private String serviceName;
    private ZeebeClient client;

    Loader(String repositoryPath, String serviceName, ZeebeClient client) {
        Objects.requireNonNull(repositoryPath, "repositoryPath should be specified");
        Objects.requireNonNull(serviceName, "serviceName should be specified");
        Objects.requireNonNull(client, "client should be specified");
        this.repositoryPath = repositoryPath;
        this.serviceName = serviceName;
        this.client = client;
    }

    void deployWorkflows() {
        String[] fileNames = workflowFileNames();
        logger.info("Deploying {} workflows...", fileNames.length);
        int successCount = 0;
        int failedCount = 0;
        for (String fileName : fileNames) {
            try {
                logger.info("Deploying {} ...", fileName);
                client.newDeployCommand()
                        .addResourceFile(fileName)
                        .send()
                        .join();
                logger.info("Deployed {}", fileName);
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

    private String[] workflowFileNames() {
        String pathName = repositoryPath + "/" + serviceName;
        File repository = new File(pathName);
        if (!repository.exists()) {
            String msg = String.format("Folder with %s name doesn't exist.", pathName);
            logger.error(msg);
            throw new RuntimeException(msg);
        }

        return Arrays.stream(Objects.requireNonNull(repository.list((dir, name) -> name.endsWith(".bpmn"))))
                .map(n -> pathName + "/" + n).toArray(String[]::new);
    }
}
