package monarch.system.db

import cats.effect.*
import cats.implicits.*
import doobie.*
import doobie.hikari.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import monarch.system.config.{Config, PostgresConfig}
import monarch.system.db.DBTransactor
import zio.interop.catz.*
import zio.interop.catz.implicits.*
import zio.{Task, *}
import eu.timepit.refined.auto.*

import scala.concurrent.ExecutionContext

trait DBTransactor:
  val trx: UIO[Transactor[Task]]

object DBTransactor extends zio.Accessible[DBTransactor]

case class DBTransactorLive(trx: UIO[Transactor[Task]]) extends DBTransactor

object DBTransactorLive:
  private def makeTransactor(
      config: PostgresConfig,
      ec: ExecutionContext
  ): TaskManaged[Transactor[Task]] =
    HikariTransactor
      .newHikariTransactor[Task](
        config.className,
        config.url,
        config.user,
        config.password,
        ec
      )
      .toManagedZIO

  val managed: ZManaged[Config, Throwable, Transactor[Task]] = (for {
    dbConfig <- Config(_.dbConfig).toManaged
    ce <- ZIO.descriptor
      .map(_.executor.asExecutionContext)
      .toManaged
    xa <- makeTransactor(dbConfig, ce)
  } yield xa)

  val managedWithMigration
      : ZManaged[FlywayAdapter & Config, Throwable, Transactor[Task]] =
    FlywayAdapter(_.migrate).toManaged *> managed

  val layer: RLayer[FlywayAdapter & Config, DBTransactor] = ZLayer.fromManaged(
    managedWithMigration.map(t => DBTransactorLive(UIO(t)))
  )
