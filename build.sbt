val scala3Version = "3.1.2-RC1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "monarch",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, _)) => Seq("-Ykind-projector:underscores")
        case Some((2, 12 | 13)) =>
          Seq("-Xsource:3", "-P:kind-projector:underscore-placeholders")
      }
    },
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "2.0.0-RC2",
      "dev.zio" %% "zio-logging-slf4j" % "2.0.0-RC5",
      "ch.qos.logback" % "logback-classic" % "1.2.11",
      "dev.zio" %% "zio-interop-cats" % "3.3.0-RC2",
      "org.tpolecat" %% "doobie-core" % "1.0.0-RC2",
      "org.tpolecat" %% "doobie-refined" % "1.0.0-RC2",
      "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC2",
      "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC2",
      "org.flywaydb" % "flyway-core" % "8.5.1",
      "org.http4s" %% "http4s-blaze-server" % "0.23.10",
      "org.http4s" %% "http4s-dsl" % "0.23.10",
      "eu.timepit" %% "refined" % "0.9.28",
      "io.circe" %% "circe-refined" % "0.15.0-M1",
      "com.softwaremill.sttp.tapir" %% "tapir-zio-http4s-server" % "1.0.0-M1",
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.0.0-M1",
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "1.0.0-M1",
      
      "com.github.pureconfig" %% "pureconfig-core" % "0.17.1",
      "com.github.sbt" % "junit-interface" % "0.13.3" % "test"
    ) 
    /*++ Seq("com.chuusai" %% "shapeless" % "2.4.0-M1")
      .map(_.cross(CrossVersion.for3Use2_13))*/
  )
