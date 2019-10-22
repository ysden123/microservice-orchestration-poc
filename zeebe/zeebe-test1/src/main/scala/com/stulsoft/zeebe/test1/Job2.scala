/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.zeebe.test1

import com.typesafe.scalalogging.StrictLogging
import io.zeebe.client.api.response.ActivatedJob
import io.zeebe.client.api.worker.{JobClient, JobHandler}

/**
 * @author Yuriy Stul
 */
class Job2 extends JobHandler with StrictLogging {
  val jobType: String = "step2"

  override def handle(client: JobClient, job: ActivatedJob): Unit = {
    Helper.showVariables(jobType, job)
    client
      .newCompleteCommand(job.getKey)
      .send()
      .join()
  }
}
