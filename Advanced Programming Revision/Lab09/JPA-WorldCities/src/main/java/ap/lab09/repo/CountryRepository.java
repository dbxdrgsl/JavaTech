package ap.lab09.repo;

import ap.lab09.model.Country;

public final class CountryRepository extends AbstractRepository<Country, Long> {
    public CountryRepository(){ super(Country.class); }
}
