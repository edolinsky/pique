name := """pique"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

import sbtprotobuf.{ProtobufPlugin=>PB}

PB.protobufSettings

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "redis.clients" % "jedis" % "2.9.0",
  "com.google.code.gson" % "gson" % "2.8.0"
)


fork in run := true

