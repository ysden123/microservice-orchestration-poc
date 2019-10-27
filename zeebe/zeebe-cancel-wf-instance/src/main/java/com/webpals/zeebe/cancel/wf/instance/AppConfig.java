/*
 * Copyright (c) 2019. Webpals
 */

package com.webpals.zeebe.cancel.wf.instance;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

/**
 * @author Yuriy Stul
 */
public class AppConfig {
    private static final AppConfig instance = new AppConfig();
    private static final Config config;

    static {
        config = ConfigFactory
                .parseFile(new File("application.conf"))
                .withFallback(ConfigFactory.load());
    }

    private AppConfig(){}

    static AppConfig getInstance() {
        return instance;
    }

    String zeebeHost() {
        return config.getConfig("zeebe").getString("host");
    }

    int zeebePort() {
        return config.getConfig("zeebe").getInt("port");
    }
}
