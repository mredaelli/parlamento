name := "Parlamento"

version := "1.0"

scalaVersion := "2.12.2"

val http4sversion = "0.17.0-M1"

libraryDependencies ++= Seq(
  "org.http4s" % "http4s-blaze-client_2.12" % http4sversion,
  "org.http4s" %% "http4s-scala-xml" % http4sversion,
  "org.http4s" %% "http4s-circe" % http4sversion,

  "org.tpolecat" %% "doobie-core-cats" % "0.4.1",
  "org.xerial" % "sqlite-jdbc" % "3.16.1"
)

val circeVersion = "0.7.1"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)
