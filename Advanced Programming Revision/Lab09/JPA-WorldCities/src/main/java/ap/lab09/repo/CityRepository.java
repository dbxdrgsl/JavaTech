package ap.lab09.repo;

import ap.lab09.model.City;

public final class CityRepository extends AbstractRepository<City, Long> {
    public CityRepository(){ super(City.class); }
}
