/*
 * Copyright (c) 2019. Webpals
 */

package com.webpals.zeebe.workflow.loader;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Yuriy Stul
 */
final public class Main {
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
        String repository = null;
        String service = null;
        String wfName = null;
        if (args.length >=2){
            // Get arguments from command line
            repository = args[0];
            service = args[1];
            if (args.length > 2){
                wfName=args[2];
            }
        }else{
            // Get arguments from environment variables
            repository = System.getenv("REPOSITORY");
            service = System.getenv("SERVICE");
            wfName = System.getenv("WF_NAME");
        }
        if (repository == null || service == null){
            logger.error("Undefined repository [{}] or/and service [{}]", repository, service);
            System.exit(1);
        }

        logger.info("Starting workflow loader for {} repository, {} service, and {} workflow ...",
                repository, service, wfName);

        String contactPoint = String.format("%s:%d", AppConfig.zeebeHost(), AppConfig.zeebePort());
        try (ZeebeClient client = ZeebeClient
                .newClientBuilder()
                .brokerContactPoint(contactPoint)
                .build()) {

            logger.info("Workflow loader is started");

            Loader loader = new Loader(repository, service, wfName, client);
            loader.deployWorkflows();
        } catch (Exception ex) {
            logger.error("Failure: " + ex.getMessage(), ex);
            System.exit(2);
        }
    }
}
