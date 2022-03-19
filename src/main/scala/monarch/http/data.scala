package monarch.http

import cats.data.EitherNel

import monarch.domain.models.Customer
import eu.timepit.refined.auto.*
import monarch.domain.errors.ValidationError

object data:

  trait CustomerInfo:
    val firstName: String
    val lastName: String

    def toDomain: EitherNel[ValidationError, Customer] =
      Customer.create(firstName, lastName)


  final case class CustomerData(id: Long, firstName: String, lastName: String) extends CustomerInfo
    
  final case class CreateCustomerData(firstName: String, lastName: String) extends CustomerInfo
   
  final case class UpdateCustomerData(firstName: String, lastName: String) extends CustomerInfo

  extension (c: Customer)
    def toData: CustomerData = CustomerData(c.id, c.firstName, c.lastName)
