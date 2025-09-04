package ap.lab09.model;

import jakarta.persistence.*;

@Entity @Table(name="cities")
@NamedQuery(name="City.findByNameLike",
        query="SELECT c FROM City c WHERE LOWER(c.name) LIKE LOWER(:name)")
public class City {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="country_id")
    private Country country;

    @Column(nullable=false, length=200)
    private String name;

    @Column(nullable=false) private boolean capital;
    @Column(nullable=false) private double latitude;
    @Column(nullable=false) private double longitude;

    public City() {}
    public City(Country country, String name, boolean capital, double lat, double lon){
        this.country=country; this.name=name; this.capital=capital; this.latitude=lat; this.longitude=lon;
    }

    public Long getId(){ return id; }
    public Country getCountry(){ return country; }
    public String getName(){ return name; }
    public boolean isCapital(){ return capital; }
    public double getLatitude(){ return latitude; }
    public double getLongitude(){ return longitude; }

    @Override public String toString(){
        return "City{id=%d,name=%s,country=%s,capital=%s}".formatted(id,name,
                country!=null?country.getName():null, capital);
    }
}
