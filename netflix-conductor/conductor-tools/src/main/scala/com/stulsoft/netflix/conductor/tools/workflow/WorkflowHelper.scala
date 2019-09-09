/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.netflix.conductor.tools.workflow

import java.util

import com.netflix.conductor.client.http.WorkflowClient
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest
import com.netflix.conductor.common.run.Workflow
import com.netflix.conductor.common.run.Workflow.WorkflowStatus
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{Await, Future, Promise}

/** Workflow Helper
 *
 * @param rootUri           specifies root URI, e.g. "http://localhost:8080/api/"
 * @param maxPollingCounter maximum number of pollings
 * @param pollingInterval   polling interval im milliseconds
 * @author Yuriy Stul
 */
class WorkflowHelper(val rootUri: String, maxPollingCounter: Int, pollingInterval: Long) extends LazyLogging {

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._

  private def initWorkflowClient(): WorkflowClient = {
    val workflowClient = new WorkflowClient()
    workflowClient.setRootURI(rootUri)
    workflowClient
  }

  /**
   * Sends request to run a workflow
   *
   * @param flowName  specifies flow name
   * @param version   specifies flow version
   * @param inputData specifies input data
   * @return future with workflow result (Right) or failure description (Left)
   */
  def runWorkflowAsync(flowName: String,
                       version: Int,
                       inputData: util.Map[String, Any]): Future[Either[String, util.Map[String, Any]]] = {
    val promise = Promise[Either[String, util.Map[String, Any]]]
    Future {
      val workflowClient = initWorkflowClient()
      val startWorkflowRequest = new StartWorkflowRequest()
      startWorkflowRequest.setName(flowName)
      startWorkflowRequest.setVersion(version)
      startWorkflowRequest.getInput.put("eventData", inputData)
      try {
        val workflowId = workflowClient.startWorkflow(startWorkflowRequest)

        var pollingCounter = maxPollingCounter
        var workFlow: Workflow = null

        var workflowStatus: WorkflowStatus = null
        do {
          workFlow = workflowClient.getWorkflow(workflowId, false)
          workflowStatus = workFlow.getStatus
          if (workflowStatus == WorkflowStatus.RUNNING)
            Thread.sleep(pollingInterval)
          pollingCounter -= 1
        } while (pollingCounter > 0 && workflowStatus == WorkflowStatus.RUNNING)

        workflowStatus match {
          case WorkflowStatus.COMPLETED =>
            promise.success(
              Right(
                workFlow
                  .getOutput
                  .get("resultData")
                  .asInstanceOf[java.util.Map[String, Any]]))
          case x =>
            promise.success(Left(s"Is not completed yet. Current status is $x."))
        }
      } catch {
        case x: Exception =>
          val msg = s"Failed sending request: ${x.getMessage}"
          logger.error(msg)
          promise.success(Left(msg))
      }
    }
    promise.future
  }

  /**
   * Sends request to run a workflow
   *
   * @param flowName  specifies flow name
   * @param version   specifies flow version
   * @param inputData specifies input data
   * @return workflow result (Right) or failure description (Left)
   */
  def runWorkflowSync(flowName: String,
                      version: Int,
                      inputData: util.Map[String, Any]): Either[String, util.Map[String, Any]] = {
    Await.result(runWorkflowAsync(flowName, version, inputData), Duration.Inf)
  }
}
