package com.stulsoft.poc.simple.worker

import java.util

import com.netflix.conductor.client.http.MetadataClient
import com.netflix.conductor.common.metadata.tasks.TaskDef
import com.netflix.conductor.common.metadata.tasks.TaskDef.{RetryLogic, TimeoutPolicy}
import com.netflix.conductor.common.metadata.workflow.{WorkflowDef, WorkflowTask}
import com.typesafe.scalalogging.LazyLogging

/** Register task definition and work flow definition.
 *
 * @author Yuriy Stul
 */
object TaskDefinitionCreator extends LazyLogging {
  def main(args: Array[String]): Unit = {
    logger.info("==>main")
    val metadataClient = new MetadataClient()
    metadataClient.setRootURI("http://localhost:8080/api/")
    val taskDefs = new util.ArrayList[TaskDef]()
    val taskDef = new TaskDef()
    taskDef.setName("task_1")
    taskDef.setRetryCount(3)
    taskDef.setTimeoutSeconds(1200)
    taskDef.setInputKeys(util.Arrays.asList("sourceRequestId", "qcElementType"))
    taskDef.setOutputKeys(util.Arrays.asList("state", "skipped", "result"))
    taskDef.setTimeoutPolicy(TimeoutPolicy.TIME_OUT_WF)
    taskDef.setRetryLogic(RetryLogic.FIXED)
    taskDef.setRetryDelaySeconds(600)
    taskDef.setResponseTimeoutSeconds(1100)
    taskDef.setConcurrentExecLimit(100)
    taskDef.setRateLimitFrequencyInSeconds(60)
    taskDef.setRateLimitPerFrequency(50)
    taskDefs.add(taskDef)
    try {
      logger.info("Registering task definition...")
      val now = System.currentTimeMillis()
      metadataClient.registerTaskDefs(taskDefs)
      logger.info(s"Registration completed in ${System.currentTimeMillis() - now} ms")
    } catch {
      case e: Exception =>
        logger.error(s"Failed task definition registration: ${e.getMessage}")
        sys.exit(-1)
    }

    val workflowDef = new WorkflowDef()
    workflowDef.setName("task_1_WORKFLOW")
    workflowDef.setDescription("MIM workflow 1")
    workflowDef.setVersion(28)
    val workflowTask = new WorkflowTask()
    workflowTask.setName("task_1")
    workflowTask.setTaskReferenceName("task_1")
    workflowTask.setDescription("TEST 1")
    val inputParameters = new util.HashMap[String, Any]()
    inputParameters.put("eventData", "${workflow.input.eventData}")
    workflowTask.setInputParameters(inputParameters)
    workflowTask.setType("SIMPLE")
    workflowTask.setStartDelay(0)
    workflowTask.setOptional(false)
    val taskDefinition = new TaskDef()
    taskDefinition.setName("task_55_2")
    taskDefinition.setDescription("TEST 1")
    taskDefinition.setRetryCount(3)
    taskDefinition.setTimeoutPolicy(TimeoutPolicy.TIME_OUT_WF)
    taskDefinition.setRetryLogic(RetryLogic.FIXED)
    taskDefinition.setResponseTimeoutSeconds(3600)
    taskDefinition.setRateLimitPerFrequency(0)
    taskDefinition.setRateLimitFrequencyInSeconds(1)
    workflowTask.setTaskDefinition(taskDefinition)
    workflowDef.setTasks(util.Arrays.asList(workflowTask))
    workflowDef.setSchemaVersion(2)
    workflowDef.setRestartable(true)
    try {
      logger.info("Registering workflow definition...")
      val now = System.currentTimeMillis()
      metadataClient.registerWorkflowDef(workflowDef)
      logger.info(s"Registration completed in ${System.currentTimeMillis() - now} ms")
    } catch {
      case e: Exception =>
        logger.error(s"Failed workflow definition registration: ${e.getMessage}")
        sys.exit(-1)
    }
  }
}
