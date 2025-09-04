package ap.lab11.model;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="cities")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class City {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false) @JoinColumn(name="country_id") private Country country;
    @Column(nullable=false, length=200) private String name;
    @Column(nullable=false) private boolean capital;
    @Column(nullable=false) private double latitude;
    @Column(nullable=false) private double longitude;
}
