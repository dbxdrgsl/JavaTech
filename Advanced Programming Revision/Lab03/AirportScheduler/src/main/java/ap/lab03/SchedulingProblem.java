package ap.lab03;

import java.util.ArrayList;
import java.util.List;

public final class SchedulingProblem {
    private final Airport airport;
    private final List<Flight> flights = new ArrayList<>();
    public SchedulingProblem(Airport airport) { this.airport = airport; }
    public Airport airport() { return airport; }
    public List<Flight> flights() { return flights; }
    public void addFlight(Flight f) { flights.add(f); }
}
