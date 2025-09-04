package ap.lab02;

import org.jetbrains.annotations.NotNull;

/** Simple greedy: iterate students; for each, pick first acceptable free project. */
public final class AllocationGreedy {
    public static void allocate(@NotNull Problem pb){
        Student[] S = pb.getStudents();
        Project[] P = pb.getProjects();
        for(Student s : S){
            Project[] prefs = s.getAcceptable();
            for(Project p : prefs){
                // verify p exists in pb.projects and is free
                Project ref = find(P, p);
                if(ref != null && ref.getAssigned()==null){
                    ref.setAssigned(s);
                    break;
                }
            }
        }
    }

    private static Project find(Project @NotNull [] arr, Project key){
        for(Project p : arr) if(p.equals(key)) return p;
        return null;
    }
}
