/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.poc.simple.worker

import java.util

import com.netflix.conductor.client.http.WorkflowClient
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest
import com.netflix.conductor.common.run.Workflow
import com.netflix.conductor.common.run.Workflow.WorkflowStatus
import com.typesafe.scalalogging.LazyLogging

/** Runs workflow, calls workflow with polling.
 *
 * @author Yuriy Stul
 */
object WorkflowRunner2 extends LazyLogging {
  def main(args: Array[String]): Unit = {
    logger.info("==>main")
    val workflowClient = new WorkflowClient()
    workflowClient.setRootURI("http://localhost:8080/api/")
    val startWorkflowRequest = new StartWorkflowRequest()
    startWorkflowRequest.setName("task_1_WORKFLOW")
    startWorkflowRequest.setVersion(28)
    val eventData = new util.HashMap[String, Any]()
    eventData.put("sourceRequestId", 123)
    eventData.put("qcElementType", "big")
    startWorkflowRequest.getInput.put("eventData", eventData)
    val now = System.currentTimeMillis()
    val workflowId = workflowClient.startWorkflow(startWorkflowRequest)
    logger.info(s"Start workflow took ${System.currentTimeMillis() - now} ms")

    var pollingCounter = 10
    var workFlow: Workflow = null

    var workflowStatus: WorkflowStatus = null
    do {
      println(s"pollingCounter = $pollingCounter")
      workFlow = workflowClient.getWorkflow(workflowId, false)
      workflowStatus = workFlow.getStatus
      if (workflowStatus == WorkflowStatus.RUNNING)
        Thread.sleep(100)
      pollingCounter -= 1
    } while (pollingCounter > 0 && workflowStatus == WorkflowStatus.RUNNING)

    workflowStatus match {
      case WorkflowStatus.COMPLETED =>
        logger.info(s"Response time is ${System.currentTimeMillis() - now} ms. Result:")
        workFlow
          .getOutput
          .get("resultData")
          .asInstanceOf[java.util.Map[String, Any]]
          .forEach((k, v) => logger.info(s"$k -> $v"))
      case x =>
        logger.warn(s"Is not completed yet. Current status is $x. Response time is ${System.currentTimeMillis() - now} ms")
    }
  }
}
