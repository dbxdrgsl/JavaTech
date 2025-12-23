package ro.uaic.dbxdrgsl.prefschedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ro.uaic.dbxdrgsl.prefschedule.dto.GradeStatisticsDTO;

/**
 * Service for communicating with QuickGrade microservice via REST API
 * Section 8. Compulsory: REST client for synchronous microservice communication
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuickGradeClientService {
    
    private final RestTemplate restTemplate;
    
    @Value("${quickgrade.service.url:http://localhost:8081}")
    private String quickGradeBaseUrl;
    
    /**
     * Fetch grade statistics from QuickGrade service
     * @return GradeStatisticsDTO containing statistics or null if service is unavailable
     */
    public GradeStatisticsDTO getGradeStatistics() {
        String url = quickGradeBaseUrl + "/api/grades/statistics";
        
        try {
            log.info("Fetching grade statistics from QuickGrade: {}", url);
            GradeStatisticsDTO statistics = restTemplate.getForObject(url, GradeStatisticsDTO.class);
            log.info("Successfully retrieved grade statistics: {}", statistics);
            return statistics;
        } catch (RestClientException e) {
            log.error("Failed to fetch grade statistics from QuickGrade: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Check if QuickGrade service is available
     * Lightweight health check that only checks HTTP status
     * @return true if service responds, false otherwise
     */
    public boolean isQuickGradeAvailable() {
        String url = quickGradeBaseUrl + "/api/grades/statistics";
        
        try {
            ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);
            boolean isAvailable = response.getStatusCode() == HttpStatus.OK;
            log.debug("QuickGrade service health check: {}", isAvailable ? "UP" : "DOWN");
            return isAvailable;
        } catch (Exception e) {
            log.warn("QuickGrade service is not available: {}", e.getMessage());
            return false;
        }
    }
}
