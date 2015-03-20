sbtPlugin := true

organization := "com.typesafe.sbt"

name := "sbt-typescript-extjs"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.4"

scalacOptions += "-feature"

resolvers ++= Seq(
  "Typesafe Releases Repository" at "http://repo.typesafe.com/typesafe/releases/",
  Resolver.url("sbt snapshot plugins", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots"))(Resolver.ivyStylePatterns),
  Resolver.sonatypeRepo("snapshots"),
  "Typesafe Snapshots Repository" at "http://repo.typesafe.com/typesafe/snapshots/"
)

libraryDependencies += "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.3"

addSbtPlugin("com.typesafe.sbt" % "sbt-js-engine" % "1.0.2")

publishMavenStyle := false

publishTo := {
  if (isSnapshot.value) Some(Classpaths.sbtPluginSnapshots)
  else Some(Classpaths.sbtPluginReleases)
}

scriptedSettings

scriptedLaunchOpts <+= version apply { v => s"-Dproject.version=$v" }

