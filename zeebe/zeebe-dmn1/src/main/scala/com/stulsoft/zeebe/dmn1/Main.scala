/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.zeebe.dmn1

import com.stulsoft.zeebe.dmn1.worker.DmnJobHandler
import com.typesafe.scalalogging.LazyLogging
import io.zeebe.client.ZeebeClient
import io.zeebe.client.api.response.ActivatedJob
import org.camunda.dmn.standalone.StandaloneEngine

/**
 * @author Yuriy Stul
 */
object Main extends App with LazyLogging {
  logger.info("==>DMN1 is started")
  var zeebeClient: ZeebeClient = _

  try {
    zeebeClient = ZeebeClient.newClientBuilder()
      .brokerContactPoint(s"${AppConfig.zeebeHost}:${AppConfig.zeebePort}")
      .build()

    logger.info("Deploying decisions...")
    val dmnEngine = StandaloneEngine.fileSystemRepository("repository")

    val deployedDecisions = dmnEngine.getDecisions
      .map(decision => s"${decision.decisionId} (${decision.resource})")

    logger.info(s"Deployed decisions: $deployedDecisions")
    zeebeClient
      .newWorker()
      .jobType("DMN")
      .handler(new DmnJobHandler(dmnEngine))
      .open()

    logger.info("Deploying Output Task")
    val jobType = "output-task"
    zeebeClient.newWorker()
      .jobType(jobType)
      .handler((jobClient, job) => {
        logger.info(s"$jobType: output data ...")
        outputData(jobType, job)

        jobClient.newCompleteCommand(job.getKey)
          .send()
          .join()
      })
      .open()
  } catch {
    case ex: Exception =>
      logger.error(s"Failure: ${ex.getMessage}", ex)
      if (zeebeClient != null)
        zeebeClient.close()
  }

  private def outputData(jobType: String, job: ActivatedJob): Unit = {
    logger.info(s"$jobType -> data: ")
    job.getVariablesAsMap.forEach((key, value) => logger.info(s"$jobType: $key -> $value"))
  }

  sys.addShutdownHook({
    logger.info("Closing client...")
    if (zeebeClient != null)
      zeebeClient.close()
  })
}
