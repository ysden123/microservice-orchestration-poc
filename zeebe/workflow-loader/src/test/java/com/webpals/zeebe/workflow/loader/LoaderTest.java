/*
 * Copyright (c) 2019. Webpals
 */

package com.webpals.zeebe.workflow.loader;

import io.zeebe.client.ZeebeClient;
import io.zeebe.test.ZeebeTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Yuriy Stul
 */
public class LoaderTest {
    @Rule public ZeebeTestRule testRule = new ZeebeTestRule();

    private ZeebeClient client;

    @Before
    public void createClient(){
        client = testRule.getClient();
    }

    @After
    public void closeClient(){
        client.close();
    }

    @Test
    public void deployWorkflows() {
        client = testRule.getClient();
        Loader loader = new Loader("src/test/resources/repository", "service1", client);
        loader.deployWorkflows();
    }
}