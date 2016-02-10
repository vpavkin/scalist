name := "todoist-api-scala"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.2.5",
  "org.typelevel" %% "cats-core" % "0.4.1",
  "eu.timepit" %% "refined" % "0.3.4",
  "io.circe" %% "circe-core" % "0.3.0-SNAPSHOT",
  "io.circe" %% "circe-generic" % "0.3.0-SNAPSHOT",
  "io.circe" %% "circe-parser" % "0.3.0-SNAPSHOT"
)
