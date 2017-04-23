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

/*crossScalaVersions := Seq("2.10.2", "2.10.3", "2.10.4", "2.10.5", "2.10.6", "2.11.0", "2.11.1", "2.11.2", "2.11.3", "2.11.4", "2.11.5", "2.11.6", "2.11.7", "2.11.8", "2.12.1")

run <<= run in Compile in core

lazy val macros = (project in file("macros")).settings(
  libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
)

lazy val core = (project in file("core")) dependsOn macros

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin(  "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full )

autoCompilerPlugins := true */