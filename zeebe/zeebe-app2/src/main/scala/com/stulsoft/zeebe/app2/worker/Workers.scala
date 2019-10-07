/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.zeebe.app2.worker

import com.stulsoft.zeebe.app2.AppConfig
import io.zeebe.client.ZeebeClient

/** Workers
 *
 * @author Yuriy Stul
 */
object Workers extends App{
  println("Workers are started.")
  var zeebeClient: ZeebeClient = _
  try {
    zeebeClient = ZeebeClient.newClientBuilder()
      .brokerContactPoint(s"${AppConfig.zeebeHost}:${AppConfig.zeebePort}")
      .build()

    // register worker 'perform-payment'
    val jobType1 = "perform-payment"
    println(s"Register $jobType1")
    zeebeClient.newWorker()
      .jobType(jobType1)
      .handler((jobClient, job) => {
        println(s"$jobType1: Performing payment ...")
        println(s"$jobType1: Data:")
        job.getVariablesAsMap.forEach((key, value) => println(s"$jobType1: $key -> $value"))

        // add data
        val newData = new java.util.HashMap[String, Any]()
        newData.put("paymentId", 456)
        newData.put("sum", 11.07)
        newData.put("index", 789)
        newData.put("undeclared", "some test text") // Missing in BPMN as output parameter, will be invisible in the next tasks!
        jobClient.newCompleteCommand(job.getKey)
          .variables(newData)
          .send()
          .join()
      })
      .open()

    // register worker 'update-db'
    val jobType2 = "update-db"
    println(s"Register $jobType2")
    zeebeClient.newWorker()
      .jobType(jobType2)
      .handler((jobClient, job) => {
        println(s"$jobType2: Updating DB ...")
        println(s"$jobType2: All data:")
        job.getVariablesAsMap.forEach((key, value) => println(s"$jobType2: $key -> $value"))

        println(s"$jobType2: Data:")
        val paymentId = job.getVariablesAsMap.get("paymentId")
        val sum = job.getVariablesAsMap.get("sum")
        println(s"$jobType2: Input parameters: paymentId=$paymentId, sum=$sum")

        val newData = new java.util.HashMap[String, Any]()
        newData.put("result", s"done ${System.currentTimeMillis()}")
        jobClient.newCompleteCommand(job.getKey)
          .variables(newData)
          .send()
          .join()
      })
      .open()
  } catch {
    case ex: Exception =>
      println(s"Failure: ${ex.getMessage}")
      if (zeebeClient != null)
        zeebeClient.close()
  }

  sys.addShutdownHook({
    println("Closing client...")
    if (zeebeClient != null)
      zeebeClient.close()
  })
}
