/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.poc.simple.worker

import com.netflix.conductor.client.http.TaskClient
import com.netflix.conductor.client.task.WorkflowTaskCoordinator
import com.typesafe.scalalogging.LazyLogging

/**
 * @author Yuriy Stul
 */
object Worker1Runner extends LazyLogging {
  def main(args: Array[String]): Unit = {
    logger.info("==>main")
    val taskClient = new TaskClient()
    taskClient.setRootURI("http://localhost:8080/api/")
    val threadCount = 2
    val worker1 = new Worker1("task_1")
//    val worker2 = new Worker1("task_2")
//    val worker3 = new Worker1("task_3")

    // Create workflow coordinator
    val builder = new WorkflowTaskCoordinator.Builder
    val coordinator = builder
//      .withWorkers(worker1, worker2, worker3)
      .withWorkers(worker1)
      .withThreadCount(threadCount)
      .withTaskClient(taskClient)
      .build()

    //Start for polling and execution of the tasks
    coordinator.init()

  }
}
