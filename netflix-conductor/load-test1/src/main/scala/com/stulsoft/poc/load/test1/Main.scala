/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.poc.load.test1

import java.util
import java.util.concurrent.atomic.AtomicInteger

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.conductor.client.http.{MetadataClient, TaskClient, WorkflowClient}
import com.netflix.conductor.client.task.WorkflowTaskCoordinator
import com.netflix.conductor.common.metadata.tasks.TaskDef
import com.netflix.conductor.common.metadata.workflow.{StartWorkflowRequest, WorkflowDef}
import com.typesafe.scalalogging.StrictLogging

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.io.Source
import scala.jdk.CollectionConverters._

/**
 * @author Yuriy Stul
 */
object Main extends App with StrictLogging {
  val rootURI = "http://localhost:8080/api/"
  val sourceRequestId = new AtomicInteger(0)
  val workCounter = new AtomicInteger(0)

  run()

  def run(): Unit = {
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
    logger.info("==>run")

    // load task and workflow definitions
    loadTaskAndWFDefinitions()

    // run workers
    val workProcessCount = 5
    (1 to workProcessCount).foreach(_ => Future(runWorkers()))

    // run workflows
    val futures = new ListBuffer[Future[Unit]]()
    val start = System.currentTimeMillis()
    val n = 100
    //    val n = 2
    Future(workExecutionWatcher(n))
    (1 to n).foreach(_ => futures += Future(runWorkflow()))
    Await.result(Future.sequence(futures), Duration.Inf)
    logger.info(s"Duration to send of $n requests took ${System.currentTimeMillis() - start} ms.")
    logger.info("<==run")
  }

  def loadTaskAndWFDefinitions(): Unit = {
    logger.info("==>loadTaskAndWFDefinitions")
    val metadataClient = new MetadataClient()
    metadataClient.setRootURI(rootURI)

    val objectMapper = new ObjectMapper()

    val taskDefsAsText = Source.fromResource("taskDefinition.json").mkString
    val taskDefs = objectMapper.readValue(taskDefsAsText, classOf[Array[TaskDef]])
    val newTaskDefs = taskDefs.filter(td => metadataClient.getTaskDef(td.getName) == null).toList
    logger.info(s"Total number of tasks = ${taskDefs.length}, number of new tasks = ${newTaskDefs.length}")

    if (newTaskDefs.nonEmpty) {
      try {
        logger.info("Registering task definition...")
        val now = System.currentTimeMillis()
        metadataClient.registerTaskDefs(newTaskDefs.asJava)
        logger.info(s"Registration completed in ${System.currentTimeMillis() - now} ms")
      } catch {
        case e: Exception =>
          logger.error(s"Failed task definition registration: ${e.getMessage}")
          sys.exit(-1)
      }
    }

    val workflowAsText = Source.fromResource("workflowDefinition.json").mkString
    val workflowDef = objectMapper.readValue(workflowAsText, classOf[WorkflowDef])
    if (metadataClient.getWorkflowDef(workflowDef.getName, workflowDef.getVersion) == null) {
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
    logger.info("<==loadTaskAndWFDefinitions")
  }

  def runWorkers(): Unit = {
    logger.info("==>runWorkers")
    val taskClient = new TaskClient()
    taskClient.setRootURI(rootURI)

    val threadCount = 20
    val worker1 = new Worker1(workCounter)

    // Create workflow coordinator
    System.setProperty("conductor.worker.pollInterval", "500")
    //    System.setProperty("conductor.worker.pollInterval", "1000") // Default
    val builder = new WorkflowTaskCoordinator.Builder
    val coordinator = builder
      .withWorkers(worker1)
      .withThreadCount(threadCount)
      .withTaskClient(taskClient)
      .build()

    //Start for polling and execution of the tasks
    coordinator.init()

    logger.info("<==runWorkers")
  }

  def runWorkflow(): Unit = {
    logger.info("==>runWorkflow")
    try {
      val workflowClient = new WorkflowClient()
      workflowClient.setRootURI(rootURI)
      val startWorkflowRequest = new StartWorkflowRequest()
      startWorkflowRequest.setName("Load_test_1_workflow")
      startWorkflowRequest.setVersion(1)
      val inputData = new util.HashMap[String, Any]()
      inputData.put("sourceRequestId", sourceRequestId.getAndIncrement())
      inputData.put("qcElementType", "big")
      startWorkflowRequest.getInput.put("eventData", inputData)
      val workflowId = workflowClient.startWorkflow(startWorkflowRequest)
      logger.info(s"Started workflow with id $workflowId")
    } catch {
      case ex: Exception =>
        logger.error(ex.getMessage, ex)
    }
    logger.info("<==runWorkflow")
  }

  def workExecutionWatcher(n: Int): Unit = {
    val start = System.currentTimeMillis()
    while (workCounter.get() <= n - 1) {
      Thread.sleep(500)
    }
    logger.info(s"Work execution of $n requests took ${System.currentTimeMillis() - start} ms.")
  }

}
