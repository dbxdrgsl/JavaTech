package ro.uaic.dbxdrgsl.quickgrade.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.uaic.dbxdrgsl.quickgrade.dto.GradeEvent;
import ro.uaic.dbxdrgsl.quickgrade.dto.GradeStatistics;

import java.util.DoubleSummaryStatistics;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for tracking grade statistics
 */
@Service
@Slf4j
public class GradeStatisticsService {
    
    private final AtomicLong totalGrades = new AtomicLong(0);
    private final CopyOnWriteArrayList<Double> grades = new CopyOnWriteArrayList<>();
    private volatile String lastStudentCode;
    private volatile String lastCourseCode;
    
    /**
     * Record a grade that was published
     */
    public void recordGrade(GradeEvent event) {
        totalGrades.incrementAndGet();
        grades.add(event.getGrade());
        lastStudentCode = event.getStudentCode();
        lastCourseCode = event.getCourseCode();
        log.info("Recorded grade: {} for student {} in course {}", 
                event.getGrade(), event.getStudentCode(), event.getCourseCode());
    }
    
    /**
     * Get current grade statistics
     */
    public GradeStatistics getStatistics() {
        if (grades.isEmpty()) {
            return GradeStatistics.builder()
                    .totalGradesPublished(0)
                    .averageGrade(0.0)
                    .minGrade(0.0)
                    .maxGrade(0.0)
                    .lastPublishedStudent("N/A")
                    .lastPublishedCourse("N/A")
                    .build();
        }
        
        // Use DoubleSummaryStatistics for efficient single-pass calculation
        DoubleSummaryStatistics stats = grades.stream()
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();
        
        double average = Math.round(stats.getAverage() * 100.0) / 100.0;
        
        return GradeStatistics.builder()
                .totalGradesPublished(totalGrades.get())
                .averageGrade(average)
                .minGrade(stats.getMin())
                .maxGrade(stats.getMax())
                .lastPublishedStudent(lastStudentCode != null ? lastStudentCode : "N/A")
                .lastPublishedCourse(lastCourseCode != null ? lastCourseCode : "N/A")
                .build();
    }
    
    /**
     * Reset statistics (for testing purposes)
     */
    public void reset() {
        totalGrades.set(0);
        grades.clear();
        lastStudentCode = null;
        lastCourseCode = null;
        log.info("Statistics reset");
    }
}
