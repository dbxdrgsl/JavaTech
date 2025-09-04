package ap.lab07;

import java.util.*;

public final class Board {
    private final List<String> log = new ArrayList<>();
    private final Map<String,Integer> scores = new LinkedHashMap<>();

    public synchronized void submit(String player, String word, int points) {
        scores.merge(player, points, Integer::sum);
        log.add("%s played %s (+%d)".formatted(player, word, points));
        System.out.println(log.get(log.size()-1));
    }

    public synchronized Map<String,Integer> scores(){ return new LinkedHashMap<>(scores); }
    public synchronized List<String> history(){ return List.copyOf(log); }
}
