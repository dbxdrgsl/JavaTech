package ro.uaic.dbxdrgsl.prefschedule.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"instructor", "pack"})
@ToString(exclude = {"instructor", "pack"})
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // COMPULSORY or OPTIONAL

    @Column(nullable = false)
    private String code;

    private String abbr;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pack_id")
    private Pack pack;

    @Column(name = "group_count")
    private Integer groupCount;

    @Column(columnDefinition = "TEXT")
    private String description;
}
