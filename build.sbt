name := "wiki-search-bot"

version := "1.0"

scalaVersion := "2.12.3"

val Http4sVersion = "0.18.0-M1"
val LogbackVersion = "1.2.3"
val PureConfigVersion = "0.8.0"
val DoobieVersion = "0.5.0-M8"


libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core" % DoobieVersion,
  "org.xerial" % "sqlite-jdbc" % "3.20.0",
  "co.fs2" % "fs2-core_2.12" % "0.10.0-M6",
  "ch.qos.logback" % "logback-classic" % LogbackVersion,

  "com.github.pureconfig" %% "pureconfig" % PureConfigVersion,

  "io.circe" %% "circe-generic" % "0.9.0-M1",
  "org.http4s" %% "http4s-circe" % Http4sVersion,
  "org.http4s" %% "http4s-dsl" % Http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
  "org.http4s" % "http4s-core_2.12" % Http4sVersion,

  "org.scalatest" % "scalatest_2.12" % "3.0.4" % "test"
)

// set main class and skip tests for assembly
test in assembly := {}
mainClass in assembly := Some("app.Server")