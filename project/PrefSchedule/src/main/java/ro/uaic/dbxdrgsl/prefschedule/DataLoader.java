package ro.uaic.dbxdrgsl.prefschedule;

import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ro.uaic.dbxdrgsl.prefschedule.model.*;
import ro.uaic.dbxdrgsl.prefschedule.service.*;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private static final String DEFAULT_STUDENT_CODE = "S2025-001";
    private static final int DEFAULT_STUDENT_YEAR = 2;

    private final StudentService studentService;
    private final InstructorService instructorService;
    private final PackService packService;
    private final CourseService courseService;
    private final Faker faker;

    public DataLoader(StudentService studentService, InstructorService instructorService,
                      PackService packService, CourseService courseService) {
        this.studentService = studentService;
        this.instructorService = instructorService;
        this.packService = packService;
        this.courseService = courseService;
        this.faker = new Faker();
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- DataLoader starting ---");

        // Create sample students if none exist
        if (studentService.count() == 0) {
            System.out.println("\n=== Creating Students ===");
            for (int i = 1; i <= 5; i++) {
                Student student = Student.builder()
                        .code("S2025-" + String.format("%03d", i))
                        .name(faker.name().fullName())
                        .email(faker.internet().emailAddress())
                        .year(faker.number().numberBetween(1, 4))
                        .build();
                student.setId(null);
                student = studentService.save(student);
                System.out.println("Created student: " + student.getName() + " (Year " + student.getYear() + ")");
            }
        }

        // Create sample instructors
        if (instructorService.findAll().isEmpty()) {
            System.out.println("\n=== Creating Instructors ===");
            for (int i = 1; i <= 3; i++) {
                Instructor instructor = Instructor.builder()
                        .name(faker.name().fullName())
                        .email(faker.internet().emailAddress())
                        .build();
                instructor.setId(null);
                instructor = instructorService.save(instructor);
                System.out.println("Created instructor: " + instructor.getName());
            }
        }

        // Create sample packs
        if (packService.findAll().isEmpty()) {
            System.out.println("\n=== Creating Packs ===");
            Pack pack1 = Pack.builder()
                    .year(2)
                    .semester(1)
                    .name("Advanced Programming Pack")
                    .build();
            pack1.setId(null);
            pack1 = packService.save(pack1);
            System.out.println("Created pack: " + pack1.getName());

            Pack pack2 = Pack.builder()
                    .year(2)
                    .semester(2)
                    .name("Database Systems Pack")
                    .build();
            pack2.setId(null);
            pack2 = packService.save(pack2);
            System.out.println("Created pack: " + pack2.getName());
        }

        // CRUD operations for courses using JavaFaker
        System.out.println("\n=== CRUD Operations for Courses ===");
        
        List<Instructor> instructors = instructorService.findAll();
        List<Pack> packs = packService.findAll();

        if (!instructors.isEmpty() && !packs.isEmpty()) {
            Instructor instructor = instructors.get(0);
            Pack pack = packs.get(0);

            // CREATE
            System.out.println("\n1. CREATE - Creating new courses");
            Course course1 = Course.builder()
                    .type("COMPULSORY")
                    .code("CS101")
                    .abbr("OOP")
                    .name("Object-Oriented Programming")
                    .instructor(instructor)
                    .groupCount(3)
                    .description(faker.lorem().sentence(10))
                    .build();
            course1.setId(null);
            course1 = courseService.save(course1);
            System.out.println("Created course: " + course1.getName() + " (ID: " + course1.getId() + ")");

            Course course2 = Course.builder()
                    .type("OPTIONAL")
                    .code("CS201")
                    .abbr("AI")
                    .name("Artificial Intelligence")
                    .instructor(instructor)
                    .pack(pack)
                    .groupCount(2)
                    .description(faker.lorem().sentence(10))
                    .build();
            course2.setId(null);
            course2 = courseService.save(course2);
            System.out.println("Created course: " + course2.getName() + " (ID: " + course2.getId() + ")");

            // READ
            System.out.println("\n2. READ - Retrieving courses");
            List<Course> allCourses = courseService.findAll();
            System.out.println("Total courses: " + allCourses.size());
            
            List<Course> compulsoryCourses = courseService.findByType("COMPULSORY");
            System.out.println("Compulsory courses: " + compulsoryCourses.size());
            
            List<Course> optionalCourses = courseService.findByType("OPTIONAL");
            System.out.println("Optional courses: " + optionalCourses.size());

            // UPDATE
            System.out.println("\n3. UPDATE - Updating course description");
            String newDescription = "Updated: " + faker.lorem().sentence(15);
            int updated = courseService.updateDescription(course1.getId(), newDescription);
            System.out.println("Updated " + updated + " course(s)");
            
            Course updatedCourse = courseService.findById(course1.getId()).orElse(null);
            if (updatedCourse != null) {
                System.out.println("New description: " + updatedCourse.getDescription().substring(0, Math.min(50, updatedCourse.getDescription().length())) + "...");
            }

            // DELETE (demonstrating but not actually deleting to keep data)
            System.out.println("\n4. DELETE - Demonstrating delete capability");
            long beforeCount = courseService.count();
            System.out.println("Courses before delete: " + beforeCount);
            // courseService.deleteById(course2.getId());
            System.out.println("(Delete commented out to preserve data for testing)");
        }

        // Test derived queries
        System.out.println("\n=== Testing Derived Queries ===");
        List<Student> year2Students = studentService.findByYear(2);
        System.out.println("Students in year 2: " + year2Students.size());

        // Test JPQL queries
        System.out.println("\n=== Testing JPQL Queries ===");
        List<Instructor> instructorsWithCourses = instructorService.findInstructorsWithCourses();
        System.out.println("Instructors with courses: " + instructorsWithCourses.size());

        System.out.println("\n=== Summary ===");
        System.out.println("Total students: " + studentService.count());
        System.out.println("Total instructors: " + instructorService.findAll().size());
        System.out.println("Total packs: " + packService.findAll().size());
        System.out.println("Total courses: " + courseService.count());

        System.out.println("\n--- DataLoader finished ---");
    }
}
