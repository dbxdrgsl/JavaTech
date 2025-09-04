package ap.lab11.model;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="continents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Continent {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=100) private String name;
}
