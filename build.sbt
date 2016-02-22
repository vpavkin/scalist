lazy val buildSettings = Seq(
  organization := "ru.vpavkin",
  scalaVersion := "2.11.7",
  crossScalaVersions := Seq("2.11.7", "2.12.0-M3")
)

lazy val compilerOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-unchecked",
  "-Xfatal-warnings",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused-import",
  "-Xfuture"
)

lazy val baseSettings = Seq(
  scalacOptions ++= compilerOptions,
  scalacOptions in(Compile, console) := compilerOptions,
  scalacOptions in(Compile, test) := compilerOptions,
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
lazy val scalaCheckVersion = "1.12.5"
lazy val scalaTestVersion = "2.2.6"

lazy val todoistAPI = project.in(file("."))
  .settings(allSettings)
  .aggregate(core, dispatch, circe, dispatchCirce, tests)
  .dependsOn(core, dispatch, circe, dispatchCirce, tests)

lazy val core = project.in(file("core"))
  .settings(
    description := "Scalist core",
    moduleName := "scalist-core",
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
    description := "Circe JSON support for Scalist",
    moduleName := "scalist-circe",
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
    description := "Dispatch HTTP support for Scalist",
    moduleName := "scalist-dispatch",
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
    description := "Todoist API client backed by Circe JSON and Dispatch HTTP",
    moduleName := "scalist-dispatch-circe",
    name := "dispatch-circe"
  )
  .settings(allSettings: _*)
  .dependsOn(core, dispatch, circe)

lazy val tests = project.in(file("tests"))
  .settings(
    description := "Scalist tests",
    moduleName := "scalist-tests",
    name := "scalist-tests"
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
