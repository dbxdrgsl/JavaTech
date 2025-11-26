package ro.uaic.dbxdrgsl.prefschedule.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @Email(message = "Email should be valid")
    private String email;
}
