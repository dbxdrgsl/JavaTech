package ro.uaic.dbxdrgsl.prefschedule.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO for creating or updating a student preference.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentPreferenceDTO {
    private Long id;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Rank order is required")
    @Min(value = 1, message = "Rank order must be at least 1")
    private Integer rankOrder;

    private Long version;
}
