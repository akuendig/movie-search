name := "movie-search"

organization  := "com.akuendig.movie"

version       := "0.0.1"

scalaVersion  := "2.10.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

//unmanagedResourceDirectories in Compile <++= baseDirectory { base =>
//    Seq( base / "src/main/webapp/dist" )
//}

resolvers ++= Seq(
  "Spray Releases" at "http://repo.spray.io/",
  "Spray Nightlies" at "http://nightlies.spray.io/",
  "Eligosource Snapshots" at "http://repo.eligotech.com/nexus/content/repositories/eligosource-snapshots"
)

libraryDependencies ++= {
    val sprayVersion = "1.2-20130801"
    val akkaVersion  = "2.2.0"
    Seq(
      "io.spray"                %   "spray-can"         			% sprayVersion,
      "io.spray"                %   "spray-client"         			% sprayVersion,
      "io.spray"                %   "spray-routing"     			% sprayVersion,
      "io.spray"                %   "spray-testkit"     			% sprayVersion,
      "com.typesafe.akka"       %%  "akka-actor"        			% akkaVersion,
      "org.eligosource" 		%% 	"eventsourced-core" 			% "0.6-SNAPSHOT",
      "org.eligosource" 		%% 	"eventsourced-journal-leveldb" 	% "0.6-SNAPSHOT",
      "org.json4s"              %%  "json4s-jackson"                % "3.2.2",
      "org.specs2"              %%  "specs2"            			% "1.13" % "test",
      "org.scalatest"           %   "scalatest_2.10"    			% "2.0.M5b" % "test",
      "org.seleniumhq.selenium" %   "selenium-java"     			% "2.28.0" % "test"
    )
}

seq(Revolver.settings: _*)

// seq(Twirl.settings: _*)
