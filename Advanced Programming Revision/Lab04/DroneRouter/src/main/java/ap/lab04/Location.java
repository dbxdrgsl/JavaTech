package ap.lab04;

import java.util.Objects;

public final class Location implements Comparable<Location> {
    private final String name;
    private final Type type;

    public Location(String name, Type type) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
    }
    public String name(){ return name; }
    public Type type(){ return type; }

    @Override public int compareTo(Location o){ return this.name.compareTo(o.name); }
    @Override public String toString(){ return name+"("+type+")"; }
    @Override public boolean equals(Object o){
        if(this==o) return true; if(!(o instanceof Location l)) return false;
        return name.equals(l.name);
    }
    @Override public int hashCode(){ return name.hashCode(); }
}
