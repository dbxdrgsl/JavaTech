package ro.uaic.dbxdrgsl.prefschedule;

import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ro.uaic.dbxdrgsl.prefschedule.model.Student;
import ro.uaic.dbxdrgsl.prefschedule.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final Faker faker = new Faker();

    public DataLoader(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- DataLoader starting ---");

        // Generate sample students using Faker (if none exist)
        if (studentRepository.count() == 0) {
            List<Student> students = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                Student s = Student.builder()
                        .code(String.format("S2025-%03d", i))
                        .name(faker.name().fullName())
                        .email(faker.internet().emailAddress())
                        .year(faker.number().numberBetween(1, 4))
                        .build();
                students.add(s);
            }
            studentRepository.saveAll(students);
            System.out.println("Saved " + students.size() + " sample students using Faker");
        }

        System.out.println("All students in database:");
        studentRepository.findAll().forEach(System.out::println);
        System.out.println("--- DataLoader finished ---");
    }
}
