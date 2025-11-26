package ro.uaic.dbxdrgsl.prefschedule.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "instructors")
@Data
@EqualsAndHashCode(callSuper = true, exclude = "courses")
@ToString(callSuper = true, exclude = "courses")
@NoArgsConstructor
public class Instructor extends Person {
    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Course> courses = new ArrayList<>();

    @Builder
    public Instructor(Long id, String name, String email, List<Course> courses) {
        super(id, name, email);
        this.courses = courses != null ? courses : new ArrayList<>();
    }

    public void addCourse(Course course) {
        courses.add(course);
        course.setInstructor(this);
    }

    public void removeCourse(Course course) {
        courses.remove(course);
        course.setInstructor(null);
    }
}
