libraryDependencies <+= (sbtVersion) { sv =>
  "org.scala-sbt" % "scripted-plugin" % sv
}

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.2.1")
