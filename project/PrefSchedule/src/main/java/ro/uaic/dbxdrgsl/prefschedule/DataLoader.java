package ro.uaic.dbxdrgsl.prefschedule;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ro.uaic.dbxdrgsl.prefschedule.model.Student;
import ro.uaic.dbxdrgsl.prefschedule.repository.StudentRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private static final String DEFAULT_STUDENT_CODE = "S2025-001";
    private static final int DEFAULT_STUDENT_YEAR = 2;

    private final StudentRepository studentRepository;

    public DataLoader(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- DataLoader starting ---");

        // Create a sample student (if none exists)
        if (studentRepository.count() == 0) {
            Student s = Student.builder()
                    .code(DEFAULT_STUDENT_CODE)
                    .name("Ion Popescu")
                    .email("ion.popescu@example.com")
                    .year(DEFAULT_STUDENT_YEAR)
                    .build();
            s = studentRepository.save(s);
            System.out.println("Saved sample student: " + s);
        }

        System.out.println("All students in database:");
        studentRepository.findAll().forEach(System.out::println);
        System.out.println("--- DataLoader finished ---");
    }
}
