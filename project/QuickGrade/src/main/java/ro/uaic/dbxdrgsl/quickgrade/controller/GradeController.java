package ro.uaic.dbxdrgsl.quickgrade.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uaic.dbxdrgsl.quickgrade.dto.GradeEvent;
import ro.uaic.dbxdrgsl.quickgrade.dto.GradeStatistics;
import ro.uaic.dbxdrgsl.quickgrade.service.GradePublisher;
import ro.uaic.dbxdrgsl.quickgrade.service.GradeStatisticsService;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {
    
    private final GradePublisher gradePublisher;
    private final GradeStatisticsService statisticsService;
    
    @PostMapping("/publish")
    public ResponseEntity<String> publishGrade(@Valid @RequestBody GradeEvent gradeEvent) {
        gradePublisher.publishGrade(gradeEvent);
        return ResponseEntity.ok("Grade event published successfully");
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> testPublish() {
        // Publish sample grade events for testing
        gradePublisher.publishGrade(new GradeEvent("S001", "MATH101", 9.5));
        gradePublisher.publishGrade(new GradeEvent("S002", "CS101", 8.7));
        gradePublisher.publishGrade(new GradeEvent("S003", "PHY101", 7.9));
        return ResponseEntity.ok("3 sample grade events published successfully");
    }
    
    /**
     * Get grade statistics (for REST client communication from PrefSchedule)
     * Section 8. Compulsory: REST endpoint for microservice communication
     */
    @GetMapping("/statistics")
    public ResponseEntity<GradeStatistics> getStatistics() {
        GradeStatistics stats = statisticsService.getStatistics();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Reset statistics (for testing purposes)
     */
    @DeleteMapping("/statistics")
    public ResponseEntity<String> resetStatistics() {
        statisticsService.reset();
        return ResponseEntity.ok("Statistics reset successfully");
    }
}
