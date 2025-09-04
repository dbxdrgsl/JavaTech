package ap.lab11.web;
import ap.lab11.model.Country;
import ap.lab11.repo.CountryRepo;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/countries")
public class CountryController {
    private final CountryRepo repo;
    public CountryController(CountryRepo repo){ this.repo = repo; }

    @GetMapping public List<Country> all(){ return repo.findAll(); }                 // compulsory
    @GetMapping("/search") public List<Country> search(@RequestParam String q){ return repo.findByNameContainingIgnoreCase(q); }
}
