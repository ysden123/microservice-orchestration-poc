/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.netflix.conductor.tools

import java.io.FileInputStream

import com.netflix.conductor.common.metadata.tasks.TaskDef
import org.scalatest.{FunSuite, Matchers}

import scala.util.{Failure, Success}

/**
 * @author Yuriy Stul
 */
class MetadataHelperTest extends FunSuite with Matchers {

  test("taskDefFromStream - one task") {
    val taskDefs = MetadataHelper.taskDefFromStream(new FileInputStream("src/test/resources/taskDefinition.json"))
    taskDefs.isSuccess shouldBe true
    taskDefs.get.length shouldBe 1
    taskDefs.get.head shouldBe a[TaskDef]
  }

  test("taskDefFromStream - multiple tasks") {
    val taskDefs = MetadataHelper.taskDefFromStream(new FileInputStream("src/test/resources/taskDefinitions.json"))
    taskDefs.isSuccess shouldBe true
    taskDefs.get.length shouldBe 2
    taskDefs.get.foreach(taskDef => taskDef shouldBe a[TaskDef])
  }

  test("taskDefFromStream - wrong JSON"){
    MetadataHelper.taskDefFromStream(new FileInputStream("build.sbt")).isFailure shouldBe true
  }
}
