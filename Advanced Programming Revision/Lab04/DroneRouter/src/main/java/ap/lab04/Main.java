package ap.lab04;

import net.datafaker.Faker;
import org.jgrapht.GraphPath;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        // ===== Compulsory: locations array + Streams on collections =====
        Location[] L = {
                new Location("Alpha", Type.FRIENDLY),
                new Location("Bravo", Type.NEUTRAL),
                new Location("Charlie", Type.ENEMY),
                new Location("Delta", Type.FRIENDLY),
                new Location("Echo", Type.ENEMY)
        };

        // TreeSet of FRIENDLY by natural order (name)
        var friendly = Arrays.stream(L)
                .filter(x -> x.type()==Type.FRIENDLY)
                .collect(Collectors.toCollection(TreeSet::new));
        System.out.println("Friendly (TreeSet, sorted by name): " + friendly);

        // LinkedList of ENEMY sorted by type then name
        var enemyList = Arrays.stream(L)
                .filter(x -> x.type()==Type.ENEMY)
                .sorted(Comparator.comparing(Location::type).thenComparing(Location::name))
                .collect(Collectors.toCollection(LinkedList::new));
        System.out.println("Enemy (LinkedList, sorted by type then name): " + enemyList);

        // ===== Homework: random names + JGraphT fastest routes =====
        Faker faker = new Faker();
        Random rng = new Random(7);

        // Build N random locations
        int N = 7;
        Location[] R = new Location[N];
        for (int i=0;i<N;i++) {
            Type t = Type.values()[rng.nextInt(Type.values().length)];
            String name = faker.address().cityName(); // fake distinct-ish names
            R[i] = new Location(name, t);
        }

        // Random directed edges with time and prob
        List<EdgeInfo> E = new ArrayList<>();
        for (int i=0;i<N;i++) for (int j=0;j<N;j++) if (i!=j && rng.nextDouble()<0.55) {
            double time = 5 + rng.nextInt(26);     // 5..30 min
            double prob = 0.6 + rng.nextDouble()*0.4; // 0.6..1.0
            E.add(new EdgeInfo(R[i], R[j], time, prob));
        }

        Model model = new Model(R, E);
        Pathfinder pf = new Pathfinder(model);

        Location start = R[0];

        // Map<Type, List<Location>>
        Map<Type, List<Location>> byType = model.byType();

        // Fastest paths from start
        Map<Location, GraphPath<Location, ?>> fast = (Map) pf.fastestFrom(start);

        // Print fastest times grouped: FRIENDLY, NEUTRAL, ENEMY
        System.out.println("\nFastest times from start = " + start.name());
        Stream.of(Type.FRIENDLY, Type.NEUTRAL, Type.ENEMY).forEach(t -> {
            var list = byType.getOrDefault(t, List.of());
            String line = list.stream()
                    .map(v -> v.name()+":"+fmt(Pathfinder.timeOf((GraphPath) fast.get(v))))
                    .collect(Collectors.joining(", "));
            System.out.println(t + " -> " + (line.isEmpty()? "(none)" : line));
        });

        // ===== Bonus: safest routes (product of probs), stats per path =====
        Map<Location, GraphPath<Location, ?>> safe = (Map) pf.safestFrom(start);

        System.out.println("\nSafest route summaries:");
        for (Location v : R) {
            var p = safe.get(v);
            double pp = Pathfinder.probOf((GraphPath) p);
            Map<Type, Long> counts = (p==null) ? Map.of()
                    : ((GraphPath<Location, ?>) p).getVertexList().stream()
                    .collect(Collectors.groupingBy(Location::type, ()->new EnumMap<>(Type.class), Collectors.counting()));
            System.out.println(start.name()+" → "+v.name()+" | p="+fmt(pp)+" | types="+counts);
        }
    }

    private static String fmt(double x){
        if (Double.isInfinite(x)) return "∞";
        if (x==0.0) return "0";
        return String.format(Locale.US, "%.3f", x);
    }
}
