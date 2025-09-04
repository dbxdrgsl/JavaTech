package ap.lab01;

import jakarta.servlet.http.*;
import java.io.*;
import java.security.SecureRandom;
import java.util.Arrays;

public class GraphServlet extends HttpServlet {
    private final SecureRandom rnd = new SecureRandom();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int n = parse(req.getParameter("numVertices"), 0);
        int m = parse(req.getParameter("numEdges"), -1);
        String format = req.getParameter("format"); // "txt" → plain text
        log(req, "n="+n+", m="+m+", format="+format);

        if (n<=0){ sendErr(resp,"numVertices must be >= 1"); return; }
        int max = n*(n-1)/2;
        if (m<0 || m>max) m = Math.min(max, n>1? (n-1)+rnd.nextInt(max-(n-1)+1) : 0);

        int[][] A = gen(n,m);

        if ("txt".equalsIgnoreCase(format)) {
            resp.setContentType("text/plain; charset=UTF-8");
            try(PrintWriter out=resp.getWriter()){ writeTxt(out, A); }
            return;
        }
        resp.setContentType("text/html; charset=UTF-8");
        try(PrintWriter out=resp.getWriter()){ writeHtml(out, A, n, m); }
    }

    private int[][] gen(int n,int m){
        int[][] A=new int[n][n]; int edges=0;
        for(int v=1; v<n; v++){ int u=rnd.nextInt(v); A[u][v]=A[v][u]=1; edges++; } // connect
        int rem = Math.max(0, m-edges);
        while(rem>0){ int u=rnd.nextInt(n), v=rnd.nextInt(n); if(u==v||A[u][v]==1) continue; A[u][v]=A[v][u]=1; rem--; }
        return A;
    }
    private void writeHtml(PrintWriter out,int[][] A,int n,int mReq){
        int[] deg=new int[n]; for(int i=0;i<n;i++) for(int j=0;j<n;j++) deg[i]+=A[i][j];
        int m = Arrays.stream(deg).sum()/2;
        out.println("<!doctype html><meta charset='utf-8'><h3>Adjacency matrix</h3>");
        out.printf("<p>n=%d, requested edges=%d, actual edges=%d; δ(G)=%d, Δ(G)=%d</p>%n",
                n, mReq, m, Arrays.stream(deg).min().orElse(0), Arrays.stream(deg).max().orElse(0));
        out.println("<style>table{border-collapse:collapse} td,th{border:1px solid #999;padding:4px;text-align:center}</style>");
        out.println("<table><tr><th></th>");
        for(int j=0;j<n;j++) out.print("<th>"+j+"</th>");
        out.println("</tr>");
        for(int i=0;i<n;i++){ out.print("<tr><th>"+i+"</th>"); for(int j=0;j<n;j++) out.print("<td>"+A[i][j]+"</td>"); out.println("</tr>");}
        out.println("</table><p><a href='/'>Back</a></p>");
    }
    private void writeTxt(PrintWriter out,int[][] A){
        int n=A.length; out.println(n);
        for(int i=0;i<n;i++){ for(int j=0;j<n;j++){ if(j>0) out.print(' '); out.print(A[i][j]); } out.println(); }
    }
    private int parse(String s,int d){ try{return Integer.parseInt(s);}catch(Exception e){return d;} }
    private void sendErr(HttpServletResponse r,String m) throws IOException { r.setStatus(400); r.setContentType("text/plain; charset=UTF-8"); r.getWriter().println("Error: "+m); }
    private void log(HttpServletRequest r,String p){ System.out.printf("[Graph] %s ip=%s ua=%s langs=%s params=%s%n",
            r.getMethod(), r.getRemoteAddr(), r.getHeader("User-Agent"), r.getHeader("Accept-Language"), p); }
}
