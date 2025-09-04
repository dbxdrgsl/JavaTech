package ap.lab03;

public final class Airliner extends Aircraft implements PassengerCapable {
    private final int seats;
    public Airliner(String name, String model, String tail, int seats) {
        super(name, model, tail);
        this.seats = seats;
    }
    @Override public int seats() { return seats; }
}
