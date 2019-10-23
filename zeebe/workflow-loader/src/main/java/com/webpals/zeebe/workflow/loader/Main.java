/*
 * Copyright (c) 2019. Webpals
 */

package com.webpals.zeebe.workflow.loader;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Yuriy Stul
 */
public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    static class AppConfig {
        private static Config config;

        static {
            config = ConfigFactory
                    .parseFile(new File("application.conf"))
                    .withFallback(ConfigFactory.load());
        }

        static String zeebeHost() {
            return config.getConfig("zeebe").getString("host");
        }

        static int zeebePort() {
            return config.getConfig("zeebe").getInt("port");
        }
    }

    public static void main(String[] args) {
        logger.info("Workflow loader is started");
    }
}
