import sbt.Keys.libraryDependencies

lazy val scalaTestVersion = "3.0.8"
lazy val scalaCheckVersion = "1.14.0"
lazy val scalaLoggingVersion = "3.9.2"
lazy val scalaMockVersion = "4.3.0"
lazy val loggingVersion = "2.12.0"
lazy val json4sVersion = "3.6.7"
lazy val scalaXmlVersion = "1.2.0"
lazy val projectVersion = "1.0.0"
lazy val conductorClientVersion = "2.14.5"
lazy val projectName = "simple-workers"

lazy val commonSettings = Seq(
  organization := "com.stulsoft",
  version := projectVersion,
  javacOptions ++= Seq("-source", "1.8"),
  scalaVersion := "2.13.0",
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-language:implicitConversions",
    "-language:postfixOps"),
  libraryDependencies ++= Seq(
    "com.netflix.conductor" % "conductor-client" % conductorClientVersion,
    "org.json4s" %% "json4s-native" % json4sVersion,
    "org.json4s" %% "json4s-jackson" % json4sVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "org.apache.logging.log4j" % "log4j-api" % loggingVersion,
    "org.apache.logging.log4j" % "log4j-core" % loggingVersion,
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % loggingVersion,
    "org.scala-lang.modules" %% "scala-xml" % scalaXmlVersion,
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "test",
    "org.scalamock" %% "scalamock" % scalaMockVersion % "test",
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
  )
)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += Resolver.jcenterRepo

lazy val simpleWorkers = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := projectName
  )