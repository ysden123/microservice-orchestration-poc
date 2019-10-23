/*
 * Copyright (c) 2019. Webpals
 */

package com.webpals.zeebe.workflow.loader;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Yuriy Stul
 */
class AppConfigTest {

    @Test
    void appConfigHost() {
        String host = Main.AppConfig.zeebeHost();
        assertNotNull(host);
    }

    @Test
    void appConfigPort() {
        int port = Main.AppConfig.zeebePort();
        assertTrue(port > 0);
    }
}