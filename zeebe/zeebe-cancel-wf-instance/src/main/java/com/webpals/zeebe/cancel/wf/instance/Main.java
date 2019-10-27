/*
 * Copyright (c) 2019. Webpals
 */

package com.webpals.zeebe.cancel.wf.instance;

import io.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstrates canceling a workflow instance in a task
 *
 * @author Yuriy Stul
 */
public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        String contactPoint = String.format("%s:%d",
                AppConfig.getInstance().zeebeHost(),
                AppConfig.getInstance().zeebePort());
        ZeebeClient client;
        try {
            client = ZeebeClient
                    .newClientBuilder()
                    .brokerContactPoint(contactPoint)
                    .build();
            client.newWorker()
                    .jobType("step1")
                    .handler((jobClient, job) -> {
                        logger.info("Handling step1");

                        // Some business logic is here
                        // ...

                        // Mark incident
                        logger.info("Fail task");
                        client.newFailCommand(job.getKey())
                                .retries(0)
                                .errorMessage("Instance is failed with some business logic")
                                .send().join();

                        // Cancel instance
                        logger.info("Canceling instance {} ...", job.getWorkflowInstanceKey());
                        client.newCancelInstanceCommand(job.getWorkflowInstanceKey())
                                .send().join();
                    })
                    .open();
            logger.info("'step1' is started");
        } catch (Exception ex) {
            logger.error("Failure: " + ex.getMessage(), ex);
            System.exit(2);
        }
    }
}
