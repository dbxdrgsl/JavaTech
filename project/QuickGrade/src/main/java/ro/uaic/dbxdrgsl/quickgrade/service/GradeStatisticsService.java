package ro.uaic.dbxdrgsl.quickgrade.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.uaic.dbxdrgsl.quickgrade.dto.GradeEvent;
import ro.uaic.dbxdrgsl.quickgrade.dto.GradeStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for tracking grade statistics
 */
@Service
@Slf4j
public class GradeStatisticsService {
    
    private final AtomicLong totalGrades = new AtomicLong(0);
    private final List<Double> grades = new ArrayList<>();
    private String lastStudentCode;
    private String lastCourseCode;
    
    /**
     * Record a grade that was published
     */
    public synchronized void recordGrade(GradeEvent event) {
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
    public synchronized GradeStatistics getStatistics() {
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
        
        double average = grades.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        
        double min = grades.stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0.0);
        
        double max = grades.stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);
        
        return GradeStatistics.builder()
                .totalGradesPublished(totalGrades.get())
                .averageGrade(Math.round(average * 100.0) / 100.0)
                .minGrade(min)
                .maxGrade(max)
                .lastPublishedStudent(lastStudentCode != null ? lastStudentCode : "N/A")
                .lastPublishedCourse(lastCourseCode != null ? lastCourseCode : "N/A")
                .build();
    }
    
    /**
     * Reset statistics (for testing purposes)
     */
    public synchronized void reset() {
        totalGrades.set(0);
        grades.clear();
        lastStudentCode = null;
        lastCourseCode = null;
        log.info("Statistics reset");
    }
}
