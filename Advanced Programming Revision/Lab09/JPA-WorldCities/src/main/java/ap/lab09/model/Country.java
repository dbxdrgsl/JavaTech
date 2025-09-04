package ap.lab09.model;

import jakarta.persistence.*;

@Entity @Table(name="countries")
@NamedQuery(name="Country.findByNameLike",
        query="SELECT c FROM Country c WHERE LOWER(c.name) LIKE LOWER(:name)")
public class Country {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=150)
    private String name;

    @Column(length=10)
    private String code;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="continent_id")
    private Continent continent;

    public Country() {}
    public Country(String name, String code, Continent continent){
        this.name=name; this.code=code; this.continent=continent;
    }

    public Long getId(){ return id; }
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }
    public String getCode(){ return code; }
    public void setCode(String code){ this.code = code; }
    public Continent getContinent(){ return continent; }
    public void setContinent(Continent continent){ this.continent = continent; }

    @Override public String toString(){
        return "Country{id=%d,name=%s,code=%s,continent=%s}".formatted(id,name,code,
                continent!=null?continent.getName():null);
    }
}
