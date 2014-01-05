import sbt._
import Keys._

import spray.revolver.RevolverPlugin.Revolver
import com.typesafe.sbt.SbtAtmos

object BuildSettings {
  val buildOrganization = "com.akuendig"
  val buildVersion = "0.2.0"
  val buildScalaVersion = "2.10.3"

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := buildOrganization,
    version := buildVersion,
    scalaVersion := buildScalaVersion,
    scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature")
  )
}

object RunSettings {
  val runSettings = Seq(
    javaOptions in run += "-Xmx4G",
    javaOptions in Revolver.reStart ++= Seq(
      "-Xmx4G",
      "-server",
      "-Dcom.sun.management.jmxremote.port=8086",
      "-Dcom.sun.management.jmxremote.ssl=false",
      "-Dcom.sun.management.jmxremote.authenticate=false"
    ),
    Revolver.enableDebugging(suspend = false)
  )
}

object Resolvers {
  val springReleasesRepo = "Spray Releases" at "http://repo.spray.io/"
  val springNightlyRepo = "Spray Nightlies" at "http://nightlies.spray.io/"

  val typesafeRepo = "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

  val eligosourceReleasesRepo =
    "Eligosource Releases" at "http://repo.eligotech.com/nexus/content/repositories/eligosource-releases/"
  val eligosourceSnapshotsRepo =
    "Eligosource Snapshots" at "http://repo.eligotech.com/nexus/content/repositories/eligosource-snapshots/"

  val sonatypeSnapshotsRepo =
    "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
}

object Versions {
  val sprayVersion = "1.2.0"
  val akkaVersion = "2.2.3"
}

object Dependencies {

  import Versions._

  // compile dependencies
  val sprayCan = "io.spray" % "spray-can" % sprayVersion % "compile"
  val sprayClient = "io.spray" % "spray-client" % sprayVersion % "compile"
  val sprayRouting = "io.spray" % "spray-routing" % sprayVersion % "compile"
  val sprayTestKit = "io.spray" % "spray-testkit" % sprayVersion % "test"

  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion % "compile"
  val akkaContrib = "com.typesafe.akka" %% "akka-contrib" % akkaVersion % "compile"
  val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"

  val scalaStm = "org.scala-stm" %% "scala-stm" % "0.7" % "compile"
  val scalaArm = "com.jsuereth" %% "scala-arm" % "1.3" % "compile"

  val slf4jApi = "org.slf4j" % "slf4j-api" % "1.7.5"

  val trueZip = "de.schlichtherle.truezip" %% "truezip" % "7.7.5"

  val json4sJackson = "org.json4s" %% "json4s-jackson" % "3.2.6"

  val reactiveMongo = ("org.reactivemongo" %% "reactivemongo" % "0.10.0" % "compile")
    .exclude("org.scala-stm", "scala-stm_2.10.0")

  val specs2 = "org.specs2" %% "specs2" % "1.13" % "test"

  val scalaTest = "org.scalatest" %% "scalatest" % "2.0.M5b" % "test"

  val selenium = "org.seleniumhq.selenium" % "selenium-java" % "2.28.0" % "test"
}

object MovieSearchBuild extends Build {

  import BuildSettings._
  import RunSettings._
  import Resolvers._
  import Dependencies._

  lazy val versionReport = TaskKey[String]("version-report")

  // Add this setting to your project.
  lazy val example = Project(
    "movie-search",
    file("."),
    settings = buildSettings ++ Revolver.settings ++ runSettings ++ Seq(
      resolvers := Seq(
        springReleasesRepo, springNightlyRepo,
        typesafeRepo,
        eligosourceReleasesRepo, eligosourceSnapshotsRepo,
        sonatypeSnapshotsRepo
      ),
      // compile dependencies (backend)
      libraryDependencies ++= Seq(
        akkaActor, akkaContrib,
        scalaStm, scalaArm,
        slf4jApi, json4sJackson, reactiveMongo),
      // compile dependencies (frontend)
      libraryDependencies ++= Seq(sprayCan, sprayClient, sprayRouting),
      // test dependencies
      libraryDependencies ++= Seq(sprayTestKit, akkaTestKit, specs2, scalaTest, selenium)
    ) ++ SbtAtmos.atmosSettings // atmos settings need to be after dependencies
  ).configs(SbtAtmos.Atmos)
}
