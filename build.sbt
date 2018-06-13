organization := "io.kontainers"

name := "play-utils"

scalaVersion := "2.12.4"

scalacOptions += "-target:jvm-1.8"

checksums in update := Nil

val playVersion = "2.6.13"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % playVersion,
  "com.typesafe.play" %% "play-json" % "2.6.9",
  "com.typesafe.play" %% "play-slick" % "3.0.1",
  "com.typesafe" % "config" % "1.3.3",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "com.typesafe.play" %% "play-test" % playVersion % Test,
  "org.mockito" % "mockito-core" % "2.18.3" % Test,
  "com.h2database" % "h2" % "1.4.197" % Test,
  "ch.qos.logback" % "logback-classic" % "1.2.3" % Test
)

fork := true
parallelExecution := false

javaOptions in Test += "-Duser.timezone=UTC"

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oD")

scalastyleFailOnError := true
scalastyleFailOnWarning := true

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

homepage := Some(url("https://github.com/kontainers/play-utils"))

licenses := Seq("The Apache Software License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

releasePublishArtifactsAction := PgpKeys.publishSigned.value

publishConfiguration := publishConfiguration.value.withOverwrite(true)

pomExtra := (
  <scm>
    <url>git@github.com:kontainers/play-utils.git</url>
    <connection>scm:git:git@github.com:kontainers/play-utils.git</connection>
  </scm>
  <developers>
    <developer>
      <id>pjfanning</id>
      <name>PJ Fanning</name>
      <url>https://github.com/pjfanning</url>
    </developer>
  </developers>
)
