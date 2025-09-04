package ap.lab05.core;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/** In-memory repository with add/remove/update/display. */
public final class ImageRepo {
    private final Map<String, ImageItem> byName = new LinkedHashMap<>();

    public void add(ImageItem item) throws RepoException {
        if (byName.containsKey(item.name()))
            throw new RepoException("duplicate image name: " + item.name());
        byName.put(item.name(), item);
    }

    public void remove(String name) throws NotFoundException {
        if (byName.remove(name) == null) throw new NotFoundException("image not found: " + name);
    }

    public void update(String name, Optional<String> newName, Optional<LocalDate> newDate,
                       Optional<List<String>> newTags, Optional<Path> newPath) throws RepoException {
        ImageItem old = byName.get(name);
        if (old == null) throw new NotFoundException("image not found: " + name);
        String nName = newName.orElse(old.name());
        if (!nName.equals(name) && byName.containsKey(nName))
            throw new RepoException("name already in use: " + nName);
        ImageItem neu = new ImageItem(nName, newDate.orElse(old.date()),
                newTags.orElse(old.tags()), newPath.orElse(old.path()));
        byName.remove(name);
        byName.put(nName, neu);
    }

    public Collection<ImageItem> all() { return Collections.unmodifiableCollection(byName.values()); }

    public ImageItem get(String name) throws NotFoundException {
        ImageItem x = byName.get(name);
        if (x == null) throw new NotFoundException("image not found: " + name);
        return x;
    }

    /** Display image via OS. */
    public void display(String name) throws RepoException {
        ImageItem x = get(name);
        if (!Files.exists(x.path())) throw new RepoException("file missing: " + x.path());
        try {
            if (!Desktop.isDesktopSupported()) throw new RepoException("Desktop API not supported.");
            Desktop.getDesktop().open(x.path().toFile());
        } catch (IOException e) {
            throw new RepoException("cannot open: " + x.path(), e);
        }
    }
}
