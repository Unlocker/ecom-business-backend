import scala.collection.immutable.Seq
import _root_.sbt._

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := Scala.v213
ThisBuild / organization := "com.ecom"
ThisBuild / organizationName := "ecom"
ThisBuild / name := "point"


val commonSettings = Seq(
  scalacOptions -= "-Xfatal-warnings",
  scalacOptions += "-Ymacro-annotations",
  run / fork := true,
  resolvers ++= Seq(
    Resolver.mavenLocal,
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
  ),
  libraryDependencies ++= Seq(
    "dev.zio" %% "zio-json" % Dependencies.zioJsonVersion,
    "dev.zio" %% "zio" % Dependencies.zioVersion,
    "dev.zio" %% "zio-prelude" % Dependencies.zioPrelude,
    "dev.zio" %% "zio-http" % Dependencies.zioHttpVersion,
    "io.getquill" %% "quill-jdbc-zio" % Dependencies.quillVersion,
    "org.postgresql" % "postgresql" % Dependencies.postgresVersion,
    "com.github.jwt-scala" %% "jwt-zio-json" % Dependencies.jwtZioVersion,
    "dev.zio" %% "zio-config" % Dependencies.zioConfigVersion,
    "dev.zio" %% "zio-schema" % Dependencies.zioSchemaVersion,
//    "dev.zio" %% "zio-schema-derivation" % Dependencies.zioSchemaVersion,
    "dev.zio" %% "zio-config-magnolia" % Dependencies.zioConfigVersion,
    "dev.zio" %% "zio-config-typesafe" % Dependencies.zioConfigVersion,
    "com.beachape" %% "enumeratum" % "1.7.3",
    "dev.zio" %% "zio-logging" % Dependencies.zioLoggingVersion,
    "ch.qos.logback" % "logback-classic" % Dependencies.logback,
    "io.github.scottweaver" %% "zio-2-0-testcontainers-postgresql" % TestDependencies.zioTestContainer % Test,
    "io.github.scottweaver" %% "zio-2-0-db-migration-aspect" % TestDependencies.zioTestContainer % Test,
    "dev.zio" %% "zio-test" % TestDependencies.zioVersion % Test,
    "dev.zio" %% "zio-test-sbt" % TestDependencies.zioVersion % Test
  
  )
)

lazy val root = (project in file("."))
  .settings(
    name := "app",
    commonSettings
  )

lazy val flyway = (project in file("modules/flyway"))
    .enablePlugins(FlywayPlugin)
    .settings(
    name := "flyway",
    version := "0.1.0-SNAPSHOT"
  )