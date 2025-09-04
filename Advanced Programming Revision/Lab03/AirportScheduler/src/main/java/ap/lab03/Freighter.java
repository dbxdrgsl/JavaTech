package ap.lab03;

public final class Freighter extends Aircraft implements CargoCapable {
    private final double maxPayloadKg;
    public Freighter(String name, String model, String tail, double maxPayloadKg) {
        super(name, model, tail);
        this.maxPayloadKg = maxPayloadKg;
    }
    @Override public double maxPayloadKg() { return maxPayloadKg; }
}
