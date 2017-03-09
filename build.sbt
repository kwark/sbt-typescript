import bintray.Keys._

sbtPlugin := true

organization := "be.venneborg.sbt"

name := "sbt-typescript"

description := "sbt plugin that transpiles typescript files to javascript classes"

version := "1.0.0"

scalaVersion := "2.10.4"

scalacOptions += "-feature"

resolvers ++= Seq(
  "Typesafe Releases Repository" at "http://repo.typesafe.com/typesafe/releases/",
  Resolver.url("sbt snapshot plugins", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots"))(Resolver.ivyStylePatterns),
  Resolver.sonatypeRepo("snapshots"),
  "Typesafe Snapshots Repository" at "http://repo.typesafe.com/typesafe/snapshots/"
)

bintrayPublishSettings

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

publishMavenStyle := false

repository in bintray := "sbt-plugins"

bintrayOrganization in bintray := None

libraryDependencies += "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.3"

addSbtPlugin("com.typesafe.sbt" % "sbt-js-engine" % "1.1.4")

scriptedSettings

scriptedLaunchOpts <+= version apply { v => s"-Dproject.version=$v" }

