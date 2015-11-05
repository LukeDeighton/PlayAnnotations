val scalaVer = "2.11.7"

lazy val root: Project = (project in file("."))
  .settings(
    name := "play-validation",
    organization := "com.github.lukedeighton",
    version := "SNAPSHOT",
    scalaVersion := scalaVer,
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVer withSources() withJavadoc(),
      "com.typesafe.play" % "play_2.11" % "2.4.2",
      "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
    )
  )