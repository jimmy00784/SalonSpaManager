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
  "org.webjars" % "angular-ui-bootstrap" % "0.12.1-1",
  "org.webjars" % "jquery" % "2.1.4",
  "org.webjars" % "Eonasdan-bootstrap-datetimepicker" % "4.7.14",
  "org.webjars" % "momentjs" % "2.10.2"
)
