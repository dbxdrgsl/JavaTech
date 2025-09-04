package ap.lab06;

import java.io.Serial;
import java.io.Serializable;

public record Dot(double x, double y) implements Serializable {
    @Serial private static final long serialVersionUID = 1L;
    public double dist(Dot o){ double dx=x-o.x, dy=y-o.y; return Math.hypot(dx,dy); }
}
