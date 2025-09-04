package ap.lab07;

import java.util.*;

public final class Player implements Runnable {
    final String name;
    final Game game;
    final int index;
    final List<Tile> rack = new ArrayList<>(7);
    final Random rnd = new Random();

    public Player(String name, Game game, int index) {
        this.name = name; this.game = game; this.index=index;
    }

    @Override public void run() {
        // initial draw
        rack.addAll(game.bag.draw(7));

        while (game.running.get()) {
            synchronized (game.turnLock) {
                while (game.running.get() && game.turn != index) {
                    try { game.turnLock.wait(); } catch (InterruptedException ignored) {}
                }
                if (!game.running.get()) break;

                // ----- my turn -----
                boolean played = playOnce();
                if (!played && game.bag.size()==0) game.noMoveCount++; else game.noMoveCount=0;
                if (game.noMoveCount >= game.nPlayers) game.stop(); // end condition: empty bag and full round of no moves

                game.turn = (game.turn + 1) % game.nPlayers;
                game.turnLock.notifyAll();
            }
        }
    }

    private boolean playOnce() {
        String best = chooseBestWord();
        if (best != null) {
            int points = Util.scoreOf(best, game.bag);
            game.board.submit(name, best, points);
            // consume letters used
            consume(best);
            // draw k new tiles
            rack.addAll(game.bag.draw(best.length()));
            return true;
        } else {
            // cannot form word: discard rack and draw 7 new or as many as possible
            rack.clear();
            rack.addAll(game.bag.draw(7));
            System.out.println(name + " passes");
            return false;
        }
    }

    private String chooseBestWord() {
        if (rack.isEmpty()) return null;
        Map<Character,Integer> have = Util.multisetOf(rack);
        String best = null; int bestScore = -1, bestLen = -1;
        for (String w : game.dict.all()) {
            String W = w.toUpperCase();
            if (Util.canForm(W, have)) {
                int sc = Util.scoreOf(W, game.bag);
                int len = W.length();
                if (sc > bestScore || (sc==bestScore && len > bestLen)) {
                    best=W; bestScore=sc; bestLen=len;
                }
            }
        }
        return best;
    }

    private void consume(String w) {
        for (char c : w.toCharArray()) {
            for (int i=0;i<rack.size();i++) {
                if (rack.get(i).letter()==c) { rack.remove(i); break; }
            }
        }
    }
}
