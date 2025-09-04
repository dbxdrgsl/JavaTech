package ap.lab07;

import java.util.*;

public final class Util {
    private Util(){}
    public static Map<Character,Integer> multisetOf(List<Tile> rack){
        Map<Character,Integer> m = new HashMap<>();
        for (Tile t: rack) m.merge(t.letter(), 1, Integer::sum);
        return m;
    }
    public static boolean canForm(String w, Map<Character,Integer> ms){
        Map<Character,Integer> need = new HashMap<>();
        for (char c: w.toCharArray()) need.merge(c,1,Integer::sum);
        for (var e: need.entrySet()) if (ms.getOrDefault(e.getKey(),0) < e.getValue()) return false;
        return true;
    }
    public static int scoreOf(String w, Bag bag){
        int s=0; for (char c: w.toCharArray()) s += bag.pointsOf(c); return s;
    }
}
