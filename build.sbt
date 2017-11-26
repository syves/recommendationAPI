resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

lazy val http4sVersion = "0.16.5"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.4"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

libraryDependencies += "org.http4s" %% "http4s-blaze-server" % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-dsl"          % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-blaze-client" % http4sVersion

//pulls in 7.1.13 and is compatable with http4s
libraryDependencies += "io.argonaut" %% "argonaut" % "6.2"

name := "recommendationAPI"

scalaVersion := "2.12.0"
