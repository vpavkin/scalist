import sbtunidoc.Plugin.UnidocKeys._
import ReleaseTransformations._

lazy val buildSettings = Seq(
  organization := "ru.pavkin",
  scalaVersion := "2.11.7"
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
  "-Xfuture"
)

lazy val baseSettings = Seq(
  scalacOptions ++= compilerOptions ++ Seq(
    "-Ywarn-unused-import"
  ),
  testOptions in Test += Tests.Argument("-oF"),
  scalacOptions in(Compile, console) := compilerOptions,
  scalacOptions in(Compile, test) := compilerOptions,
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val allSettings = buildSettings ++ baseSettings ++ publishSettings

lazy val shapelessVersion = "2.2.5"
lazy val catsVersion = "0.4.1"
lazy val circeVersion = "0.3.0"
lazy val dispatchVersion = "0.11.2"
lazy val scalaCheckVersion = "1.12.5"
lazy val scalaTestVersion = "2.2.6"

lazy val scalist = project.in(file("."))
  .settings(allSettings)
  .settings(docSettings)
  .settings(noPublishSettings)
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
  .settings(noPublishSettings: _*)
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

lazy val noDocProjects: Seq[ProjectReference] = Seq.empty

lazy val docSettings = site.settings ++ ghpages.settings ++ unidocSettings ++ Seq(
  site.addMappingsToSiteDir(mappings in(ScalaUnidoc, packageDoc), "api"),
  scalacOptions in(ScalaUnidoc, unidoc) ++= Seq(
    "-groups",
    "-implicits",
    "-doc-source-url", scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
    "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath
  ),
  git.remoteRepo := "git@github.com:vpavkin/scalist.git",
  unidocProjectFilter in(ScalaUnidoc, unidoc) := (inAnyProject -- inProjects(noDocProjects: _*))
)

lazy val noPublishSettings = Seq(
  publish :=(),
  publishLocal :=(),
  publishArtifact := false
)

lazy val publishSettings = Seq(
  releaseIgnoreUntrackedFiles := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  homepage := Some(url("https://github.com/vpavkin/scalist")),
  licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  autoAPIMappings := true,
  apiURL := Some(url("https://vpavkin.github.io/scalist/api/")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/vpavkin/scalist"),
      "scm:git:git@github.com:vpavkin/scalist.git"
    )
  ),
  pomExtra :=
    <developers>
      <developer>
        <id>vpavkin</id>
        <name>Vladimir Pavkin</name>
        <url>http://pavkin.ru</url>
      </developer>
    </developers>
)

lazy val sharedReleaseProcess = Seq(
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    setNextVersion,
    commitNextVersion,
    ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
    pushChanges
  )
)

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")


addCommandAlias("validate", ";compile;test;scalastyle")
