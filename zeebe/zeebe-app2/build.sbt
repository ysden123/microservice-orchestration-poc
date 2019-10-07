import sbt.Keys.libraryDependencies

lazy val zeebeClientVersion = "0.20.0"
lazy val scalaTestVersion = "3.0.8"
lazy val scalaCheckVersion = "1.14.0"
lazy val scalaLoggingVersion = "3.9.2"
lazy val scalaMockVersion = "4.3.0"
lazy val loggingVersion="2.12.1"
lazy val configVersion = "1.3.4"
lazy val projectVersion = "0.0.1"
lazy val projectName = "zeebe-app2"

lazy val commonSettings = Seq(
  organization := "com.stulsoft.zeebe",
  version := projectVersion,
  javacOptions ++= Seq("-source", "1.8"),
  scalaVersion := "2.13.1",
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-language:implicitConversions",
    "-language:postfixOps"),
  libraryDependencies ++= Seq(
    "com.typesafe" % "config" % configVersion,
    "io.zeebe" % "zeebe-client-java" % zeebeClientVersion
  )
)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += Resolver.jcenterRepo

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := projectName
  ).settings(
  assemblyJarName in assembly := projectName + "-" + projectVersion + ".jar",
  mainClass in assembly := Some("com.stulsoft.zeebe.app2.worker.Workers")
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
