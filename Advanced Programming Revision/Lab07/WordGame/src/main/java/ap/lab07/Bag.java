package ap.lab07;

import java.util.*;

public final class Bag {
    private final Map<Character,Integer> letterPoints = new HashMap<>();
    private final Deque<Tile> tiles = new ArrayDeque<>();
    private final Random rnd = new Random(7);

    public Bag() {
        for (char c='A'; c<='Z'; c++) letterPoints.put(c, 1 + rnd.nextInt(10));
        // 10 tiles per letter
        ArrayList<Tile> list = new ArrayList<>(26*10);
        for (char c='A'; c<='Z'; c++) for (int i=0;i<10;i++) list.add(new Tile(c, letterPoints.get(c)));
        Collections.shuffle(list, rnd);
        tiles.addAll(list);
    }

    public synchronized List<Tile> draw(int k) {
        if (k<=0 || tiles.isEmpty()) return List.of();
        ArrayList<Tile> out = new ArrayList<>(Math.min(k, tiles.size()));
        for (int i=0; i<k && !tiles.isEmpty(); i++) out.add(tiles.pollFirst());
        return out;
    }

    public synchronized int size(){ return tiles.size(); }
    public int pointsOf(char c){ return letterPoints.get(Character.toUpperCase(c)); }
}
