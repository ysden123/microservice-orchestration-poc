/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.zeebe.topology

import com.stulsoft.zeebe.config.AppConfig
import com.typesafe.scalalogging.LazyLogging
import io.zeebe.client.ZeebeClient
import io.zeebe.client.api.response.Topology

/**
 * @author Yuriy Stul
 */
object ShowTopology extends App with LazyLogging {
  logger.info("Zebbe topology")

  var zeebeClient: ZeebeClient = _
  try {
    zeebeClient = ZeebeClient.newClientBuilder()
      .brokerContactPoint(s"${AppConfig.zeebeHost}:${AppConfig.zeebePort}")
      .build()

    val topology: Topology = zeebeClient.newTopologyRequest().send().join()
    logger.info("Topology:")
    logger.info(s"Cluster size: ${topology.getClusterSize}, partition count: ${topology.getPartitionsCount}, replication factor: ${topology.getReplicationFactor}")

    val brokers = topology.getBrokers
    logger.info(s"Brokers (${brokers.size()}):")
    brokers
      .forEach(broker => {
        logger.info(s"Address: ${broker.getAddress}, host: ${broker.getHost}, port: ${broker.getPort}, nodeId: ${broker.getNodeId}")

        val partitions = broker.getPartitions
        logger.info(s"Partitions (${partitions.size()}):")
        partitions.forEach(partition => {
          logger.info(s"Partition id: ${partition.getPartitionId}, role: ${partition.getRole}")
        })
      })
  } catch {
    case ex: Exception =>
      logger.error(s"Failure: ${ex.getMessage}", ex)
  } finally {
    if (zeebeClient != null)
      zeebeClient.close()
  }
}
