package monarch.http.routes

import org.http4s.HttpRoutes
import sttp.tapir.PublicEndpoint
import sttp.tapir.ztapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*
import io.circe.generic.auto.*
import sttp.tapir.server.http4s.*
import sttp.tapir.server.http4s.ztapir.*
import zio.*
import zio.interop.catz.*
import monarch.domain.service.CustomerService
import monarch.Environment.CustomerEnv
import sttp.model.StatusCode
import monarch.http.ErrorInfo
import monarch.http.ErrorInfo.*
import monarch.http.data.*
import monarch.domain.models.Customer
import sttp.tapir.EndpointOutput.OneOf
import eu.timepit.refined.auto.*

object CustomerRoutes:

  val httpErrors: OneOf[ErrorInfo, ErrorInfo] = oneOf[ErrorInfo](
    oneOfMapping(StatusCode.InternalServerError, jsonBody[InternalServerError]),
    oneOfMapping(StatusCode.BadRequest, jsonBody[BadRequest]),
    oneOfMapping(StatusCode.NotFound, jsonBody[NotFound])
  )

  val getCustomer: PublicEndpoint[Long, ErrorInfo, CustomerData, Any] =
    endpoint.get
      .in("customers" / path[Long]("id"))
      .out(jsonBody[CustomerData])
      .errorOut(httpErrors)

  val createCustomer: PublicEndpoint[CreateCustomerData, ErrorInfo, Long, Any] =
    endpoint.post
      .in("customers")
      .in(jsonBody[CreateCustomerData])
      .out(plainBody[Long])
      .errorOut(httpErrors)

  val updateCustomer
      : PublicEndpoint[(Long, UpdateCustomerData), ErrorInfo, Unit, Any] =
    endpoint.put
      .in("customers" / path[Long]("id"))
      .in(jsonBody[UpdateCustomerData])
      .errorOut(httpErrors)
      .out(statusCode(StatusCode.Accepted))

  // endpoints

  def getCustomerEndpoint: ZServerEndpoint[CustomerEnv, Any] =
    getCustomer.zServerLogic { (id: Long) =>
      CustomerService(_.get(id)).mapBoth(e => NotFound(e.getMessage), _.toData)
    }

  def createCustomerEndpoint: ZServerEndpoint[CustomerEnv, Any] =
    createCustomer.zServerLogic { (data: CreateCustomerData) =>
      ZIO
        .fromEither(data.toDomain)
        .mapError(e =>
          BadRequest("Validation Failed", e.toList.map(_.getMessage))
        )
        .flatMap { c =>
          CustomerService(_.create(c)).mapError(e => BadRequest(e.getMessage))
        }
    }

  def updateCustomerEndpoint: ZServerEndpoint[CustomerEnv, Any] =
    updateCustomer.zServerLogic { (id: Long, data: UpdateCustomerData) =>
      ZIO
        .fromEither(data.toDomain)
        .mapError(e =>
          BadRequest("Validation Failed", e.toList.map(_.getMessage))
        )
        .flatMap { c =>
          CustomerService(_.update(id, c)).mapError(e =>
            BadRequest(e.getMessage)
          )
        }

    }

  // routes
  val endpoints: List[ZServerEndpoint[CustomerEnv, Any]] = List(
    getCustomerEndpoint,
    createCustomerEndpoint,
    updateCustomerEndpoint
  )

  val routes: HttpRoutes[RIO[CustomerEnv, *]] =
    ZHttp4sServerInterpreter()
      .from(
        endpoints
      )
      .toRoutes
