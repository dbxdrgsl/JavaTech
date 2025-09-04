package ap.lab03;

public record Runway(String id) {
    @Override public String toString() { return "Runway(" + id + ")"; }
}
