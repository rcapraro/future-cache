scalaVersion := "2.13.8"

name         := "future-cache"
organization := "com.capraro"
version      := "1.0"

val sttpClientVersion = "3.6.2"

libraryDependencies ++= Seq("com.softwaremill.sttp.client3" %% "core", "com.softwaremill.sttp.client3" %% "circe")
  .map(_ % sttpClientVersion)

val circeVersion = "0.14.1"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)
