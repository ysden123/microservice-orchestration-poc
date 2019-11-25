/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.poc.load.test2

import java.util
import java.util.concurrent.atomic.AtomicInteger

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.conductor.client.http.{MetadataClient, WorkflowClient}
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
  val sourceRequestId = new AtomicInteger(0)
  val workflowClient = new WorkflowClient()
  workflowClient.setRootURI(AppConfig.rootUri())
  val workCounter = new AtomicInteger(0)

  run()

  def run(): Unit = {
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
    loadTaskAndWFDefinitions()

    val start = System.currentTimeMillis()
    val futures = ListBuffer[Future[Iterable[Long]]]()
    val n = 1500
    val threadNumber = 3
    (1 to threadNumber).foreach(_ => futures += Future(runWorkFlows(n)))

    Await.result(Future.sequence(futures), Duration.Inf)
    val times = for (
      result <- futures.toList.map(x => x.value);
      time <- result.get.get
    ) yield time

    val total = times.sum
    val size = times.length

    val mean = if (size > 0) 1.0 * total / size else 0.0
    val speed = if (total > 0) 1000.0 * size / total else 0.0
    val min = times.min
    val max = times.max
    val deviation = if (size > 0) 1.0 * times.map(t => Math.abs(t - mean)).sum * 100.0/ (mean *size) else 0.0

    logger.info(s"Number of requests: $size, number of threads: $threadNumber")
    logger.info(s"Test duration: ${System.currentTimeMillis() - start} ms.")
    logger.info(f"Mean = $mean%.3f ms/request, min =$min ms/request, max = $max ms/request, deviation = $deviation%.3f%%, speed = $speed%.3f requests/second")
  }

  def loadTaskAndWFDefinitions(): Unit = {
    logger.info("==>loadTaskAndWFDefinitions")
    val metadataClient = new MetadataClient()
    metadataClient.setRootURI(AppConfig.rootUri())

    val objectMapper = new ObjectMapper()

    val taskDefsAsText = Source.fromResource("taskDefinition.json").mkString
    val taskDefs = objectMapper.readValue(taskDefsAsText, classOf[Array[TaskDef]])
    val newTaskDefs = taskDefs.filter(td => {
      try {
        // Throws exception if task doesn't exist.
        metadataClient.getTaskDef(td.getName) == null
      } catch {
        case _: Exception =>
          true
      }
    }).toList
    logger.info(s"Total number of tasks = ${taskDefs.length}, number of new tasks = ${newTaskDefs.length}")

    if (newTaskDefs.nonEmpty) {
      try {
        logger.info("Registering task definition...")
        metadataClient.registerTaskDefs(newTaskDefs.asJava)
        logger.info(s"Task definition registration completed")
      } catch {
        case e: Exception =>
          logger.error(s"Failed task definition registration: ${e.getMessage}")
          sys.exit(-1)
      }
    }

    val workflowAsText = Source.fromResource("workflowDefinition.json").mkString
    val workflowDef = objectMapper.readValue(workflowAsText, classOf[WorkflowDef])
    try {
      metadataClient.getWorkflowDef(workflowDef.getName, workflowDef.getVersion)
      logger.info(s"Workflow definition registration completed")
    } catch {
      case _: Exception =>
        try {
          logger.info("Registering workflow definition...")
          metadataClient.registerWorkflowDef(workflowDef)
          logger.info(s"Workflow definition registration completed")
        } catch {
          case e: Exception =>
            logger.error(s"Failed workflow definition registration: ${e.getMessage}")
            sys.exit(-1)
        }
    }

    logger.info("<==loadTaskAndWFDefinitions")
  }

  def runWorkFlow(): Long = {
    val start = System.currentTimeMillis()
    try {
      val startWorkflowRequest = new StartWorkflowRequest()
      startWorkflowRequest.setName("Load_test_1_workflow")
      startWorkflowRequest.setVersion(1)
      val inputData = new util.HashMap[String, Any]()
      inputData.put("sourceRequestId", sourceRequestId.getAndIncrement())
      inputData.put("qcElementType", "big")
      startWorkflowRequest.getInput.put("eventData", inputData)
      workflowClient.startWorkflow(startWorkflowRequest)
    } catch {
      case ex: Exception =>
        logger.error(ex.getMessage, ex)
    }
    System.currentTimeMillis() - start
  }

  def runWorkFlows(n: Int): Iterable[Long] = {
    val times = ListBuffer[Long]()

    while (workCounter.getAndIncrement() < n) {
      times += runWorkFlow()
    }

    times
  }
}
