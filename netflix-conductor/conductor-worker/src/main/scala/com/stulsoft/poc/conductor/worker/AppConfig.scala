/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.poc.conductor.worker

import java.io.File

import com.typesafe.config.ConfigFactory

/**
 * @author Yuriy Stul
 */
object AppConfig {
  private lazy val config = ConfigFactory.parseFile(new File("application.conf"))
    .withFallback(ConfigFactory.load())

  def rootUri(): String = {
    s"""http://${config.getConfig("conductor").getString("host")}:${config.getConfig("conductor").getInt("port")}/api/"""
  }

  def threadCounter(): Int = {
    config.getConfig("worker").getInt("threadCounter")
  }
}
