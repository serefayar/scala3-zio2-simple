package monarch.system.config

import pureconfig.ConfigReader
import pureconfig.generic.derivation.default.*

case class Configuration(
    httpServer: HttpServerConfig,
    dbConfig: PostgresConfig
) derives ConfigReader
