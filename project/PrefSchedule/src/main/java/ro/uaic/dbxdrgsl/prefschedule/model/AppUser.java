package ro.uaic.dbxdrgsl.prefschedule.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * AppUser entity representing application users with authentication capabilities.
 * This serves as a base for Student, Instructor, and Admin users.
 */
@Entity
@Table(name = "app_users")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AppUser extends Person {
    
    @NotBlank(message = "Username is required")
    @Column(nullable = false, unique = true)
    private String username;
    
    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @Builder.Default
    @Column(nullable = false)
    private boolean enabled = true;
    
    public enum Role {
        STUDENT, INSTRUCTOR, ADMIN
    }
}
