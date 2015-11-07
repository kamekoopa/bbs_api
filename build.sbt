name := """bbs_api"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "com.h2database"     %  "h2"                           % "1.4.190",
  "org.scalikejdbc"    %% "scalikejdbc"                  % "2.2.9",
  "org.scalikejdbc"    %% "scalikejdbc-config"           % "2.2.9",
  "org.scalikejdbc"    %% "scalikejdbc-play-initializer" % "2.4.2",
  "org.flywaydb"       %% "flyway-play"                  % "2.2.0",
  "org.scalaz"         %% "scalaz-core"                  % "7.1.5",
  "com.github.xuwei-k" %% "play-json-extra"              % "0.3.0",
  specs2 % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
