/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.zeebe.err1

import com.typesafe.scalalogging.StrictLogging
import io.zeebe.client.ZeebeClient

/**
 * @author Yuriy Stul
 */
object Main extends App with StrictLogging {
  logger.info("==>ERR1 is started")
  var zeebeClient: ZeebeClient = _

  try {
    zeebeClient = ZeebeClient.newClientBuilder()
      .brokerContactPoint(s"${AppConfig.zeebeHost}:${AppConfig.zeebePort}")
      .build()

    logger.info("Deploying 1st task")
    val job1Handler = new Job1
    zeebeClient.newWorker()
        .jobType(job1Handler.jobType)
        .handler(job1Handler)
        .open()

    logger.info("Deploying 2nd task")
    val job2Handler = new Job2
    zeebeClient.newWorker()
        .jobType(job2Handler.jobType)
        .handler(job2Handler)
        .open()

  } catch {
    case ex: Exception =>
      logger.error(s"Failure: ${ex.getMessage}", ex)
      if (zeebeClient != null)
        zeebeClient.close()
  }

  sys.addShutdownHook({
    logger.info("Closing client...")
    if (zeebeClient != null)
      zeebeClient.close()
  })
}
