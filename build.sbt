sbtPlugin := true

organization := "com.typesafe.sbt"

name := "sbt-typescript"

version := "1.0.0"

scalaVersion := "2.10.4"

scalacOptions += "-feature"

resolvers ++= Seq(
  "Typesafe Releases Repository" at "http://repo.typesafe.com/typesafe/releases/",
  Resolver.url("sbt snapshot plugins", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots"))(Resolver.ivyStylePatterns),
  Resolver.sonatypeRepo("snapshots"),
  "Typesafe Snapshots Repository" at "http://repo.typesafe.com/typesafe/snapshots/"
)

libraryDependencies += "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.3"

lazy val jsEngine = uri("https://github.com/sbt/sbt-js-engine.git")

lazy val root = project.in( file(".") ).dependsOn( jsEngine )

publishMavenStyle := false

publishTo := {
  if (isSnapshot.value) Some(Classpaths.sbtPluginSnapshots)
  else Some(Classpaths.sbtPluginReleases)
}

scriptedSettings

scriptedLaunchOpts <+= version apply { v => s"-Dproject.version=$v" }

