import sbt._
import Keys._

object BuildSettings {
  val buildOrganization = "com.akuendig"
  val buildVersion      = "0.2.0"
  val buildScalaVersion = "2.10.3"

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := buildOrganization,
    version := buildVersion,
    scalaVersion := buildScalaVersion,
    scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature", "-target:jvm-1.7")
  )
}

object Resolvers {
  val springReleasesRepo = "Spray Releases" at "http://repo.spray.io/"
  val springNightlyRepo  = "Spray Nightlies" at "http://nightlies.spray.io/"

  val typesafeRepo = "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

  val eligosourceReleasesRepo  =
    "Eligosource Releases" at "http://repo.eligotech.com/nexus/content/repositories/eligosource-releases/"
  val eligosourceSnapshotsRepo =
    "Eligosource Snapshots" at "http://repo.eligotech.com/nexus/content/repositories/eligosource-snapshots/"

  val sonatypeSnapshotsRepo =
    "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
}

object Versions {
  val sprayVersion = "1.2.0"
  val akkaVersion  = "2.2.3"
}

object Dependencies {

  import Versions._

  // compile dependencies
  val sprayCan     = "io.spray" % "spray-can" % sprayVersion
  val sprayClient  = "io.spray" % "spray-client" % sprayVersion
  val sprayRouting = "io.spray" % "spray-routing" % sprayVersion
  val sprayTestKit = "io.spray" % "spray-testkit" % sprayVersion % "test"

  val akkaActor   = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaContrib = "com.typesafe.akka" %% "akka-contrib" % akkaVersion
  val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"

  val scalaStm = "org.scala-stm" %% "scala-stm" % "0.7"
  val scalaArm = "com.jsuereth" %% "scala-arm" % "1.3"

  val slf4jApi = "org.slf4j" % "slf4j-api" % "1.7.5"

  // Required such that all annotations used by trueVFS are on the classpath during
  // scalac compilation. Otherwise the compiler throws "bad constant pool" exceptions.
  val findbugs = "com.google.code.findbugs" % "jsr305" % "2.0.3"

  // Required such that all annotations used by trueVFS are on the classpath during
  // scalac compilation. Otherwise the compiler throws "bad constant pool" exceptions.
  val trueVFSKernelSpec    = "net.java.truevfs" % "truevfs-kernel-spec" % "0.10.5"
  // Provides the path API
  val trueVFSAccess        = "net.java.truevfs" % "truevfs-access" % "0.10.5"
  val trueVFSAccessSwing   = "net.java.truevfs" % "truevfs-access-swing" % "0.10.5"
  // Basic profile containing the kernel and a zip driver.
  val trueVFSKernel        = "net.java.truevfs" % "truevfs-kernel-impl" % "0.10.5"
  val trueVFSFile          = "net.java.truevfs" % "truevfs-driver-file" % "0.10.5"
  val trueVFSZip           = "net.java.truevfs" % "truevfs-driver-zip" % "0.10.5"
  val trueVFSCompZip       = "net.java.truevfs" % "truevfs-comp-zip" % "0.10.5"
  val trueVFSCompZipDriver = "net.java.truevfs" % "truevfs-comp-zipdriver" % "0.10.5"
  val trueVFSCompIBM       = "net.java.truevfs" % "truevfs-comp-ibm437" % "0.10.5"

  val trueVFSShed = "net.java.truecommons" % "truecommons-shed" % "2.3.3"
  val trueVFSCio = "net.java.truecommons" % "truecommons-cio" % "2.3.3"
  val trueVFSIo = "net.java.truecommons" % "truecommons-io" % "2.3.3"

  val trueVFSKeySpec    = "net.java.truecommons" % "truecommons-key-spec" % "2.3.3"
  val trueVFSKeyConsole = "net.java.truecommons" % "truecommons-key-console" % "2.3.3"
  val trueVFSKeyDefault = "net.java.truecommons" % "truecommons-key-default" % "2.3.3"
  val trueVFSKeySwing   = "net.java.truecommons" % "truecommons-key-swing" % "2.3.3"
  val trueVFSKeyOSX     = "net.java.truecommons" % "truecommons-key-macosx" % "2.3.3"

  val json4sJackson = "org.json4s" %% "json4s-jackson" % "3.2.6"

  val reactiveMongo = ("org.reactivemongo" %% "reactivemongo" % "0.10.0")
    .exclude("org.scala-stm", "scala-stm_2.10.0")

  val specs2 = "org.specs2" %% "specs2" % "1.13" % "test"

  val scalaTest = "org.scalatest" %% "scalatest" % "2.0.M5b" % "test"

  val selenium = "org.seleniumhq.selenium" % "selenium-java" % "2.28.0" % "test"
}

object MovieSearchBuild extends Build {

  import BuildSettings._
  import Resolvers._
  import Dependencies._

  lazy val versionReport = TaskKey[String]("version-report")

  // Add this setting to your project.
  lazy val example = Project(
    "movie-search",
    file("."),
    settings = buildSettings ++ Seq(
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
        slf4jApi, json4sJackson,
        findbugs,
        trueVFSShed, trueVFSIo, trueVFSCio, trueVFSKernel, trueVFSKernelSpec,
        trueVFSFile, trueVFSZip,
        trueVFSCompZip, trueVFSCompZipDriver, trueVFSCompIBM,
        trueVFSAccess, trueVFSAccessSwing,
        trueVFSKeySpec, trueVFSKeyConsole, trueVFSKeyDefault, trueVFSKeySwing, trueVFSKeyOSX,
        reactiveMongo),
      // compile dependencies (frontend)
      libraryDependencies ++= Seq(sprayCan, sprayClient, sprayRouting),
      // test dependencies
      libraryDependencies ++= Seq(sprayTestKit, akkaTestKit, specs2, scalaTest, selenium)
    )
  )
}
