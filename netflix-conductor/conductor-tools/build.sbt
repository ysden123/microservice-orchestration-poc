lazy val scalaLoggingVersion = "3.9.2"
lazy val scalaTestVersion = "3.0.8"
lazy val scalaCheckVersion = "1.14.0"
lazy val logbackVersion = "1.2.3"
lazy val conductorClientVersion = "2.15.0"

lazy val commonSettings = Seq(
  organization := "com.stulsoft",
  version := "1.1.0",
  javacOptions ++= Seq("-source", "1.8"),
  scalaVersion := "2.13.0",
  scalacOptions ++= Seq(
    "-feature",
    "-language:implicitConversions",
    "-language:postfixOps"),
  libraryDependencies ++= Seq(
    "com.netflix.conductor" % "conductor-client" % conductorClientVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "test"
  )
)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += Resolver.jcenterRepo

lazy val conductorTools = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "conductor-tools"
  )