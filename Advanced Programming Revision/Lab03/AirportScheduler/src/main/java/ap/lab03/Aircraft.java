package ap.lab03;

import java.util.Objects;

/** Base aircraft; natural order by name (call sign). */
public abstract class Aircraft implements Comparable<Aircraft> {
    private final String name;      // call sign
    private final String model;
    private final String tailNumber;

    protected Aircraft(String name, String model, String tailNumber) {
        this.name = Objects.requireNonNull(name);
        this.model = Objects.requireNonNull(model);
        this.tailNumber = Objects.requireNonNull(tailNumber);
    }
    public String name() { return name; }
    public String model() { return model; }
    public String tailNumber() { return tailNumber; }

    @Override public int compareTo(Aircraft o) { return this.name.compareTo(o.name); }

    @Override public String toString() {
        return getClass().getSimpleName()+"{name="+name+", model="+model+", tail="+tailNumber+"}";
    }
}
