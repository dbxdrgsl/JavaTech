package ap.lab03;

import java.util.ArrayList;
import java.util.List;

public final class Airport {
    private final String name;
    private final List<Runway> runways = new ArrayList<>();
    public Airport(String name) { this.name = name; }
    public String name() { return name; }
    public List<Runway> runways() { return runways; }
    public void addRunway(String id) { runways.add(new Runway(id)); }
    public int capacity() { return runways.size(); }
}
