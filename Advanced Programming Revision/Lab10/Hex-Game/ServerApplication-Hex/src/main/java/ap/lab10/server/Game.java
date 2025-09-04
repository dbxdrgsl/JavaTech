package ap.lab10.server;

import java.util.*;

public final class Game {
    public final String id;
    public final int n;
    private final char[][] b; // '.' empty, 'R', 'B'
    private final Map<String,PlayerColor> who = new LinkedHashMap<>();
    private PlayerColor turn = PlayerColor.RED;
    private boolean finished=false; private String winnerMsg="";
    // blitz time (ms)
    private final Map<PlayerColor,Long> remain = new EnumMap<>(PlayerColor.class);
    private PlayerColor ticking = null; private long tickStart = 0L;

    public Game(String id, int n, int timeSec){
        this.id=id; this.n=n; this.b=new char[n][n];
        for (char[] r: b) Arrays.fill(r,'.');
        long T = timeSec*1000L;
        remain.put(PlayerColor.RED, T); remain.put(PlayerColor.BLUE, T);
    }

    public synchronized String join(String name, PlayerColor color){
        if (finished) return "ERR game finished";
        if (who.containsKey(name)) return "ERR name taken";
        if (who.values().contains(color)) return "ERR color taken";
        if (who.size()>=2) return "ERR game full";
        who.put(name, color);
        if (who.size()==2) { ticking = PlayerColor.RED; tickStart = System.currentTimeMillis(); }
        return "OK joined as " + color;
    }

    public synchronized String move(String name, int r, int c){
        if (finished) return "ERR game finished";
        var color = who.get(name);
        if (color==null) return "ERR not a player";
        if (who.size()<2) return "ERR waiting opponent";
        if (color != turn) return "ERR not your turn";
        // clock
        long now = System.currentTimeMillis();
        if (ticking==color) {
            long left = remain.get(color) - (now - tickStart);
            remain.put(color, left);
            if (left<=0) { finished=true; winnerMsg="TIMEOUT "+color+" loses"; return "LOSE TIMEOUT"; }
        }
        if (r<0||r>=n||c<0||c>=n) return "ERR out of board";
        if (b[r][c] != '.') return "ERR occupied";
        b[r][c] = (color==PlayerColor.RED? 'R':'B');

        if (hasWon(color)) { finished=true; winnerMsg="WIN "+color; }
        // switch turn + start ticking for opponent
        turn = turn.opp(); ticking = turn; tickStart = now;
        return finished? ("WIN "+color) : "OK";
    }

    public synchronized String clock(){
        long now = System.currentTimeMillis();
        long r = remain.get(PlayerColor.RED);
        long b = remain.get(PlayerColor.BLUE);
        if (ticking==PlayerColor.RED) r -= (now - tickStart);
        if (ticking==PlayerColor.BLUE) b -= (now - tickStart);
        return "RED="+Math.max(0,r/1000)+"s BLUE="+Math.max(0,b/1000)+"s";
    }

    public synchronized String show(){
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<n;i++){
            sb.append(" ".repeat(i));
            for(int j=0;j<n;j++){ sb.append(b[i][j]==0?'.':b[i][j]).append(' '); }
            sb.append('\n');
        }
        sb.append("turn=").append(turn).append(" ").append(clock());
        if (finished) sb.append(" | ").append(winnerMsg);
        return sb.toString();
    }

    public synchronized List<String> players(){
        return who.entrySet().stream().map(e->e.getKey()+":"+e.getValue()).toList();
    }

    private boolean hasWon(PlayerColor col){
        char want = (col==PlayerColor.RED? 'R':'B');
        ArrayDeque<int[]> q=new ArrayDeque<>();
        boolean[][] vis=new boolean[n][n];
        if (col==PlayerColor.RED){
            for(int j=0;j<n;j++) if(b[0][j]==want){ q.add(new int[]{0,j}); vis[0][j]=true; }
            while(!q.isEmpty()){
                int[] x=q.poll(); if (x[0]==n-1) return true;
                for(int[] nb: neigh(x[0],x[1])) if(!vis[nb[0]][nb[1]] && b[nb[0]][nb[1]]==want){ vis[nb[0]][nb[1]]=true; q.add(nb); }
            }
        } else {
            for(int i=0;i<n;i++) if(b[i][0]==want){ q.add(new int[]{i,0}); vis[i][0]=true; }
            while(!q.isEmpty()){
                int[] x=q.poll(); if (x[1]==n-1) return true;
                for(int[] nb: neigh(x[0],x[1])) if(!vis[nb[0]][nb[1]] && b[nb[0]][nb[1]]==want){ vis[nb[0]][nb[1]]=true; q.add(nb); }
            }
        }
        return false;
    }
    private List<int[]> neigh(int r,int c){
        int[][] d={{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0}};
        ArrayList<int[]> out=new ArrayList<>(6);
        for(var v:d){ int R=r+v[0], C=c+v[1]; if(0<=R&&R<n&&0<=C&&C<n) out.add(new int[]{R,C}); }
        return out;
    }
}
