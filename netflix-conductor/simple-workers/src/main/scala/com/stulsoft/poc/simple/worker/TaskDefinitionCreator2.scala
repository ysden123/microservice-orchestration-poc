/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.poc.simple.worker

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.conductor.client.http.MetadataClient
import com.netflix.conductor.common.metadata.tasks.TaskDef
import com.netflix.conductor.common.metadata.workflow.WorkflowDef
import com.typesafe.scalalogging.LazyLogging

import scala.io.Source
import scala.jdk.CollectionConverters._

/** Register task definition and work flow definition.
 *
 * @author Yuriy Stul
 */
object TaskDefinitionCreator2 extends LazyLogging {
  def main(args: Array[String]): Unit = {
    logger.info("==>main")
    val metadataClient = new MetadataClient()
    metadataClient.setRootURI("http://localhost:8080/api/")

    val objectMapper = new ObjectMapper()

    val taskDefsAsText = Source.fromResource("taskDefinition.json").mkString
    val taskDefs = objectMapper.readValue(taskDefsAsText, classOf[Array[TaskDef]])
    try {
      logger.info("Registering task definition...")
      val now = System.currentTimeMillis()
      metadataClient.registerTaskDefs(taskDefs.toList.asJava)
      logger.info(s"Registration completed in ${System.currentTimeMillis() - now} ms")
    } catch {
      case e: Exception =>
        logger.error(s"Failed task definition registration: ${e.getMessage}")
        sys.exit(-1)
    }

    val workflowAsText = Source.fromResource("workflowDefinition.json").mkString
    val workflowDef = objectMapper.readValue(workflowAsText, classOf[WorkflowDef])
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
