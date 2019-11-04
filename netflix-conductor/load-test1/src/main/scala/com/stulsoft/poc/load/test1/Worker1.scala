/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.poc.load.test1

import java.util
import java.util.concurrent.atomic.AtomicInteger

import com.netflix.conductor.client.worker.Worker
import com.netflix.conductor.common.metadata.tasks.TaskResult.Status
import com.netflix.conductor.common.metadata.tasks.{Task, TaskResult}
import com.typesafe.scalalogging.{LazyLogging, StrictLogging}

/**
 * @author Yuriy Stul
 */
class Worker1(workCounter:AtomicInteger) extends Worker with StrictLogging {
  override def getTaskDefName: String = "loadTest1Task"

  override def execute(task: Task): TaskResult = {
    logger.debug(s"task.getInputData: ${task.getInputData}")
    task.getInputData.forEach((k, v) => logger.info(s"Key: $k, Value: $v"))
    val eventData: java.util.Map[String, Any] = task.getInputData.get("eventData").asInstanceOf[java.util.Map[String, Any]]
    val result = new TaskResult(task)
    logger.debug(s"eventData: $eventData")
    if (eventData != null) {
      eventData.forEach((k, v) => logger.debug(s"$k -> $v"))

      //Register the output of the task
      val resultData = new util.HashMap[String, Any]()
      resultData.put("state", "the state value")
      resultData.put("skipped", "no")
      resultData.put("result", "the result")
      result.getOutputData.put("resultData", resultData)

      result.setStatus(Status.COMPLETED)
      workCounter.getAndIncrement()
    }else{
      result.setStatus(Status.FAILED)
    }
    result
  }
}
