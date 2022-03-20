package monarch.system.config

import pureconfig.*
import pureconfig.generic.derivation.default.*
import zio.*

trait Config:
  val dbConfig: UIO[PostgresConfig]
  val httpServer: UIO[HttpServerConfig]

object Config extends zio.Accessible[Config]
  

case class ConfigLive(
    dbConfig: UIO[PostgresConfig],
    httpServer: UIO[HttpServerConfig]
) extends Config

object ConfigLive:

  private val basePath = "monarch"
  private val source = ConfigSource.default.at(basePath)

  def layer: ULayer[Config] = ZLayer.fromZIO(
    ZIO
      .effect(source.loadOrThrow[Configuration])
      .map(c => ConfigLive(UIO(c.dbConfig), UIO(c.httpServer)))
      .orDie
  )

  