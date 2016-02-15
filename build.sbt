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

lazy val shapelessVersion = "2.2.5"
lazy val catsVersion = "0.4.1"
lazy val circeVersion = "0.3.0"
lazy val dispatchVersion = "0.11.2"
lazy val scalaCheckVersion = "1.13.0"
lazy val scalaTestVersion = "2.2.6"

lazy val todoistAPI = project.in(file("."))
  .settings(allSettings)
  .aggregate(core, dispatch, circe, dispatchCirce, tests)

lazy val core = project.in(file("core"))
  .settings(
    description := "todoist api core",
    moduleName := "todoist-api-core",
    name := "core"
  )
  .settings(allSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % shapelessVersion,
      "org.typelevel" %% "cats-core" % catsVersion
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
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion
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
      "net.databinder.dispatch" %% "dispatch-core" % dispatchVersion
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

lazy val tests = project.in(file("tests"))
  .settings(
    description := "todoist api tests",
    moduleName := "todoist-api-tests",
    name := "todoist-api-tests"
  )
  .settings(allSettings: _*)
  .settings(libraryDependencies ++= Seq(
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "test",
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
  ))
  .settings(
    ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages := "ru\\.pavkin\\.todoist\\.api\\.tests\\..*"
  )
  .settings(
    fork := true
  )
  .dependsOn(
    core,
    dispatch,
    circe,
    dispatchCirce
  )

addCommandAlias("validate", ";compile;test;scalastyle")
