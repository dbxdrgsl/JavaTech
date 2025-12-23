package ro.uaic.dbxdrgsl.prefschedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for grade statistics from QuickGrade service
 * Section 8. Compulsory: DTO for REST client communication
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeStatisticsDTO {
    private long totalGradesPublished;
    private double averageGrade;
    private double minGrade;
    private double maxGrade;
    private String lastPublishedStudent;
    private String lastPublishedCourse;
}
