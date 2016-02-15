lazy val buildSettings = Seq(
  organization := "ru.vpavkin",
  scalaVersion := "2.11.7",
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
  .aggregate(core, dispatch, circe, dispatchCirce)

lazy val core = project.in(file("core"))
  .settings(
    description := "todoist api core",
    moduleName := "todoist-api-core",
    name := "core"
  )
  .settings(allSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % "2.2.5",
      "org.typelevel" %% "cats-core" % "0.4.1"
    )
  )

lazy val circe = project.in(file("circe"))
  .settings(
    description := "circe json support for todoist-api-scala",
    moduleName := "todoist-api-circe",
    name := "circe"
  )
  .settings(allSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.3.0",
      "io.circe" %% "circe-generic" % "0.3.0",
      "io.circe" %% "circe-parser" % "0.3.0"
    )
  ).dependsOn(core)

lazy val dispatch = project.in(file("dispatch"))
  .settings(
    description := "dispatch http support for todoist-api-scala",
    moduleName := "todoist-api-dispatch",
    name := "dispatch"
  )
  .settings(allSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "net.databinder.dispatch" %% "dispatch-core" % "0.11.2"
    )
  ).dependsOn(core)

lazy val dispatchCirce = project.in(file("dispatch-circe"))
  .settings(
    description := "todoist api backed by circe json and dispatch http",
    moduleName := "todoist-api-dispatch-circe",
    name := "dispatch-circe"
  )
  .settings(allSettings: _*)
  .dependsOn(core, dispatch, circe)

addCommandAlias("validate", ";compile;test;scalastyle")
