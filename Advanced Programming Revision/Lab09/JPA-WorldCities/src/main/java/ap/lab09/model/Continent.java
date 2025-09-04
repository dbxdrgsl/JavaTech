package ap.lab09.model;

import jakarta.persistence.*;

@Entity @Table(name="continents")
@NamedQuery(name="Continent.findByNameLike",
        query="SELECT c FROM Continent c WHERE LOWER(c.name) LIKE LOWER(:name)")
public class Continent {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=100)
    private String name;

    public Continent() {}
    public Continent(String name){ this.name = name; }

    public Long getId(){ return id; }
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }

    @Override public String toString(){ return "Continent{id=%d,name=%s}".formatted(id,name); }
}
