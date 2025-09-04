package ap.lab07;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Game {
    final Bag bag = new Bag();
    final Board board = new Board();
    final Dictionary dict;
    final Object turnLock = new Object();
    final int nPlayers;
    volatile int turn = 0;
    volatile int noMoveCount = 0;
    final AtomicBoolean running = new AtomicBoolean(true);
    final long timeLimitMs;

    public Game(Dictionary dict, int nPlayers, long timeLimitMs) {
        this.dict = dict; this.nPlayers = nPlayers; this.timeLimitMs = timeLimitMs;
    }

    public void start(List<Player> players) throws InterruptedException {
        // timekeeper
        Thread tk = new Thread(() -> {
            long t0 = System.currentTimeMillis();
            while (running.get()) {
                long ms = System.currentTimeMillis() - t0;
                System.out.printf("[time] %.1fs, bag=%d%n", ms/1000.0, bag.size());
                if (ms > timeLimitMs) { System.out.println("[time] limit reached"); stop(); }
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
        }, "timekeeper");
        tk.setDaemon(true); tk.start();

        // players
        List<Thread> threads = new ArrayList<>();
        for (Player p: players) {
            Thread t = new Thread(p, "player-"+p.name);
            threads.add(t); t.start();
        }
        for (Thread t: threads) t.join();
        running.set(false);
    }

    void stop() {
        running.set(false);
        synchronized (turnLock) { turnLock.notifyAll(); }
    }
}
