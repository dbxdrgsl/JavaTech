package ro.uaic.dbxdrgsl.prefschedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.uaic.dbxdrgsl.prefschedule.dto.GradeStatisticsDTO;
import ro.uaic.dbxdrgsl.prefschedule.service.QuickGradeClientService;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for microservice communication
 * Section 8. Compulsory: REST endpoint that calls QuickGrade microservice
 */
@RestController
@RequestMapping("/api/microservices")
@RequiredArgsConstructor
@Tag(name = "Microservice Communication", description = "Endpoints for inter-service REST communication")
public class MicroserviceController {
    
    private final QuickGradeClientService quickGradeClientService;
    
    /**
     * Get grade statistics from QuickGrade microservice
     * Section 8. Compulsory: Demonstrates synchronous REST communication between microservices
     */
    @Operation(summary = "Get grade statistics from QuickGrade", 
               description = "Fetches grade statistics from QuickGrade microservice via REST client")
    @GetMapping("/quickgrade/statistics")
    public ResponseEntity<?> getQuickGradeStatistics() {
        GradeStatisticsDTO statistics = quickGradeClientService.getGradeStatistics();
        
        if (statistics == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "QuickGrade service is unavailable");
            error.put("message", "Unable to fetch statistics from QuickGrade microservice");
            return ResponseEntity.status(503).body(error);
        }
        
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * Health check for QuickGrade service
     */
    @Operation(summary = "Check QuickGrade service availability",
               description = "Checks if QuickGrade microservice is available")
    @GetMapping("/quickgrade/health")
    public ResponseEntity<Map<String, Object>> checkQuickGradeHealth() {
        boolean isAvailable = quickGradeClientService.isQuickGradeAvailable();
        
        Map<String, Object> response = new HashMap<>();
        response.put("service", "QuickGrade");
        response.put("available", isAvailable);
        response.put("status", isAvailable ? "UP" : "DOWN");
        
        return ResponseEntity.ok(response);
    }
}
