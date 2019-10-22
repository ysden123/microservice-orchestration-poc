import sbt.Keys.libraryDependencies

lazy val zeebeClientVersion = "0.20.0"
lazy val zeebeTestVersion = "0.20.0"
lazy val configVersion = "1.3.4"
lazy val scalaTestVersion = "3.0.8"
lazy val scalaLoggingVersion = "3.9.2"
lazy val loggingVersion = "2.12.0"
lazy val junitVersion = "4.12"
lazy val junitInterface = "0.11"

lazy val projectVersion = "1.0.0"
lazy val projectName = "zeebe-test1"

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
    "com.typesafe" % "config" % configVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "org.apache.logging.log4j" % "log4j-api" % loggingVersion,
    "org.apache.logging.log4j" % "log4j-core" % loggingVersion,
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % loggingVersion,
    "junit" % "junit" % junitVersion % Test,
    "com.novocode" % "junit-interface" % junitInterface % Test,
    "io.zeebe" % "zeebe-test" % zeebeTestVersion % Test
  )
)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += Resolver.jcenterRepo

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := projectName,
    mainClass in Compile := Some("com.stulsoft.zeebe.test1.Main")
  )
  .enablePlugins(JavaAppPackaging)
