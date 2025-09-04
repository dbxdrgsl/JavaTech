package ap.lab05.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/** An image item. */
public record ImageItem(
        String name,
        LocalDate date,
        List<String> tags,
        Path path
) implements Serializable {
    @Serial private static final long serialVersionUID = 1L;
    @JsonCreator public ImageItem(
            @JsonProperty("name") String name,
            @JsonProperty("date") LocalDate date,
            @JsonProperty("tags") List<String> tags,
            @JsonProperty("path") Path path) {
        this.name = Objects.requireNonNull(name);
        this.date = Objects.requireNonNull(date);
        this.tags = List.copyOf(Objects.requireNonNull(tags));
        this.path = Objects.requireNonNull(path);
    }
    @Override public String toString() {
        return "ImageItem{name='%s', date=%s, tags=%s, path=%s}".formatted(name, date, tags, path);
    }
}
