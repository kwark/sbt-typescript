organization := "be.venneborg.sbt"

description := "sbt plugin that transpiles typescript files to javascript classes"

version := "1.1.0-SNAPSHOT"

scalacOptions += "-feature"

resolvers ++= Seq(
  "Typesafe Releases Repository" at "http://repo.typesafe.com/typesafe/releases/",
  Resolver.url("sbt snapshot plugins", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots"))(Resolver.ivyStylePatterns),
  Resolver.sonatypeRepo("snapshots"),
  "Typesafe Snapshots Repository" at "http://repo.typesafe.com/typesafe/snapshots/"
)

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

publishMavenStyle := false

bintrayRepository := "sbt-plugins"

bintrayOrganization := None

//libraryDependencies += "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.3"

addSbtPlugin("com.typesafe.sbt" % "sbt-js-engine" % "1.2.3")

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-typescript-extjs",
    scriptedLaunchOpts := { scriptedLaunchOpts.value ++
      Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false
  )

