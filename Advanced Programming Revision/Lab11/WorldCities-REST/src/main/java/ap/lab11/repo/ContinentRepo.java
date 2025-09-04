package ap.lab11.repo;
import ap.lab11.model.Continent;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ContinentRepo extends JpaRepository<Continent,Long> { }
