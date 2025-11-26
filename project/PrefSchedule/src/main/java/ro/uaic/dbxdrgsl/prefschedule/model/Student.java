package ro.uaic.dbxdrgsl.prefschedule.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "students")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class Student extends Person {
    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "student_year")
    private int year;

    @Builder
    public Student(Long id, String name, String email, String code, int year) {
        super(id, name, email);
        this.code = code;
        this.year = year;
    }
}
