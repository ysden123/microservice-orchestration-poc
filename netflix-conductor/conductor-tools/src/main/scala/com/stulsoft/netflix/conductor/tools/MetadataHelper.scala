/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.netflix.conductor.tools

import java.io.InputStream

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.conductor.common.metadata.tasks.TaskDef
import com.typesafe.scalalogging.LazyLogging

import scala.jdk.CollectionConverters._
import scala.util.Try

/**
 * @author Yuriy Stul
 */
object MetadataHelper extends LazyLogging {
  private val objectMapper = new ObjectMapper()

  /**
   * Returns a collection of TaskDef objects
   *
   * @param inputStream specifies a stream with JSON-formatted task definition
   * @return thecollection of TaskDef objects
   */
  def taskDefFromStream(inputStream: InputStream): Try[Seq[TaskDef]] = {
    Try {
      val rootNode = objectMapper.readTree(inputStream)
      if (!rootNode.isContainerNode) {
        val msg = "Expected JSON array or object"
        logger.error(msg)
        throw new IllegalArgumentException(msg)
      }

      if (rootNode.isArray) {
        rootNode
          .elements()
          .asScala
          .map(jsonNode => objectMapper.convertValue(jsonNode, classOf[TaskDef]))
          .toSeq
      } else if (rootNode.isObject) {
        List(objectMapper.convertValue(rootNode, classOf[TaskDef]))
      } else {
        Seq.empty[TaskDef]
      }
    }
  }
}
