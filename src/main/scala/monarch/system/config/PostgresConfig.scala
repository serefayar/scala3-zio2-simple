package monarch.system.config

import eu.timepit.refined.types.string.NonEmptyString


case class PostgresConfig(
    className: NonEmptyString,
    url: NonEmptyString,
    user: NonEmptyString,
    password: NonEmptyString
)
