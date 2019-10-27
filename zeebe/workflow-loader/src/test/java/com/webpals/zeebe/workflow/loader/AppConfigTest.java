/*
 * Copyright (c) 2019. Webpals
 */

package com.webpals.zeebe.workflow.loader;


import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Yuriy Stul
 */
public class AppConfigTest {

    @Test
    public void appConfigHost() {
        String host = AppConfig.getInstance().zeebeHost();
        assertNotNull(host);
    }

    @Test
    public void appConfigPort() {
        int port = AppConfig.getInstance().zeebePort();
        assertTrue(port > 0);
    }
}