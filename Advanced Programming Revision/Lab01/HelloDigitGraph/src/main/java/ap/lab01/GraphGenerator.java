// src/main/java/ap/lab01/GraphGenerator.java
package ap.lab01;

import java.util.*;

public class GraphGenerator {
    /** Author: Dragos-Andrei Bobu */
    public static void main(String[] args) {
        if (args.length < 2) { System.out.println("Usage: java GraphGenerator n k"); return; }
        int n = Integer.parseInt(args[0]);
        int k = Integer.parseInt(args[1]);
        if (n < 1 || k < 1) { System.out.println("n,k must be >=1"); return; }

        long t0 = System.nanoTime();
        BitSet[] A = new BitSet[n];
        for (int i = 0; i < n; i++) A[i] = new BitSet(n);

        // Ensure a k-clique on vertices [0..k-1] if k<=n
        if (k <= n) for (int i = 0; i < k; i++) for (int j = i+1; j < k; j++) setUndir(A, i, j, true);

        // Ensure a k-stable set on vertices [n-k..n-1] if 2k <= n
        boolean stableOK = false;
        if (2*k <= n) {
            for (int i = n-k; i < n; i++) for (int j = i+1; j < n; j++) setUndir(A, i, j, false);
            stableOK = true;
        }

        // Randomly add other edges (skip diagonal). p ~ 0.5
        Random rng = new Random();
        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                // Preserve enforced edges: clique part already set; stable part already cleared
                boolean forcedClique = (i < k && j < k);
                boolean forcedStable = (2*k <= n) && (i >= n-k && j >= n-k);
                if (!forcedClique && !forcedStable) setUndir(A, i, j, rng.nextBoolean());
            }
        }

        // Metrics
        long m = 0; int minDeg = Integer.MAX_VALUE, maxDeg = Integer.MIN_VALUE; long sumDeg = 0;
        for (int i = 0; i < n; i++) {
            int deg = A[i].cardinality(); // since we mirror edges, this is degree
            minDeg = Math.min(minDeg, deg);
            maxDeg = Math.max(maxDeg, deg);
            sumDeg += deg;
            // clear diagonal just in case
            A[i].clear(i);
        }
        // count edges once
        for (int i = 0; i < n; i++) m += A[i].nextSetBit(i+1) < 0 ? 0 : A[i].get(i+1, n).cardinality();

        boolean handshaking = (sumDeg == 2L * m);

        // Output: for large n, skip matrix
        if (n <= 2000) {
            System.out.println(toPretty(A));
        } else {
            System.out.println("(matrix omitted for n > 2000)");
        }
        System.out.printf("n=%d, k=%d, edges m=%d%n", n, k, m);
        System.out.printf("Δ(G)=%d, δ(G)=%d%n", maxDeg, minDeg);
        System.out.printf("∑deg = %d, 2m = %d, check=%s%n", sumDeg, 2L*m, handshaking ? "OK" : "FAIL");

        if (!stableOK && 2*k > n)
            System.out.println("Note: cannot guarantee both a k-clique and a k-stable set when 2k > n.");

        long ms = (System.nanoTime() - t0) / 1_000_000;
        if (n >= 30_000) System.out.printf("Time: %d ms (use -Xms4G -Xmx4G if needed)%n", ms);
    }

    private static void setUndir(BitSet[] A, int i, int j, boolean v) {
        if (v) { A[i].set(j); A[j].set(i); } else { A[i].clear(j); A[j].clear(i); }
    }

    private static String toPretty(BitSet[] A) {
        int n = A.length;
        StringBuilder sb = new StringBuilder(n*(n+1));
        final char ONE = '◼', ZERO = '·';
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) sb.append(A[i].get(j) ? ONE : ZERO);
            sb.append('\n');
        }
        return sb.toString();
    }
}
