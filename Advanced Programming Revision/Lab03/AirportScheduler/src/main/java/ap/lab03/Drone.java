package ap.lab03;

public final class Drone extends Aircraft implements CargoCapable {
    private final double maxPayloadKg;
    private final double batteryHours;
    public Drone(String name, String model, String tail, double maxPayloadKg, double batteryHours) {
        super(name, model, tail);
        this.maxPayloadKg = maxPayloadKg;
        this.batteryHours = batteryHours;
    }
    public double batteryHours() { return batteryHours; }
    @Override public double maxPayloadKg() { return maxPayloadKg; }
}
