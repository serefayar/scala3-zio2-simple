package monarch.domain.repository

import doobie.util.transactor.Transactor
import monarch.domain.models.Customer
import monarch.system.db.{DBTransactor, DBTransactorLive}
import zio.*
import cats.implicits.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.postgres.implicits.*
import doobie.refined.implicits.*
import doobie.*
import zio.interop.catz.*
import zio.interop.catz.implicits.*

trait CustomerRepository extends Repository[Task, Customer, Long]:
  def getById(id: Long): Task[Option[Customer]]

  def insert(customer: Customer): Task[Long]

  def update(id: Long, customer: Customer): Task[Unit]
  

object CustomerRepository extends zio.Accessible[CustomerRepository]

case class CustomerRepositoryLive(trx: Transactor[Task])
    extends CustomerRepository:

  override def getById(id: Long): Task[Option[Customer]] =
    sql"""SELECT * FROM CUSTOMERS WHERE ID = $id """
      .query[Customer]
      .option
      .transact(trx)

  override def insert(customer: Customer): Task[Long] =
    sql"""INSERT INTO CUSTOMERS (first_name, last_name) VALUES (${customer.firstName}, ${customer.lastName}) """.update
      .withUniqueGeneratedKeys[Long]("id")
      .transact(trx)

  override def update(id: Long, customer: Customer): Task[Unit] =
    sql"""UPDATE CUSTOMERS SET first_name = ${customer.firstName}, last_name = ${customer.lastName} WHERE id = ${id} """.update.run.void
      .transact(trx)

object CustomerRepositoryLive:
  val layer: URLayer[DBTransactor, CustomerRepository] = ZLayer.fromZIO(
    DBTransactor(_.trx).map(CustomerRepositoryLive(_))
  )
