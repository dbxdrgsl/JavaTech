package ap.lab11.repo;
import ap.lab11.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CityRepo extends JpaRepository<City,Long> {
    List<City> findByNameContainingIgnoreCase(String q);
}
