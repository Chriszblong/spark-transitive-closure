import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      name := "pCore",
      scalaVersion := "2.11.11",
      version      := "0.0"
    )),
    name := "spark-transitive-closure",
    autoScalaLibrary := false,
    libraryDependencies ++= Seq(
		"org.apache.spark" %% "spark-core" % "2.3.1" % "provided",
		"org.apache.spark" %% "spark-graphx" % "2.3.1" % "provided",
		"org.apache.spark" %% "spark-mllib" % "2.3.1" % "provided",
		"com.github.nscala-time" %% "nscala-time" % "2.20.0",
		"org.reactivemongo" %% "reactivemongo" % "0.15.0",
		"com.typesafe.play" %% "play-json" % "2.6.9",
		"org.scala-graph" %% "graph-core" % "1.12.1",
		"org.rogach" %% "scallop" % "3.1.1"
	)	
 )	
  assemblyMergeStrategy in assembly :={
        case PathList("META-INF",xs @ _*)=>MergeStrategy.discard
		case x=>MergeStrategy.first
 }

