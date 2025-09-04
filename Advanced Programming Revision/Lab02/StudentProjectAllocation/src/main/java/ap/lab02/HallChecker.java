package ap.lab02;

import org.jetbrains.annotations.NotNull;

/** Brute-force Hall's condition for small n: for all nonempty S ⊆ Students, |N(S)| ≥ |S|. */
public final class HallChecker {
    public static boolean hasPerfectAllocation(Student @NotNull [] students, Project[] projects){
        int n = students.length;
        if(n==0) return true;
        // map project equals to index
        int m = projects.length;
        for(int mask=1; mask < (1<<n); mask++){
            boolean[] reachable = new boolean[m];
            int sCount = Integer.bitCount(mask);
            for(int i=0;i<n;i++) if(((mask>>i)&1)==1){
                Project[] prefs = students[i].getAcceptable();
                for(Project p : prefs){
                    int idx = indexOf(projects, p);
                    if(idx>=0) reachable[idx]=true;
                }
            }
            int nCount=0; for(boolean b:reachable) if(b) nCount++;
            if(nCount < sCount) return false;
        }
        return true;
    }
    private static int indexOf(Project @NotNull [] arr, Project key){
        for(int i=0;i<arr.length;i++) if(arr[i].equals(key)) return i;
        return -1;
    }
}
