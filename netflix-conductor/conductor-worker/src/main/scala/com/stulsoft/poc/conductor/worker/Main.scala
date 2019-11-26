/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.poc.conductor.worker

import com.netflix.conductor.client.http.TaskClient
import com.netflix.conductor.client.task.WorkflowTaskCoordinator
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
 * @author Yuriy Stul
 */
object Main extends App with StrictLogging {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  Await.result(Future(runWorkers()), Duration.Inf)

  def runWorkers(): Unit = {
    logger.info("==>runWorkers")
    val taskClient = new TaskClient()
    taskClient.setRootURI(AppConfig.rootUri())

    val worker1 = new Service()

    // Create workflow coordinator
    System.setProperty("conductor.worker.pollInterval", "100")
    //    System.setProperty("conductor.worker.pollInterval", "1000") // Default
    val builder = new WorkflowTaskCoordinator.Builder
    val coordinator = builder
      .withWorkers(worker1)
      .withThreadCount(AppConfig.threadCounter())
      .withTaskClient(taskClient)
      .build()

    //Start for polling and execution of the tasks
    coordinator.init()

    logger.info("<==runWorkers")
  }
}
