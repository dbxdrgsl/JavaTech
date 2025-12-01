package ro.uaic.dbxdrgsl.quickgrade.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uaic.dbxdrgsl.quickgrade.dto.GradeEvent;
import ro.uaic.dbxdrgsl.quickgrade.service.GradePublisher;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {
    
    private final GradePublisher gradePublisher;
    
    @PostMapping("/publish")
    public ResponseEntity<String> publishGrade(@RequestBody GradeEvent gradeEvent) {
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
}
