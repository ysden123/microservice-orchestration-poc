/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.zeebe.condition1.worker

import com.stulsoft.zeebe.condition1.AppConfig
import com.typesafe.scalalogging.LazyLogging
import io.zeebe.client.ZeebeClient
import io.zeebe.client.api.response.ActivatedJob

/**
 * @author Yuriy Stul
 */
object Workers extends App with LazyLogging{
  logger.info("Workers are started.")

  var zeebeClient: ZeebeClient = _
  try {
    zeebeClient = ZeebeClient.newClientBuilder()
      .brokerContactPoint(s"${AppConfig.zeebeHost}:${AppConfig.zeebePort}")
      .build()

    val jobValidateRequest = "validate-request"
    logger.info(s"Register $jobValidateRequest")
    zeebeClient.newWorker()
      .jobType(jobValidateRequest)
      .handler((jobClient, job) => {
        logger.info(s"$jobValidateRequest: Checking request ...")
        outputData(jobValidateRequest, job)

        val newData = new java.util.HashMap[String, Any]()
        newData.put("valid", false)
        jobClient.newCompleteCommand(job.getKey)
          .variables(newData)
          .send()
          .join()
      })
      .open()
    
    val jobTerminate = "terminate"
    logger.info(s"Register $jobTerminate")
    zeebeClient.newWorker()
      .jobType(jobTerminate)
      .handler((jobClient, job) => {
        logger.info(s"$jobTerminate: Terminating request ...")
        outputData(jobTerminate, job)

        jobClient.newCompleteCommand(job.getKey)
          .send()
          .join()
      })
      .open()
    
    val jobUpdateDb = "update-db"
    logger.info(s"Register $jobUpdateDb")
    zeebeClient.newWorker()
      .jobType(jobUpdateDb)
      .handler((jobClient, job) => {
        logger.info(s"$jobUpdateDb: Updating db ...")
        outputData(jobUpdateDb, job)

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

  sys.addShutdownHook({
    logger.info("Closing client...")
    if (zeebeClient != null)
      zeebeClient.close()
  })

  private def outputData(jobType: String, job: ActivatedJob): Unit = {
    logger.info(s"$jobType -> data: ")
    job.getVariablesAsMap.forEach((key, value) => logger.info(s"$jobType: $key -> $value"))
  }
}
