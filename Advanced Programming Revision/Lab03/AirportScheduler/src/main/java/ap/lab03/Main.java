package ap.lab03;

import java.time.LocalTime;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // ===== Compulsory: aircraft, interfaces, sorting, cargo array =====
        List<Aircraft> fleet = new ArrayList<>();
        fleet.add(new Airliner("ALPHA", "A320", "YR-AAA", 180));
        fleet.add(new Freighter("CARGO1", "B767F", "YR-CGF", 52000));
        fleet.add(new Drone("DRN-7", "DJI-M600", "DR-007", 10, 1.5));
        fleet.add(new Airliner("BRAVO", "B737", "YR-BBB", 160));
        Collections.sort(fleet); // by name
        System.out.println("Fleet (sorted by name):");
        fleet.forEach(System.out::println);

        // Array of cargo-capable aircraft
        CargoCapable[] cargoArray = fleet.stream()
                .filter(a -> a instanceof CargoCapable)
                .map(a -> (CargoCapable)a)
                .toArray(CargoCapable[]::new);
        System.out.println("\nCargo-capable array length=" + cargoArray.length);

        // ===== Homework: airport, flights, scheduling =====
        Airport apt = new Airport("IAS");
        apt.addRunway("R1"); apt.addRunway("R2"); // two runways

        SchedulingProblem pb = new SchedulingProblem(apt);
        // Times
        LocalTime t = LocalTime.of(10, 0);
        pb.addFlight(new Flight("F1", fleet.get(0), t.plusMinutes( 0),  t.plusMinutes(20)));
        pb.addFlight(new Flight("F2", fleet.get(1), t.plusMinutes( 5),  t.plusMinutes(25)));
        pb.addFlight(new Flight("F3", fleet.get(2), t.plusMinutes(15),  t.plusMinutes(35)));
        pb.addFlight(new Flight("F4", fleet.get(3), t.plusMinutes(30),  t.plusMinutes(50)));
        pb.addFlight(new Flight("F5", fleet.get(0), t.plusMinutes(18),  t.plusMinutes(40)));
//        pb.addFlight(new Flight("F5", fleet.get(0), t.plusMinutes(40), t.plusMinutes(60)));

        int need = Scheduler.maxConcurrency(pb.flights());
        while (apt.capacity() < need) apt.addRunway("R" + (apt.capacity() + 1));
        System.out.println("\nMax concurrency = " + need + ", runways available = " + apt.capacity());

        // Greedy schedule
        Map<Flight, Runway> sched = Scheduler.scheduleGreedy(pb);
        System.out.println("\nGreedy schedule:");
        sched.forEach((f,r)-> System.out.println(f.id()+" -> "+r));

        // Equitable schedule
        Map<Flight, Runway> eq = Scheduler.scheduleEquitable(pb);
        System.out.println("\nEquitable schedule:");
        eq.forEach((f,r)-> System.out.println(f.id()+" -> "+r));

        // Per-runway load
        Map<Runway, Long> counts = new LinkedHashMap<>();
        for (Runway r : apt.runways()) counts.put(r, 0L);
        eq.values().forEach(r -> counts.put(r, counts.get(r)+1));
        System.out.println("\nRunway loads (equitable): " + counts);
        long min = counts.values().stream().min(Long::compare).orElse(0L);
        long max = counts.values().stream().max(Long::compare).orElse(0L);
        System.out.println("Load balance diff = " + (max - min));
    }
}
