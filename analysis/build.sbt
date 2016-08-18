version in ThisBuild := {
  val major = 0
  val minor = 0
  val patch = 1
  s"$major.$minor.$patch"
}

scalaVersion in ThisBuild := "2.11.8"

javacOptions += "-Xlint:unchecked"

organization := "edu.psu.sagnik.research"

name := "vlganalysis" //vlg => vector line graphs

resolvers ++= Seq(
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Sonatype Shapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "JAI releases" at "http://maven.geotoolkit.org/"
)

libraryDependencies ++= Seq(
   "edu.psu.sagnik.research" %% "inkscapesvgprocessing" % "0.0.3",
   //jackson for json
  "org.json4s" %% "json4s-native" % "3.2.11",
  "org.json4s" %% "json4s-jackson" % "3.2.10",
  //commons io for file writing
  "commons-io" % "commons-io" % "2.4",
  //for PNG conversions
  "org.apache.xmlgraphics" % "xmlgraphics-commons" % "1.5",
  "org.apache.xmlgraphics" % "batik-transcoder" % "1.8",
  "org.apache.xmlgraphics" % "batik-codec" % "1.8",
  // testing
  "org.scalatest"        %% "scalatest"  %  "2.2.4"
 )

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings")

fork := true

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")

testOptions in Test += Tests.Argument("-oF")

fork in Test := false

parallelExecution in Test := false

