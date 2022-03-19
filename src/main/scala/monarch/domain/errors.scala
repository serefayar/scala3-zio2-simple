package monarch.domain

import scala.util.control.NoStackTrace
import io.circe.generic.auto.*

object errors:
  
  sealed trait Error extends Throwable with NoStackTrace

  case class CustomerNotFound(customerId: Long) extends Throwable(s"Customer with id ${customerId} was not found!") with Error
  case class UnknownError() extends Throwable(s"Unkown Error!") with Error

  sealed trait ValidationError  extends Error
  
  case class EmptyField(field: String, msg: String) extends Throwable(s"Field ${field}: ${msg}") with ValidationError