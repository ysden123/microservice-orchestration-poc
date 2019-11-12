/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.netflix.conductor.err1;

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

    private AppConfig() {
    }

    static AppConfig getInstance() {
        return instance;
    }

    String rootURI() {
        return String.format("http://%s:%d/api/",
                config.getConfig("conductor").getString("host"),
                config.getConfig("conductor").getInt("port"));
    }
}
