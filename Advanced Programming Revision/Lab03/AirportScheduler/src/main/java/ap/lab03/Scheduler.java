package ap.lab03;

import java.time.LocalTime;
import java.util.*;

/** Greedy scheduler + equitable variant. */
public final class Scheduler {

    /** Assign flights to runways. If at some time overlaps exceed capacity, throws IllegalStateException. */
    public static Map<Flight, Runway> scheduleGreedy(SchedulingProblem pb) {
        List<Runway> R = pb.airport().runways();
        if (R.isEmpty()) throw new IllegalStateException("no runways");

        // Sort flights by start time, then end time
        List<Flight> F = new ArrayList<>(pb.flights());
        F.sort(Comparator.comparing(Flight::start).thenComparing(Flight::end));

        // For each runway, track last end time of last scheduled flight
        Map<Runway, LocalTime> avail = new LinkedHashMap<>();
        for (Runway r : R) avail.put(r, LocalTime.MIN);

        Map<Flight, Runway> assign = new LinkedHashMap<>();
        for (Flight f : F) {
            Runway chosen = null;
            for (Runway r : R) {
                if (!f.start().isBefore(avail.get(r))) { chosen = r; break; }
            }
            if (chosen == null) throw new IllegalStateException("capacity exceeded at " + f.start());
            assign.put(f, chosen);
            avail.put(chosen, f.end());
        }
        return assign;
    }

    /** Equitable: when several runways are feasible, choose the one with the smallest load so far. */
    public static Map<Flight, Runway> scheduleEquitable(SchedulingProblem pb) {
        List<Runway> R = pb.airport().runways();
        List<Flight> F = new ArrayList<>(pb.flights());
        F.sort(Comparator.comparing(Flight::start).thenComparing(Flight::end));

        Map<Runway, LocalTime> avail = new HashMap<>();
        Map<Runway, Integer> load = new HashMap<>();
        for (Runway r : R) { avail.put(r, LocalTime.MIN); load.put(r, 0); }

        Map<Flight, Runway> assign = new LinkedHashMap<>();
        for (Flight f : F) {
            Runway best = null; int bestLoad = Integer.MAX_VALUE;
            for (Runway r : R) {
                if (!f.start().isBefore(avail.get(r))) {
                    int ld = load.get(r);
                    if (ld < bestLoad) { bestLoad = ld; best = r; }
                }
            }
            if (best == null) throw new IllegalStateException("capacity exceeded at " + f.start());
            assign.put(f, best);
            avail.put(best, f.end());
            load.put(best, load.get(best)+1);
        }
        return assign;
    }

    /** Maximum concurrent flights = lower bound on needed runways. */
    public static int maxConcurrency(Collection<Flight> flights) {
        record E(LocalTime t, int delta){}
        List<E> ev = new ArrayList<>();
        for (Flight f : flights) { ev.add(new E(f.start(), +1)); ev.add(new E(f.end(), -1)); }
        ev.sort(Comparator.<E, LocalTime>comparing(e->e.t).thenComparingInt(e->-e.delta));
        int cur=0, best=0;
        for (E e: ev) { cur += e.delta; if (cur>best) best=cur; }
        return best;
    }
}
