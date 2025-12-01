package ro.uaic.dbxdrgsl.prefschedule.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import ro.uaic.dbxdrgsl.prefschedule.service.GradeService;

@Component
@RequiredArgsConstructor
@Slf4j
public class GradeConsumer {
    
    private final GradeService gradeService;
    
    /**
     * Consumes grade events from the main grade queue.
     * Implements retry mechanism with exponential backoff.
     * After max retries, failed messages are sent to Dead-Letter Queue.
     */
    @RabbitListener(queues = RabbitMQConfig.GRADE_QUEUE)
    @Retryable(
        retryFor = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2, maxDelay = 10000)
    )
    public void consumeGrade(GradeEvent gradeEvent) {
        log.info("========================================");
        log.info("Received grade event from QuickGrade:");
        log.info("  Student Code: {}", gradeEvent.getStudentCode());
        log.info("  Course Code:  {}", gradeEvent.getCourseCode());
        log.info("  Grade:        {}", gradeEvent.getGrade());
        log.info("========================================");
        
        try {
            // Process grade event with business logic validation
            gradeService.processGradeEvent(gradeEvent);
            
            // Success message
            log.info("✓ Grade successfully processed and stored in database");
            System.out.println("========================================");
            System.out.println("✓ GRADE PROCESSED SUCCESSFULLY:");
            System.out.println("  Student: " + gradeEvent.getStudentCode());
            System.out.println("  Course:  " + gradeEvent.getCourseCode());
            System.out.println("  Grade:   " + gradeEvent.getGrade());
            System.out.println("  Status:  Stored in database");
            System.out.println("========================================");
            
        } catch (IllegalArgumentException e) {
            // Business logic validation failed (student not found, course not compulsory, etc.)
            log.error("✗ Grade validation failed: {}", e.getMessage());
            System.err.println("========================================");
            System.err.println("✗ GRADE VALIDATION FAILED:");
            System.err.println("  Student: " + gradeEvent.getStudentCode());
            System.err.println("  Course:  " + gradeEvent.getCourseCode());
            System.err.println("  Error:   " + e.getMessage());
            System.err.println("  Action:  Message will be retried with exponential backoff");
            System.err.println("========================================");
            
            // Re-throw to trigger retry mechanism
            throw new RuntimeException("Grade validation failed: " + e.getMessage(), e);
            
        } catch (Exception e) {
            // Unexpected error
            log.error("✗ Unexpected error processing grade event: {}", gradeEvent, e);
            System.err.println("========================================");
            System.err.println("✗ UNEXPECTED ERROR:");
            System.err.println("  Student: " + gradeEvent.getStudentCode());
            System.err.println("  Course:  " + gradeEvent.getCourseCode());
            System.err.println("  Error:   " + e.getMessage());
            System.err.println("  Action:  Message will be retried, then sent to DLQ if still failing");
            System.err.println("========================================");
            
            // Re-throw to trigger retry/DLQ mechanism
            throw new RuntimeException("Error processing grade event", e);
        }
    }
}
