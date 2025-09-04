package ap.lab10.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;

public final class GameServer {
    private final int port;
    private volatile boolean running=true;
    private ServerSocket server;
    final ExecutorService pool = Executors.newCachedThreadPool();
    final Map<String, Game> games = new ConcurrentHashMap<>();

    public GameServer(int port){ this.port=port; }

    public void start() throws IOException {
        server = new ServerSocket(port);
        System.out.println("Server on port " + port);
        while (running) {
            try {
                Socket s = server.accept();
                pool.submit(new ClientThread(s, this));
            } catch (IOException e){
                if (running) System.err.println("accept failed: "+e.getMessage());
            }
        }
    }

    public void stopServer(){
        running=false;
        try { if (server!=null) server.close(); } catch (IOException ignored) {}
        pool.shutdownNow();
    }

    public static void main(String[] args) throws Exception {
        int port = args.length>0? Integer.parseInt(args[0]) : 5000;
        new GameServer(port).start();
    }
}
