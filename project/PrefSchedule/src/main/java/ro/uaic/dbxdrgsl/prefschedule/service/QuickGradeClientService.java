package ro.uaic.dbxdrgsl.prefschedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
     * @return true if service responds, false otherwise
     */
    public boolean isQuickGradeAvailable() {
        try {
            getGradeStatistics();
            return true;
        } catch (Exception e) {
            log.warn("QuickGrade service is not available: {}", e.getMessage());
            return false;
        }
    }
}
