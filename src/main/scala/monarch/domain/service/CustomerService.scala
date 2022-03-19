package monarch.domain.service

import monarch.domain.errors.{Error, CustomerNotFound, UnknownError}
import monarch.domain.models.Customer
import monarch.domain.repository.CustomerRepository
import zio.*
import monarch.Environment.CustomerEnv
import eu.timepit.refined.auto.*

trait CustomerService:
  def get(id: Long): RIO[CustomerRepository, Customer]

  def create(customer: Customer): RIO[CustomerRepository, Long]

  def update(id: Long, customer: Customer): RIO[CustomerRepository, Unit]

object CustomerService extends zio.Accessible[CustomerService]

case class CustomerServiceLive(repo: CustomerRepository)
    extends CustomerService:
  override def get(id: Long): RIO[CustomerRepository, Customer] =
    repo
      .getById(id)
      .flatMap(maybeCustomer =>
        ZIO.fromOption(maybeCustomer).mapError(_ => CustomerNotFound(id))
      )

  override def create(customer: Customer): RIO[CustomerRepository, Long] =
    repo.insert(customer).mapError(_ => UnknownError())

  override def update(
      id: Long,
      customer: Customer
  ): RIO[CustomerRepository, Unit] =
    for {
      c <- get(id)
      _ <- repo
        .update(
          id,
          c.copy(firstName = customer.firstName, lastName = customer.lastName)
        )
        .mapError(_ => UnknownError())
    } yield ZIO.unit

object CustomerServiceLive:
  val layer: URLayer[CustomerRepository, CustomerService] =
    (CustomerServiceLive(_)).toLayer[CustomerService]
