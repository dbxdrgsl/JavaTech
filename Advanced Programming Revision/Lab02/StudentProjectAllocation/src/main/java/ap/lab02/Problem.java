package ap.lab02;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/** Holds arrays of students, teachers, projects. Prevents duplicates. */
public final class Problem {
    private Student[] students = new Student[0];
    private Teacher[] teachers = new Teacher[0];
    private Project[] projects = new Project[0];

    public void addStudent(Student s){ if(!contains(students,s)) students = append(students,s); }
    public void addTeacher(Teacher t){ if(!contains(teachers,t)) teachers = append(teachers,t); }
    public void addProject(Project p){ if(!contains(projects,p)) projects = append(projects,p); }

    public Student[] getStudents(){ return students.clone(); }
    public Teacher[] getTeachers(){ return teachers.clone(); }
    public Project[] getProjects(){ return projects.clone(); }

    /** All persons, students then teachers, as array. */
    public Person @NotNull [] getAllPersons(){
        Person[] a = new Person[students.length + teachers.length];
        System.arraycopy(students,0,a,0,students.length);
        System.arraycopy(teachers,0,a,students.length,teachers.length);
        return a;
    }

    private static <T> boolean contains(T @NotNull [] a, T x){ for(T y:a) if(y.equals(x)) return true; return false; }
    private static <T> T @NotNull [] append(T[] a, T x){
        a = Arrays.copyOf(a, a.length+1); a[a.length-1]=x; return a;
    }

    @Override public @NotNull String toString(){
        return "Problem{students="+Arrays.toString(students)+", teachers="+Arrays.toString(teachers)+
                ", projects="+Arrays.toString(projects)+"}";
    }
}
