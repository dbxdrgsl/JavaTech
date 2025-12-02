package ro.uaic.dbxdrgsl.quickgrade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for grade statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeStatistics {
    private long totalGradesPublished;
    private double averageGrade;
    private double minGrade;
    private double maxGrade;
    private String lastPublishedStudent;
    private String lastPublishedCourse;
}
