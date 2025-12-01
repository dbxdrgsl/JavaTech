package ro.uaic.dbxdrgsl.prefschedule.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "students")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Student extends Person {
    @NotBlank(message = "Student code is required")
    @Column(nullable = false, unique = true)
    private String code;

    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 6, message = "Year must not exceed 6")
    @Column(name = "student_year")
    private int year;
}
