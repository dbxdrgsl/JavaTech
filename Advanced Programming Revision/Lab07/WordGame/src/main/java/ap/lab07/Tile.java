package ap.lab07;

import java.io.Serial;
import java.io.Serializable;

public record Tile(char letter, int points) implements Serializable {
    @Serial private static final long serialVersionUID = 1L;
}
