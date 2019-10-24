/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.zeebe.err1

import com.typesafe.scalalogging.StrictLogging
import io.zeebe.client.api.response.ActivatedJob
import io.zeebe.client.api.worker.{JobClient, JobHandler}

/** A job with error
 *
 * @author Yuriy Stul
 */
class Job1 extends JobHandler with StrictLogging {
  val jobType: String = "step1"

  override def handle(client: JobClient, job: ActivatedJob): Unit = {
    try {
      Helper.showVariables(jobType, job)
      throw new RuntimeException(s"Test exception for $jobType")
    } catch {
      case ex: Exception =>
        val msg = s"Job1 is failed: ${ex.getMessage}"
        logger.error(msg)
        client.newFailCommand(job.getKey)
          .retries(job.getRetries - 1)  // decrease number of retries!
          .errorMessage(msg)
          .send()
          .join()
    }
  }
}
