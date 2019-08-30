/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.netflix.conductor.tools.io

import org.scalatest.{FunSuite, Matchers}

/**
 * @author Yuriy Stul
 */
class StreamUtilsTest extends FunSuite with Matchers  {

  test("inputStreams") {
    val iss = StreamUtils.inputStreams(".sbt")
    iss.isEmpty shouldBe false
    iss.foreach(is => is.close())
  }

  test("inputStreams empty") {
    val iss = StreamUtils.inputStreams(".123")
    iss.isEmpty shouldBe true
  }
}
