package monarch.http

import cats.syntax.all.*
import monarch.domain.models.Customer
import monarch.system.config.Config
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits.*
import zio.*
import zio.interop.catz.*
import zio.interop.catz.implicits.*
import org.http4s.server.Router
import sttp.tapir.PublicEndpoint
import sttp.tapir.ztapir.*
import org.http4s.HttpRoutes
import monarch.http.routes.CustomerRoutes
import monarch.domain.service.CustomerService
import monarch.system.db.DBTransactor
import monarch.Environment.AppEnv
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter

object Server:

  def run(): ZIO[AppEnv & Config, Throwable, Unit] = for {

    config <- Config.httpServerConfig
    swaggerRoutes = ZHttp4sServerInterpreter()
      .from(
        SwaggerInterpreter().fromServerEndpoints[RIO[AppEnv, *]](
          CustomerRoutes.endpoints,
          "Monarch",
          "1.0"
        )
      )
      .toRoutes
    routes = Router(
      "/" -> (CustomerRoutes.routes <+> swaggerRoutes)
    ).orNotFound
    _ <- BlazeServerBuilder[RIO[AppEnv, *]]
      .bindHttp(9090, "0.0.0.0")
      .withoutBanner
      .withHttpApp(routes)
      .serve
      .compile
      .drain
  } yield ()
