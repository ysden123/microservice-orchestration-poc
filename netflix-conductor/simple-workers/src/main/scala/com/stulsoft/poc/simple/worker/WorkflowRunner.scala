package com.stulsoft.poc.simple.worker

import java.util

import com.netflix.conductor.client.http.WorkflowClient
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest
import com.typesafe.scalalogging.LazyLogging

/** Runs workflow, calls workflow.
 *
 * @author Yuriy Stul
 */
object WorkflowRunner extends LazyLogging {
  def main(args: Array[String]): Unit = {
    logger.info("==>main")
    val workflowClient = new WorkflowClient()
    workflowClient.setRootURI("http://localhost:8080/api/")
    val startWorkflowRequest = new StartWorkflowRequest()
    startWorkflowRequest.setName("task_1_WORKFLOW")
    startWorkflowRequest.setVersion(28)
    val eventData = new util.HashMap[String, Any]()
    eventData.put("sourceRequstId", 123)
    eventData.put("qcElementType", "big")
    startWorkflowRequest.getInput.put("eventData", eventData)
    var now = System.currentTimeMillis()
    val workflowId = workflowClient.startWorkflow(startWorkflowRequest)
    logger.info(s"Start workflow took ${System.currentTimeMillis() - now} ms")

    Thread.sleep(5000)
    now = System.currentTimeMillis()
    val workFlow = workflowClient.getWorkflow(workflowId, false)
    logger.info(s"Getting workflow result took ${System.currentTimeMillis() - now} ms")
    workFlow
      .getOutput
      .get("resultData")
      .asInstanceOf[java.util.Map[String, Any]]
      .forEach((k, v) => logger.info(s"$k -> $v"))
  }

}
