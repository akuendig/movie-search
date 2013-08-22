import sbt._
import Keys._

import spray.revolver.RevolverPlugin.Revolver
//import scalabuff.ScalaBuffPlugin._
//import com.twitter.scrooge.ScroogeSBT

object BuildSettings {
  val buildOrganization = "com.akuendig"
  val buildVersion = "0.1.0"
  val buildScalaVersion = "2.10.2"

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
    javaOptions in Revolver.reStart ++= Seq("-Xmx4G", "-Dcom.sun.management.jmxremote", "-Dcom.sun.management.jmxremote.ssl=false", "-Dcom.sun.management.jmxremote.authenticate=false"),
    Revolver.enableDebugging(port = 5050, suspend = false)
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
}

object Versions {
  val sprayVersion = "1.2-20130801"
  val akkaVersion = "2.2.0"
}

object Dependencies {

  import Versions._

  // compile dependencies
  lazy val sprayCan =     "io.spray" % "spray-can"      % sprayVersion % "compile"
  lazy val sprayClient =  "io.spray" % "spray-client"   % sprayVersion % "compile"
  lazy val sprayRouting = "io.spray" % "spray-routing"  % sprayVersion % "compile"
  lazy val sprayTestKit = "io.spray" % "spray-testkit"  % sprayVersion % "test"

  lazy val akkaActor =   "com.typesafe.akka"  %% "akka-actor"   % akkaVersion % "compile"
//  lazy val akkaRemote =  "com.typesafe.akka"  %% "akka-remote"  % akkaVersion % "compile"
  lazy val akkaTestKit = "com.typesafe.akka"  %% "akka-testkit" % akkaVersion % "test"

  lazy val esCore =    "org.eligosource"  %% "eventsourced-core"            % "0.6-SNAPSHOT" % "compile"
  lazy val esJournal = "org.eligosource"  %% "eventsourced-journal-leveldb" % "0.6-SNAPSHOT" % "compile"

  lazy val scalaStm = "org.scala-stm" %% "scala-stm" % "0.7" % "compile"
  lazy val scalaArm = "com.jsuereth"  %% "scala-arm" % "1.3" % "compile"

//  lazy val thrift =        "org.apache.thrift"    % "libthrift"       % "0.9.0" % "compile"
//  lazy val scrooge =       "com.twitter"          %% "scrooge-core"   % "3.5.0" % "compile"
//  lazy val finagleThrift = "com.twitter"          %% "finagle-thrift" % "6.5.2" % "compile"

  lazy val msgpackJson = "com.googlecode.json-simple" % "json-simple" % "1.1.1" % "compile"
  lazy val msgpackJavassist = "org.javassist" % "javassist" % "3.16.1-GA" % "compile"

  //  lazy val protoBuf =      "com.google.protobuf"  % "protobuf-java"   % "2.5.0" % "compile"
  //  lazy val scalaBuff = "net.sandrogrzicic" %%  "scalabuff-compiler" % "1.1.1" % "compile"

  lazy val json4sJackson = "org.json4s" %% "json4s-jackson" % "3.2.5" % "compile"
//  lazy val json4sNative = "org.json4s" %% "json4s-native" % "3.2.5" % "compile"

  lazy val specs2 = "org.specs2" %% "specs2" % "1.13" % "test"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "2.0.M5b" % "test"

  lazy val selenium = "org.seleniumhq.selenium" % "selenium-java" % "2.28.0" % "test"
}

object MovieSearchBuild extends Build {

  import BuildSettings._
  import RunSettings._
  import Resolvers._
  import Dependencies._

  lazy val example = Project(
    "movie-search",
    file("."),
    settings = buildSettings ++ Revolver.settings ++ runSettings ++ Seq(
      resolvers := Seq(springReleasesRepo, springNightlyRepo, typesafeRepo, eligosourceReleasesRepo, eligosourceSnapshotsRepo),
      // compile dependencies (backend)
      libraryDependencies ++= Seq(akkaActor, scalaStm, scalaArm, msgpackJson, msgpackJavassist, esCore, esJournal, json4sJackson),
      // compile dependencies (frontend)
      libraryDependencies ++= Seq(sprayCan, sprayClient, sprayRouting),
      // test dependencies
      libraryDependencies ++= Seq(sprayTestKit, akkaTestKit, specs2, scalaTest, selenium)
    )
  )
}
