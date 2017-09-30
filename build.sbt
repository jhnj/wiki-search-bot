name := "wiki-search-bot"

version := "1.0"

scalaVersion := "2.12.3"

val http4sVersion = "0.17.2"
libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core-cats" % "0.4.4",
  "org.xerial" % "sqlite-jdbc" % "3.20.0",
  "co.fs2" % "fs2-core_2.12" % "0.9.0",
  "ch.qos.logback" %  "logback-classic" % "1.2.3",

  "com.github.pureconfig" %% "pureconfig" % "0.8.0",

  "io.circe" %% "circe-generic" % "0.8.0",
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion
)