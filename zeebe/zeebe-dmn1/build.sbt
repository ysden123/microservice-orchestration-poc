import sbt.Keys.libraryDependencies

lazy val zeebeClientVersion = "0.20.0"
lazy val dmnEngineStandaloneVersion = "1.4.0"
lazy val configVersion = "1.3.4"
lazy val scalaTestVersion = "3.0.8"
lazy val scalaLoggingVersion = "3.9.2"
lazy val loggingVersion = "2.12.0"

lazy val projectVersion = "1.0.0"
lazy val projectName = "zeebe-dmn1"

lazy val commonSettings = Seq(
  organization := "com.stulsoft",
  version := projectVersion,
  javacOptions ++= Seq("-source", "1.8"),
  scalaVersion := "2.12.8",
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-language:implicitConversions",
    "-language:postfixOps"),
  libraryDependencies ++= Seq(
    "io.zeebe" % "zeebe-client-java" % zeebeClientVersion,
    "org.camunda.bpm.extension.dmn.scala" % "dmn-engine-standalone" % dmnEngineStandaloneVersion,
    "com.typesafe" % "config" % configVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "org.apache.logging.log4j" % "log4j-api" % loggingVersion,
    "org.apache.logging.log4j" % "log4j-core" % loggingVersion,
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % loggingVersion,
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
  )
)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += Resolver.jcenterRepo

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := projectName,
    mainClass in Compile := Some("com.stulsoft.zeebe.dmn1.Main")
  )
  .enablePlugins(JavaAppPackaging)
