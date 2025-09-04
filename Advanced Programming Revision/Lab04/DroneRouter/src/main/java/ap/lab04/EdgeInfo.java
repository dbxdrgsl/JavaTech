package ap.lab04;

/** Directed arc: a -> b, with time (minutes) and success probability p in (0,1]. */
public record EdgeInfo(Location from, Location to, double timeMin, double prob) { }
