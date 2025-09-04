package ap.lab06;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public final class GameModel implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    public final ArrayList<Dot> dots = new ArrayList<>();
    public final LinkedHashSet<Edge> edges = new LinkedHashSet<>();
    public Player turn = Player.BLUE;

    public void clear(){ dots.clear(); edges.clear(); turn = Player.BLUE; }

    public boolean hasEdge(int a, int b){
        int u=Math.min(a,b), v=Math.max(a,b);
        return edges.contains(new Edge(u,v,0, Player.BLUE));
    }

    public boolean addEdge(int a, int b){
        if (a==b || hasEdge(a,b)) return false;
        double len = dots.get(a).dist(dots.get(b));
        edges.add(new Edge(a,b,len,turn));
        turn = turn.next();
        return true;
    }

    public boolean isConnected(){
        int n = dots.size(); if (n==0) return true;
        DSU dsu = new DSU(n);
        for (Edge e: edges) dsu.union(e.u, e.v);
        int r = dsu.find(0);
        for (int i=1;i<n;i++) if (dsu.find(i)!=r) return false;
        return edges.size() >= n-1; // connected and enough edges
    }

    public double totalLength(){
        double s=0; for (Edge e: edges) s+=e.len; return s;
    }

    public double mstLength(){
        int n = dots.size(); if (n<=1) return 0;
        ArrayList<FullEdge> all = new ArrayList<>(n*(n-1)/2);
        for (int i=0;i<n;i++) for (int j=i+1;j<n;j++) all.add(new FullEdge(i,j,dots.get(i).dist(dots.get(j))));
        all.sort(Comparator.comparingDouble(fe->fe.w));
        DSU dsu = new DSU(n);
        double sum=0; int used=0;
        for (FullEdge fe: all){
            if (dsu.union(fe.u, fe.v)){ sum += fe.w; used++; if (used==n-1) break; }
        }
        return sum;
    }

    private static final class FullEdge {
        final int u,v; final double w; FullEdge(int u,int v,double w){this.u=u;this.v=v;this.w=w;}
    }
    private static final class DSU implements Serializable {
        final int[] p, r;
        DSU(int n){ p=new int[n]; r=new int[n]; for(int i=0;i<n;i++) p[i]=i; }
        int find(int x){ return p[x]==x? x : (p[x]=find(p[x])); }
        boolean union(int a,int b){ a=find(a); b=find(b); if(a==b) return false;
            if(r[a]<r[b]){int t=a;a=b;b=t;} p[b]=a; if(r[a]==r[b]) r[a]++; return true; }
    }
}
