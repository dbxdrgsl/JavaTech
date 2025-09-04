package ap.lab11.client;

import ap.lab11.model.City;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Configuration
public class ApiClient {

    @Bean
    ApplicationRunner callApi() {
        return args -> {
            RestClient http = RestClient.builder()
                    .baseUrl("http://localhost:8080")
                    .build();

            // GET countries
            List<Map<String, Object>> countries = http.get()
                    .uri("/api/countries")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            System.out.println("Countries: " + countries);

            // GET cities
            List<Map<String, Object>> cities = http.get()
                    .uri("/api/cities")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            System.out.println("Cities: " + cities);

            // Use first country id
            Long firstCountryId = ((Number) countries.get(0).get("id")).longValue();

            // POST city
            City created = http.post()
                    .uri("/api/cities")
                    .body(Map.of(
                            "countryId", firstCountryId,
                            "name", "Cluj-Napoca",
                            "capital", false,
                            "latitude", 46.7712,
                            "longitude", 23.6236
                    ))
                    .retrieve()
                    .body(City.class);
            System.out.println("Created: " + created.getId() + " " + created.getName());

            // PUT rename
            City renamed = http.put()
                    .uri("/api/cities/{id}", created.getId())
                    .body(Map.of("name", "Cluj"))
                    .retrieve()
                    .body(City.class);
            System.out.println("Renamed: " + renamed.getName());

            // DELETE
            http.delete()
                    .uri("/api/cities/{id}", created.getId())
                    .retrieve()
                    .toBodilessEntity();
            System.out.println("Deleted: " + created.getId());
        };
    }
}
