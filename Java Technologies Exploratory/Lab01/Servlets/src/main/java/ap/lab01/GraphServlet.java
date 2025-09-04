package ap.lab01;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.Arrays;

@WebServlet(name="GraphServlet", urlPatterns={"/graph"})
public class GraphServlet extends HttpServlet {
    private final SecureRandom rnd = new SecureRandom();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int n = parseInt(req.getParameter("numVertices"), 0);
        int m = parseInt(req.getParameter("numEdges"), -1);
        String format = req.getParameter("format"); // "txt" for plain

        logRequest(req, "numVertices="+n+", numEdges="+m+", format="+format);

        if (n <= 0) { sendError(resp, "numVertices must be >= 1"); return; }
        int maxEdges = n*(n-1)/2;
        if (m < 0 || m > maxEdges) m = Math.min(maxEdges, n - 1 + (n > 1 ? rnd.nextInt(maxEdges - (n - 1) + 1) : 0));

        int[][] A = randomSimpleUndirectedGraph(n, m);

        if ("txt".equalsIgnoreCase(format)) {
            resp.setContentType("text/plain; charset=UTF-8");
            try (PrintWriter out = resp.getWriter()) { writeMatrixText(out, A); }
            return;
        }

        resp.setContentType("text/html; charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) { writeMatrixHtml(out, A, n, m); }
    }

    private int[][] randomSimpleUndirectedGraph(int n, int m){
        int[][] A = new int[n][n];
        // ensure connectivity first using a random tree
        int edges = 0;
        for (int v = 1; v < n; v++) {
            int u = rnd.nextInt(v);
            A[u][v] = A[v][u] = 1; edges++;
        }
        // add remaining edges uniformly at random without duplicates
        int remaining = Math.max(0, m - edges);
        while (remaining > 0) {
            int u = rnd.nextInt(n), v = rnd.nextInt(n);
            if (u == v || A[u][v] == 1) continue;
            A[u][v] = A[v][u] = 1; remaining--;
        }
        return A;
    }

    private void writeMatrixHtml(PrintWriter out, int[][] A, int n, int m){
        int[] deg = new int[n]; for (int i=0;i<n;i++) for (int j=0;j<n;j++) deg[i]+=A[i][j];
        int mCount = Arrays.stream(deg).sum()/2;

        out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Graph</title>");
        out.println("<style>table{border-collapse:collapse} td,th{border:1px solid #999;padding:4px;text-align:center}</style>");
        out.println("</head><body><h3>Adjacency matrix ("+n+" vertices, requested edges="+m+", actual edges="+mCount+")</h3>");
        out.println("<table><tr><th></th>");
        for (int j=0;j<n;j++) out.print("<th>"+j+"</th>");
        out.println("</tr>");
        for (int i=0;i<n;i++){
            out.print("<tr><th>"+i+"</th>");
            for (int j=0;j<n;j++) out.print("<td>"+A[i][j]+"</td>");
            out.println("</tr>");
        }
        out.println("</table>");
        out.println("<p>Degrees δ(G)="+Arrays.stream(deg).min().orElse(0)+", Δ(G)="+Arrays.stream(deg).max().orElse(0)+", m="+mCount+"</p>");
        out.println("<p><a href='index.html'>Back</a></p></body></html>");
    }

    private void writeMatrixText(PrintWriter out, int[][] A){
        int n = A.length;
        out.println(n);
        for (int i=0;i<n;i++){
            for (int j=0;j<n;j++){
                if (j>0) out.print(' ');
                out.print(A[i][j]);
            }
            out.println();
        }
    }

    private int parseInt(String s, int def){ try { return Integer.parseInt(s); } catch (Exception e){ return def; } }

    private void sendError(HttpServletResponse resp, String msg) throws IOException {
        resp.setStatus(400);
        resp.setContentType("text/plain; charset=UTF-8");
        resp.getWriter().println("Error: " + msg);
    }

    private void logRequest(HttpServletRequest req, String params){
        String method = req.getMethod();
        String ip = req.getRemoteAddr();
        String ua = req.getHeader("User-Agent");
        String langs = req.getHeader("Accept-Language");
        getServletContext().log(String.format("[Graph] %s ip=%s ua=%s langs=%s params=%s", method, ip, ua, langs, params));
    }
}
