/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.poc.simple.worker

import com.netflix.conductor.client.http.TaskClient
import com.netflix.conductor.client.task.WorkflowTaskCoordinator
import com.netflix.conductor.client.worker.Worker
import com.netflix.conductor.common.metadata.tasks.TaskResult.Status
import com.netflix.conductor.common.metadata.tasks.{Task, TaskResult}
import com.typesafe.scalalogging.LazyLogging

/**
 * @author Yuriy Stul
 */
class Worker1(taskDefName: String) extends Worker with LazyLogging {
  override def getTaskDefName: String = taskDefName

  override def execute(task: Task): TaskResult = {
    logger.info(s"Executing task: $task")
    val result = new TaskResult(task)
    result.setStatus(Status.COMPLETED)

    //Register the output of the task//Register the output of the task
    result.getOutputData.put("outputKey1", "value")
    result
  }
}

