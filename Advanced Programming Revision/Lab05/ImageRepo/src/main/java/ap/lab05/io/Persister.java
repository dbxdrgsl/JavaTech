package ap.lab05.io;

import ap.lab05.core.ImageItem;
import ap.lab05.core.RepoException;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public interface Persister {
    void save(Path file, Collection<ImageItem> items) throws RepoException;
    List<ImageItem> load(Path file) throws RepoException;
}
