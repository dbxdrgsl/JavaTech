package ap.lab11.repo;
import ap.lab11.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CountryRepo extends JpaRepository<Country,Long> {
    List<Country> findByNameContainingIgnoreCase(String q);
}
