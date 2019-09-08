/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.poc.simple.worker

import java.util

import com.stulsoft.poc.simple.workflow.WorkflowHelper
import com.typesafe.scalalogging.LazyLogging

/**
 * @author Yuriy Stul
 */
object WorkflowRunner3 extends LazyLogging {
  def main(args: Array[String]): Unit = {
    logger.info("==>main")
    val inputData = new util.HashMap[String, Any]()
    inputData.put("sourceRequestId", 123)
    inputData.put("qcElementType", "big")
    WorkflowHelper.runWorkflow("task_1_WORKFLOW",
      "http://localhost:8080/api/",
      28, inputData) match {
      case Right(result) =>
        logger.info("Result:")
        result.forEach((k, v) => logger.info(s"$k -> $v"))
      case Left(error) =>
        logger.error(s"Failure: $error")
    }
  }

}
