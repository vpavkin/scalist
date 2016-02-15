lazy val buildSettings = Seq(
  organization := "ru.vpavkin",
  scalaVersion := "2.10.6",
  crossScalaVersions := Seq("2.10.6", "2.11.7")
)

lazy val compilerOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-unchecked",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-Ywarn-unused-import"
)

lazy val baseSettings = Seq(
  scalacOptions in(Compile, console) := compilerOptions,
  scalacOptions in(Compile, test) := compilerOptions,
  libraryDependencies ++= Seq(
    compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
  ),
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val allSettings = buildSettings ++ baseSettings

lazy val todoistAPI = project.in(file("."))
  .settings(allSettings)
  .settings(libraryDependencies ++= Seq(
    "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
    "com.chuusai" %% "shapeless" % "2.2.5",
    "org.typelevel" %% "cats-core" % "0.4.1",
    "io.circe" %% "circe-core" % "0.3.0",
    "io.circe" %% "circe-generic" % "0.3.0",
    "io.circe" %% "circe-parser" % "0.3.0"
  ))



addCommandAlias("validate", ";compile;test;scalastyle")
