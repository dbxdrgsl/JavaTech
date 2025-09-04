package ap.lab04;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;
import java.util.stream.Collectors;

public final class Model {
    public final Location[] locs;             // array for compulsory tasks
    public final Graph<Location, DefaultWeightedEdge> gTime;  // weights = time
    public final Graph<Location, DefaultWeightedEdge> gRisk;  // weights = -ln(prob)

    public Model(Location[] locs, Collection<EdgeInfo> edges){
        this.locs = locs.clone();
        this.gTime = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        this.gRisk = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        for (Location v : locs){ gTime.addVertex(v); gRisk.addVertex(v); }
        for (EdgeInfo e : edges){
            var et = gTime.addEdge(e.from(), e.to());
            gTime.setEdgeWeight(et, e.timeMin());
            var er = gRisk.addEdge(e.from(), e.to());
            double w = -Math.log(Math.max(1e-12, Math.min(1.0, e.prob())));
            gRisk.setEdgeWeight(er, w);
        }
    }

    /** Map<Type, List<Location>> with stable iteration order by Type. */
    public Map<Type, List<Location>> byType() {
        return Arrays.stream(locs).collect(Collectors.groupingBy(
                Location::type,
                () -> new EnumMap<>(Type.class),
                Collectors.toList()
        ));
    }
}
