package monarch

import monarch.domain.repository.CustomerRepository
import monarch.domain.repository.CustomerRepositoryLive
import monarch.domain.service.CustomerService
import monarch.domain.service.CustomerServiceLive
import monarch.http.Server
import monarch.system.config.Config
import monarch.system.config.ConfigLive
import monarch.system.db.DBTransactor
import monarch.system.db.DBTransactorLive
import monarch.system.db.FlywayAdapterLive
import monarch.Environment.AppEnv
import zio.*

import java.io.IOException
import monarch.system.db.FlywayAdapter

object Boot extends ZIOApp:

  override type Environment = AppEnv & Config & ZEnv

  override val tag: EnvironmentTag[Environment] = EnvironmentTag[Environment]

  override def layer: ZLayer[ZIOAppArgs, Throwable, Environment] =
    ZLayer.make[AppEnv & Config & ZEnv](
      ZEnv.live,
      ConfigLive.layer,
      FlywayAdapterLive.layer,
      DBTransactorLive.layer,
      CustomerRepositoryLive.layer,
      CustomerServiceLive.layer
    )

  override def run: ZIO[Environment & ZEnv & ZIOAppArgs, Any, ExitCode] =
    Server
      .run()
      .tapError(err => ZIO.logError(err.getMessage))
      .exitCode
