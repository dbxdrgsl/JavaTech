package ap.lab05.io;

import ap.lab05.core.ImageItem;
import ap.lab05.core.RepoException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

/** JSON persistence using Jackson. */
public final class JsonPersister implements Persister {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Override public void save(Path file, Collection<ImageItem> items) throws RepoException {
        try { mapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), items); }
        catch (IOException e) { throw new RepoException("save failed: " + file, e); }
    }
    @Override public List<ImageItem> load(Path file) throws RepoException {
        try { return mapper.readValue(file.toFile(), new TypeReference<>(){}); }
        catch (IOException e) { throw new RepoException("load failed: " + file, e); }
    }
}
