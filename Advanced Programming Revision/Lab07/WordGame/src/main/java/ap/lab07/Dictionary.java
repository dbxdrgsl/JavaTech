package ap.lab07;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public final class Dictionary {
    private final Set<String> words;

    public Dictionary() { this.words = defaultWords(); }

    public Dictionary(Path file) throws IOException {
        try (var lines = Files.lines(file)) {
            this.words = lines.map(s->s.replaceAll("[^A-Za-z]","").toUpperCase())
                    .filter(s->!s.isBlank()).collect(Collectors.toCollection(HashSet::new));
        }
    }

    public boolean contains(String w){ return words.contains(w.toUpperCase()); }
    public Set<String> all(){ return words; }

    private static Set<String> defaultWords() {
        String[] w = {
                // 2-letter
                "TO","OF","IN","ON","AT","IT","IS","AS","AN","OR","BY","BE","DO","GO","NO","SO","UP","US","WE",
                // 3-letter
                "CAT","DOG","ANT","EAR","EEL","EGO","ICE","INK","OAK","OAR","OXO","PAN","PEN","PIN","PIT","POT",
                "RAT","ROD","ROW","RUN","SAP","SEA","SEE","SEW","SIP","SIT","SON","SUN","TAP","TAR","TEN","TIN",
                // 4-letter
                "JAVA","CODE","WORD","PLAY","GAME","NODE","TREE","LOCK","QUEUE","DATA","NOTE","TIME"
        };
        return new HashSet<>(Arrays.asList(w));
    }
}
