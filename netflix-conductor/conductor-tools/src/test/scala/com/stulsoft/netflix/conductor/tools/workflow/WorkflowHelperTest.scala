/*
 * Copyright (c) 2019. Yuriy Stul
 */

/**
 * @author Yuriy Stul
 */
package com.stulsoft.netflix.conductor.tools.workflow

import java.util

import org.scalatest.{BeforeAndAfter, FunSuite, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class WorkflowHelperTest extends FunSuite with Matchers with BeforeAndAfter {
  var workflowHelper: WorkflowHelper = _
  before {
    workflowHelper = new WorkflowHelper("http://localhost:8888/api/", 20, 100)
  }

  test("testRunWorkflowAsync") {
    val result = Await.result(workflowHelper.runWorkflowAsync("test", 1, new util.HashMap[String, Any]()),
      Duration.Inf)
    result shouldBe a[Left[String, Any]]
  }

  test("testRunWorkflowSync") {
    val result = workflowHelper.runWorkflowSync("test", 1, new util.HashMap[String, Any]())
    result shouldBe a[Left[String, Any]]
  }

}
