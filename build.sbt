resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

lazy val http4sVersion = "0.16.5"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.4"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

libraryDependencies += "io.argonaut" %% "argonaut" % "6.2"
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.17"

name := "recommendationAPI"

scalaVersion := "2.12.3"
