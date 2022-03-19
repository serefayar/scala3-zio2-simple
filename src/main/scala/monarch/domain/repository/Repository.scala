package monarch.domain.repository

trait Repository[F[_], ENTITY, IDTYPE]:
  def getById(id: IDTYPE): F[Option[ENTITY]]
