/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.zeebe.test1;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.response.WorkflowInstanceEvent;
import io.zeebe.test.ZeebeTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuriy Stul
 */
public class MainTest {
    @Rule public ZeebeTestRule testRule = new ZeebeTestRule();

    private ZeebeClient client;

    @Before
    public void deploy() {
        client = testRule.getClient();

        client
                .newDeployCommand()
                .addResourceFile("workflow/demoWF.bpmn")
                .send()
                .join();

        System.out.println("Deploying 1st task");
        Job1 job1Handler = new Job1();
        client
                .newWorker()
                .jobType(job1Handler.jobType())
                .handler(job1Handler)
                .open();

        System.out.println("Deploying 2nd task");
        Job2 job2Handler = new Job2();
        client
                .newWorker()
                .jobType(job2Handler.jobType())
                .handler(job2Handler)
                .open();
    }

    @Test
    public void successTest() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("p1", "test value");
        WorkflowInstanceEvent workflowInstance =
                client.newCreateInstanceCommand()
                        .bpmnProcessId("TestDemo")
                        .latestVersion()
                        .variables(variables)
                        .send()
                        .join();

        ZeebeTestRule
                .assertThat(workflowInstance)
                .isEnded()
                .hasPassed("StartEvent_1", "ServiceTask_1g30bjq", "ServiceTask_1yqje3o", "EndEvent_1tnt7ks");
    }
}
