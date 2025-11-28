package ro.uaic.dbxdrgsl.prefschedule.dto;

import lombok.*;

/**
 * Response DTO for student preference with expanded details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentPreferenceResponseDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentCode;
    private Long courseId;
    private String courseName;
    private String courseCode;
    private String packName;
    private Integer rankOrder;
    private Long version;
}
