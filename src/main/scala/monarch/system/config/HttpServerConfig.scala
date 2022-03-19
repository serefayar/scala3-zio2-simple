package monarch.system.config

import eu.timepit.refined.types.net.PortNumber
import eu.timepit.refined.types.string.NonEmptyString


case class HttpServerConfig(
    host: NonEmptyString,
    port: PortNumber
)
