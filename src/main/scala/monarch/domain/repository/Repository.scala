package monarch.domain.repository

trait Repository[F[_], ENTITY, IDTYPE]:
  
  def getById(id: IDTYPE): F[Option[ENTITY]]

  def insert(entity: ENTITY): F[IDTYPE]

  def update(id: IDTYPE, entity: ENTITY): F[Unit]
