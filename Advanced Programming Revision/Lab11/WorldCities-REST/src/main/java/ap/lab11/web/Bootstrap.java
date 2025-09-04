package ap.lab11.web;
import ap.lab11.model.*; import ap.lab11.repo.*;
import org.springframework.boot.ApplicationRunner; import org.springframework.context.annotation.Bean; import org.springframework.context.annotation.Configuration;

@Configuration
public class Bootstrap {
    @Bean ApplicationRunner seed(ContinentRepo cont, CountryRepo ctry, CityRepo city){
        return args -> {
            if (cont.count()==0) {
                var eu = cont.save(new Continent(null, "Europe"));
                var ro = ctry.save(new Country(null, "Romania", "RO", eu));
                city.save(new City(null, ro, "Iași", false, 47.1585, 27.6014));
                city.save(new City(null, ro, "Bucharest", true, 44.4268, 26.1025));
            }
        };
    }
}
