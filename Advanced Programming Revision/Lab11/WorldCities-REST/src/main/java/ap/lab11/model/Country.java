package ap.lab11.model;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="countries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Country {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=150) private String name;
    @Column(length=10) private String code;
    @ManyToOne(optional=false) @JoinColumn(name="continent_id") private Continent continent;
}
