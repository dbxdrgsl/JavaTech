package ap.lab06;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public final class Edge implements Serializable {
    @Serial private static final long serialVersionUID = 1L;
    public final int u, v;        // u < v canonical
    public final double len;
    public final Player owner;

    public Edge(int a, int b, double len, Player owner){
        if (a==b) throw new IllegalArgumentException("loop");
        if (a<b){ this.u=a; this.v=b; } else { this.u=b; this.v=a; }
        this.len = len; this.owner = Objects.requireNonNull(owner);
    }
    @Override public boolean equals(Object o){
        if(!(o instanceof Edge e)) return false;
        return u==e.u && v==e.v; // ignore owner in identity
    }
    @Override public int hashCode(){ return u*65537 + v; }
}
