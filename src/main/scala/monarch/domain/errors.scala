package monarch.domain

import scala.util.control.NoStackTrace
import io.circe.generic.auto.*

object errors:
  
  sealed trait Error extends Throwable with NoStackTrace


  enum DomainError(msg: String) extends Throwable(msg) with Error:
    case CustomerNotFound(customerId: Long) extends DomainError(s"Customer with id ${customerId} was not found!")
    case UnknownError() extends DomainError(s"Unkown Error!")
    

  enum ValidationError(msg: String) extends Throwable(msg) with Error:
    case EmptyField(field: String, msg: String) extends ValidationError(s"Field ${field}: ${msg}")  
