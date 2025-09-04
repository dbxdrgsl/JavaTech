package ap.lab09.repo;

import ap.lab09.model.Continent;

public final class ContinentRepository extends AbstractRepository<Continent, Long> {
    public ContinentRepository(){ super(Continent.class); }
}
