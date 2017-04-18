name := "Parlamento"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "org.http4s" % "http4s-blaze-client_2.12" % "0.17.0-M1",
  "org.http4s" %% "http4s-scala-xml" % "0.17.0-M1",

  "org.tpolecat" %% "doobie-core-cats" % "0.4.1",
  "org.xerial" % "sqlite-jdbc" % "3.16.1"
)