/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.zeebe.dmn1.worker

import scala.collection.JavaConverters._
import org.camunda.dmn.standalone.StandaloneEngine
import org.camunda.dmn.DmnEngine._
import java.util.Collections

import com.typesafe.scalalogging.LazyLogging
import io.zeebe.client.api.response.ActivatedJob
import io.zeebe.client.api.worker.{JobClient, JobHandler}

/**
 * @author Yuriy Stul
 */
class DmnJobHandler(engine: StandaloneEngine) extends JobHandler with LazyLogging {

  override def handle(client: JobClient, job: ActivatedJob) {
    Option(job.getCustomHeaders.get("decisionRef")).map(_.toString) match {
      case None => error("missing custom header 'decisionRef'")
      case Some(decisionId) => {
        val variables: Map[String, Any] = job.getVariablesAsMap.asScala.toMap

        engine.evalDecisionById(decisionId, variables) match {
          case Left(Failure(msg)) =>
            error(s"Fail to evaluate decision '$decisionId': $msg")
          case Right(NilResult) => complete(client, job, null)
          case Right(Result(r)) => complete(client, job, r)
        }
      }
    }
  }

  private def complete(client: JobClient, job: ActivatedJob, result: Any) {
    client
      .newCompleteCommand(job.getKey)
      .variables(Collections.singletonMap("result", result))
      .send()
  }

  private def error(failure: String) {
    // the exception mark the job as failed
    logger.error(failure)
    throw new RuntimeException(failure)
  }

}
