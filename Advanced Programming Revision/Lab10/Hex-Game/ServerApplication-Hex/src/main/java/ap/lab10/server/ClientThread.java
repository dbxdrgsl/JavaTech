package ap.lab10.server;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public final class ClientThread implements Runnable {
    private final Socket sock; private final GameServer srv;
    public ClientThread(Socket s, GameServer srv){ this.sock=s; this.srv=srv; }

    @Override public void run() {
        try (var in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
             var out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())), true)) {
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) { send(out, "ERR empty"); continue; }
                if (line.equalsIgnoreCase("stop")) { srv.stopServer(); send(out, "Server stopped"); break; }
                handle(line, out);
            }
        } catch (Exception ignored) { }
        try { sock.close(); } catch (IOException ignored) {}
    }

    private void send(PrintWriter out, String... lines){
        for (String s: lines) out.println(s);
        out.println("."); // sentinel
    }

    private void handle(String line, PrintWriter out){
        String[] t = line.split("\\s+");
        String cmd = t[0].toLowerCase();
        try {
            switch (cmd) {
                case "ping" -> send(out, "pong");
                case "games" -> send(out, "GAMES "+srv.games.keySet());
                case "create" -> { // create <gameId> [size] [timeSec]
                    if (t.length<2) { send(out,"ERR usage: create <id> [size=7] [time=60]"); return; }
                    String id=t[1];
                    int n = (t.length>=3? Integer.parseInt(t[2]):7);
                    int T = (t.length>=4? Integer.parseInt(t[3]):60);
                    if (srv.games.putIfAbsent(id, new Game(id,n,T))!=null) { send(out,"ERR exists"); return; }
                    send(out,"OK created "+id+" size="+n+" time="+T+"s");
                }
                case "join" -> { // join <id> <playerName> <R|B>
                    if (t.length<4) { send(out,"ERR usage: join <id> <name> <R|B>"); return; }
                    Game g = srv.games.get(t[1]); if (g==null){ send(out,"ERR no such game"); return; }
                    var color = t[3].equalsIgnoreCase("R")? PlayerColor.RED : PlayerColor.BLUE;
                    send(out, g.join(t[2], color));
                }
                case "move" -> { // move <id> <name> <row> <col>
                    if (t.length<5){ send(out,"ERR usage: move <id> <name> <r> <c>"); return; }
                    Game g = srv.games.get(t[1]); if (g==null){ send(out,"ERR no such game"); return; }
                    int r=Integer.parseInt(t[3]), c=Integer.parseInt(t[4]);
                    send(out, g.move(t[2], r, c));
                }
                case "show" -> {
                    if (t.length<2){ send(out,"ERR usage: show <id>"); return; }
                    Game g = srv.games.get(t[1]); if (g==null){ send(out,"ERR no such game"); return; }
                    send(out, g.show().split("\n"));
                }
                case "clock" -> {
                    if (t.length<2){ send(out,"ERR usage: clock <id>"); return; }
                    Game g = srv.games.get(t[1]); if (g==null){ send(out,"ERR no such game"); return; }
                    send(out, g.clock());
                }
                default -> send(out, "Server received the request: "+line);
            }
        } catch (Exception e){
            send(out, "ERR "+e.getMessage(), "DBG "+ Arrays.toString(t));
        }
    }
}
