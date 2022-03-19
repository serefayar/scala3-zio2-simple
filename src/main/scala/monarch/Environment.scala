package monarch


import zio.ZEnv
import monarch.system.config.Config
import zio.Clock
import monarch.domain.service.CustomerService
import monarch.domain.repository.CustomerRepository
import zio.ZLayer
import monarch.system.db.DBTransactor
import monarch.system.config.ConfigLive
import monarch.system.db.DBTransactorLive
import monarch.domain.repository.CustomerRepositoryLive
import monarch.domain.service.CustomerServiceLive
import monarch.system.db.FlywayAdapter
import monarch.system.db.FlywayAdapterLive


object Environment:
  
  type CustomerEnv = CustomerService & CustomerRepository & Clock

  type AppEnv = CustomerEnv

