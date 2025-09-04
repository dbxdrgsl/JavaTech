package ap.lab01;

import jakarta.servlet.http.*;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.*;

/** Bonus: first k spanning trees of K_n with w(i,j)=i+j, in increasing total weight. */
public class SpanningTreesServlet extends HttpServlet {

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int n = parse(req.getParameter("n"), 0);
        int k = parse(req.getParameter("k"), 0);
        if (n < 2 || k < 1) { bad(resp, "n>=2 and k>=1"); return; }
        int L = n - 2;

        long t0 = System.nanoTime();
        List<Result> outTrees = firstKTrees(n, k);
        long ms = Math.round((System.nanoTime() - t0) / 1e6);

        resp.setContentType("text/plain; charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.printf("n=%d, k=%d, generated=%d, time=%d ms%n", n, k, outTrees.size(), ms);
            for (int i = 0; i < outTrees.size(); i++) {
                var r = outTrees.get(i);
                out.printf("[%d] weight=%d  prufer=%s  edges=%s%n", i+1, r.weight, Arrays.toString(r.code), edgesToString(r.edges));
            }
            if (L == 0) out.println("Note: n=2 has a single tree.");
        }
    }

    private static String edgesToString(int[][] e){
        var sb = new StringBuilder();
        sb.append('[');
        for (int i=0;i<e.length;i++){
            if (i>0) sb.append(", ");
            sb.append('(').append(e[i][0]).append(',').append(e[i][1]).append(')');
        }
        return sb.append(']').toString();
    }

    private static int parse(String s, int d){ try { return Integer.parseInt(s); } catch (Exception e){ return d; } }
    private static void bad(HttpServletResponse r, String m) throws IOException { r.setStatus(400); r.setContentType("text/plain; charset=UTF-8"); r.getWriter().println("Error: "+m); }

    /** Result bag. */
    private static final class Result {
        final int[] code; final int[][] edges; final long weight;
        Result(int[] code, int[][] edges, long weight){ this.code=code; this.edges=edges; this.weight=weight; }
    }

    /** Generate first k trees in increasing weight, using best-first over Prufer codes by sum. */
    private static List<Result> firstKTrees(int n, int k) {
        int L = n - 2;
        long base = n * (long)(n + 1) / 2; // constant term

        // min-heap by (sum, lexicographic code)
        record Node(int sum, int[] code) { }
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator
                .comparingInt((Node a) -> a.sum)
                .thenComparing(a -> Arrays.toString(a.code))); // stable tie-break

        // visited states to avoid duplicates
        Set<String> seen = new HashSet<>();

        // seed = [1,1,...,1]
        int[] seed = new int[L]; Arrays.fill(seed, 1);
        pq.add(new Node(L==0?0:Arrays.stream(seed).sum(), seed));
        seen.add(Arrays.toString(seed));

        ArrayList<Result> out = new ArrayList<>(k);

        while (!pq.isEmpty() && out.size() < k) {
            Node cur = pq.poll();
            int[] code = cur.code;

            // decode Prufer → edges
            int[][] edges = decodePruferToEdges(n, code);

            long w = base; for (int v : code) w += v; // weight = base + sum(code)
            out.add(new Result(code.clone(), edges, w));

            // push neighbors: +1 at each position (<= n)
            for (int i = 0; i < code.length; i++) {
                if (code[i] == n) continue;
                int[] nxt = code.clone();
                nxt[i] = code[i] + 1;
                String key = Arrays.toString(nxt);
                if (seen.add(key)) {
                    int s = cur.sum + 1;
                    pq.add(new Node(s, nxt));
                }
            }
        }
        // special case n=2 (L=0): one tree with edge (1,2)
        if (n == 2 && out.isEmpty()) {
            out.add(new Result(new int[0], new int[][]{{1,2}}, 1+2)); // weight = 3
        }

        return out;
    }

    /** Standard Prufer decode producing edges as 1-based labels. */
    private static int[][] decodePruferToEdges(int n, int[] code) {
        int[] deg = new int[n+1];
        Arrays.fill(deg, 1);
        for (int v : code) deg[v]++;

        PriorityQueue<Integer> leaves = new PriorityQueue<>();
        for (int v = 1; v <= n; v++) if (deg[v] == 1) leaves.add(v);

        int[][] E = new int[n-1][2];
        for (int i = 0; i < code.length; i++) {
            int u = leaves.poll();
            int v = code[i];
            E[i][0] = u; E[i][1] = v;
            if (--deg[v] == 1) leaves.add(v);
        }
        // last edge
        int u = leaves.poll(), v = leaves.poll();
        E[n-2][0] = u; E[n-2][1] = v;
        return E;
    }
}
