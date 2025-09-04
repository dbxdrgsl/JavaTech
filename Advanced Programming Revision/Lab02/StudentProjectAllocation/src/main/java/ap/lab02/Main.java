package ap.lab02;

import java.time.LocalDate;

public class Main {
    /** Entry point for Lab 2 demo. */
    public static void main(String[] args) {
        // Teachers and projects
        Teacher t1 = new Teacher("Dr. Ada", LocalDate.of(1980,5,10), null);
        Teacher t2 = new Teacher("Dr. Turing", LocalDate.of(1975,3,1), null);

        Project p1 = new Project("P1", ProjectType.THEORETICAL, t1);
        Project p2 = new Project("P2", ProjectType.PRACTICAL,   t1);
        Project p3 = new Project("P3", ProjectType.THEORETICAL, t2);
        Project p4 = new Project("P4", ProjectType.PRACTICAL,   t2);

        t1.setProposed(new Project[]{p1,p2});
        t2.setProposed(new Project[]{p3,p4});

        // Students with preferences (arrays)
        Student s1 = new Student("S1", LocalDate.of(2004,1,2), "R001", new Project[]{p1,p2});
        Student s2 = new Student("S2", LocalDate.of(2004,2,3), "R002", new Project[]{p1,p3});
        Student s3 = new Student("S3", LocalDate.of(2004,3,4), "R003", new Project[]{p3,p4});
        Student s4 = new Student("S4", LocalDate.of(2004,4,5), "R004", new Project[]{p1,p4});

        // Problem
        Problem pb = new Problem();
        pb.addTeacher(t1); pb.addTeacher(t2);
        pb.addProject(p1); pb.addProject(p2); pb.addProject(p3); pb.addProject(p4);
        pb.addStudent(s1); pb.addStudent(s2); pb.addStudent(s3); pb.addStudent(s4);

        // Print sample objects
        System.out.println(t1); System.out.println(t2);
        System.out.println(p1); System.out.println(p2); System.out.println(p3); System.out.println(p4);
        System.out.println(s1); System.out.println(s2); System.out.println(s3); System.out.println(s4);

        // Persons array
        Person[] persons = pb.getAllPersons();
        System.out.print("Persons: "); for(Person x: persons) System.out.print(x.getName()+" "); System.out.println();

        // Greedy allocation
        AllocationGreedy.allocate(pb);
        for(Project p : pb.getProjects()) System.out.println(p);

        // Optional Hall check (small n)
        boolean hall = HallChecker.hasPerfectAllocation(pb.getStudents(), pb.getProjects());
        System.out.println("Hall condition (perfect allocation possible): "+hall);
    }
}
