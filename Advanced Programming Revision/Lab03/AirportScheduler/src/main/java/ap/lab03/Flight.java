package ap.lab03;

import java.time.LocalTime;
import java.util.Objects;

public final class Flight {
    private final String id;
    private final Aircraft aircraft;
    private final LocalTime start;
    private final LocalTime end;

    public Flight(String id, Aircraft aircraft, LocalTime start, LocalTime end) {
        if (!end.isAfter(start)) throw new IllegalArgumentException("end must be after start");
        this.id = Objects.requireNonNull(id);
        this.aircraft = Objects.requireNonNull(aircraft);
        this.start = start; this.end = end;
    }
    public String id() { return id; }
    public Aircraft aircraft() { return aircraft; }
    public LocalTime start() { return start; }
    public LocalTime end() { return end; }

    /** Conflict if intervals intersect. */
    public boolean conflicts(Flight other) {
        return !(this.end.compareTo(other.start) <= 0 || other.end.compareTo(this.start) <= 0);
    }
    @Override public String toString() {
        return "Flight(" + id + ", " + aircraft.name() + ", ["+start+"–"+end+"])";
    }
}
