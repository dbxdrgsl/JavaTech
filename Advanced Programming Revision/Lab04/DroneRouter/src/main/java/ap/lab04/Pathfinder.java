package ap.lab04;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;

public final class Pathfinder {
    private final Model model;
    public Pathfinder(Model model){ this.model = model; }

    public Map<Location, GraphPath<Location, DefaultWeightedEdge>> fastestFrom(Location s){
        var dj = new DijkstraShortestPath<>(model.gTime);
        Map<Location, GraphPath<Location, DefaultWeightedEdge>> res = new LinkedHashMap<>();
        for (Location v : model.gTime.vertexSet()) res.put(v, dj.getPath(s, v));
        return res;
    }

    public Map<Location, GraphPath<Location, DefaultWeightedEdge>> safestFrom(Location s){
        var dj = new DijkstraShortestPath<>(model.gRisk);
        Map<Location, GraphPath<Location, DefaultWeightedEdge>> res = new LinkedHashMap<>();
        for (Location v : model.gRisk.vertexSet()) res.put(v, dj.getPath(s, v));
        return res;
    }

    public static double timeOf(GraphPath<Location, DefaultWeightedEdge> p){
        return p==null? Double.POSITIVE_INFINITY : p.getWeight();
    }
    public static double probOf(GraphPath<Location, DefaultWeightedEdge> p){
        if (p==null) return 0.0;
        // weight = sum -ln(p_i) = -ln(prod p_i)
        return Math.exp(-p.getWeight());
    }

    public static String pathToString(GraphPath<Location, DefaultWeightedEdge> p){
        if (p==null) return "(unreachable)";
        return String.join(" -> ", p.getVertexList().stream().map(Location::name).toList());
    }
}
