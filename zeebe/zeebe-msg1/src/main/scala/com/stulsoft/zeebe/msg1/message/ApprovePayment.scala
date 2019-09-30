/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.zeebe.msg1.message

import com.stulsoft.zeebe.msg1.AppConfig
import com.typesafe.scalalogging.LazyLogging
import io.zeebe.client.ZeebeClient

/** Sends payment approve
 *
 * @author Yuriy Stul
 */
object ApprovePayment extends App with LazyLogging {
  logger.info("Sending payment approvement...")
  var zeebeClient: ZeebeClient = _

  try {
    zeebeClient = ZeebeClient.newClientBuilder()
      .brokerContactPoint(s"${AppConfig.zeebeHost}:${AppConfig.zeebePort}")
      .build()

    val newData = new java.util.HashMap[String, Any]()
    newData.put("billId", 789)

    zeebeClient.newPublishMessageCommand()
      .messageName("payment-approved")
      .correlationKey("123")
      .variables(newData)
      .send()
      .join()

    logger.info("Correlated")

  } catch {
    case ex: Exception =>
      logger.error(s"Failure: ${ex.getMessage}", ex)
  } finally {
    if (zeebeClient != null)
      zeebeClient.close()
  }
}
