name := """SalonSpaManager"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23",
  //"org.webjars" %% "webjars-play" % "2.3.0-2",
  "org.webjars.bower" % "angular" % "1.3.15",
  "org.webjars.bower" % "angular-animate" % "1.3.15",
  "org.webjars.bower" % "angular-route" % "1.3.15",
  "org.webjars.bower" % "angular-resource" % "1.3.15",
  "org.webjars" % "bootstrap" % "3.3.4"
)
