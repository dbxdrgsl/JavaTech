package ro.uaic.dbxdrgsl.prefschedule.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a student's preference for a course.
 * Students rank courses within each pack according to their preferences.
 * The rankOrder field represents the preference ranking (1 = most preferred).
 * Ties are allowed (multiple courses can have the same rankOrder).
 */
@Entity
@Table(name = "student_preferences", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"student", "course"})
@ToString(exclude = {"student", "course"})
public class StudentPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /**
     * The rank order representing preference (1 = most preferred).
     * Ties are allowed - multiple courses can share the same rank.
     */
    @Column(name = "rank_order", nullable = false)
    private Integer rankOrder;

    /**
     * Version field for optimistic locking and ETag support.
     */
    @Version
    private Long version;
}
