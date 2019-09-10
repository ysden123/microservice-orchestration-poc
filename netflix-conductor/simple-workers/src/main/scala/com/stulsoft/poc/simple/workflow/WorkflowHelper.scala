/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.poc.simple.workflow

import java.util

import com.netflix.conductor.client.http.WorkflowClient
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest
import com.netflix.conductor.common.run.Workflow
import com.netflix.conductor.common.run.Workflow.WorkflowStatus

/**
 * @author Yuriy Stul
 */
object WorkflowHelper {
  def runWorkflow(flowName: String,
                  rootUri: String,
                  version: Int,
                  inputData: util.Map[String, Any]): Either[String, util.Map[String, Any]] = {
    val workflowClient = new WorkflowClient()
    workflowClient.setRootURI(rootUri)
    val startWorkflowRequest = new StartWorkflowRequest()
    startWorkflowRequest.setName(flowName)
    startWorkflowRequest.setVersion(version)
    startWorkflowRequest.getInput.put("eventData", inputData)
    val workflowId = workflowClient.startWorkflow(startWorkflowRequest)

    var pollingCounter = 20
    var workFlow: Workflow = null

    var workflowStatus: WorkflowStatus = null
    do {
      workFlow = workflowClient.getWorkflow(workflowId, false)
      workflowStatus = workFlow.getStatus
      if (workflowStatus == WorkflowStatus.RUNNING)
        Thread.sleep(100)
      pollingCounter -= 1
    } while (pollingCounter > 0 && workflowStatus == WorkflowStatus.RUNNING)

    workflowStatus match {
      case WorkflowStatus.COMPLETED =>
        Right(
        workFlow
          .getOutput
          .get("resultData")
          .asInstanceOf[java.util.Map[String, Any]])
      case x =>
        Left(s"Is not completed yet. Current status is $x.")
    }
  }
}
