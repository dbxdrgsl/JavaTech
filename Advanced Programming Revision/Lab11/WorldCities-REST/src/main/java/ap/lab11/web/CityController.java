package ap.lab11.web;
import ap.lab11.model.City;
import ap.lab11.model.Country;
import ap.lab11.repo.CityRepo;
import ap.lab11.repo.CountryRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController @RequestMapping("/api/cities")
public class CityController {
    private final CityRepo repo; private final CountryRepo countries;
    public CityController(CityRepo r, CountryRepo c){ this.repo=r; this.countries=c; }

    @GetMapping public List<City> all(){ return repo.findAll(); }                               // GET list
    @GetMapping("/search") public List<City> search(@RequestParam String q){ return repo.findByNameContainingIgnoreCase(q); }

    @PostMapping public ResponseEntity<City> create(@RequestBody CityDTO dto){                  // POST
        Country c = countries.findById(dto.countryId()).orElseThrow();
        City saved = repo.save(new City(null, c, dto.name(), dto.capital(), dto.latitude(), dto.longitude()));
        return ResponseEntity.created(URI.create("/api/cities/"+saved.getId())).body(saved);
    }

    @PutMapping("/{id}") public City rename(@PathVariable Long id, @RequestBody RenameDTO dto){ // PUT (rename only)
        City x = repo.findById(id).orElseThrow();
        x.setName(dto.name()); return repo.save(x);
    }

    @DeleteMapping("/{id}") public void delete(@PathVariable Long id){ repo.deleteById(id); }    // DELETE

    public record CityDTO(Long countryId, String name, boolean capital, double latitude, double longitude) { }
    public record RenameDTO(String name) { }
}
