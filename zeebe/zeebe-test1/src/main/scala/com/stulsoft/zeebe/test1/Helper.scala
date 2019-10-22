/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.zeebe.test1

import com.typesafe.scalalogging.StrictLogging
import io.zeebe.client.api.response.ActivatedJob

/**
 * @author Yuriy Stul
 */
object Helper extends StrictLogging {
  def showVariables(jobType: String, job: ActivatedJob): Unit = {
    logger.info(s"$jobType -> retries = ${job.getRetries} ")
    logger.info(s"$jobType -> data: ")
    job.getVariablesAsMap.forEach((key, value) => logger.info(s"$jobType: $key -> $value"))
  }
}
