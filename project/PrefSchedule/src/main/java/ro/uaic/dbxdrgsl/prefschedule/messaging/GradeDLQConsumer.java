package ro.uaic.dbxdrgsl.prefschedule.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Dead-Letter Queue consumer for monitoring and logging failed grade events.
 * Messages end up in the DLQ after exhausting all retry attempts.
 */
@Component
@Slf4j
public class GradeDLQConsumer {
    
    @RabbitListener(queues = RabbitMQConfig.GRADE_DLQ)
    public void handleDeadLetter(GradeEvent gradeEvent, Message message) {
        log.error("╔════════════════════════════════════════╗");
        log.error("║   DEAD-LETTER QUEUE - FAILED MESSAGE  ║");
        log.error("╚════════════════════════════════════════╝");
        log.error("Grade event sent to DLQ after max retries:");
        log.error("  Student Code: {}", gradeEvent.getStudentCode());
        log.error("  Course Code:  {}", gradeEvent.getCourseCode());
        log.error("  Grade:        {}", gradeEvent.getGrade());
        
        // Extract failure information from message headers
        if (message.getMessageProperties() != null) {
            String reason = (String) message.getMessageProperties().getHeaders().get("x-exception-message");
            Integer retryCount = (Integer) message.getMessageProperties().getHeaders().get("x-death-count");
            
            log.error("  Failure Reason: {}", reason != null ? reason : "Unknown");
            log.error("  Retry Count:    {}", retryCount != null ? retryCount : "Unknown");
        }
        
        log.error("╔════════════════════════════════════════╗");
        log.error("║   ACTION REQUIRED: MANUAL REVIEW       ║");
        log.error("╚════════════════════════════════════════╝");
        
        // Console output for visibility
        System.err.println("\n╔════════════════════════════════════════╗");
        System.err.println("║ ⚠️  DEAD-LETTER QUEUE - FAILED MESSAGE ⚠️  ║");
        System.err.println("╚════════════════════════════════════════╝");
        System.err.println("Grade event FAILED after max retries:");
        System.err.println("  Student: " + gradeEvent.getStudentCode());
        System.err.println("  Course:  " + gradeEvent.getCourseCode());
        System.err.println("  Grade:   " + gradeEvent.getGrade());
        System.err.println("\n⚠️  MANUAL REVIEW REQUIRED ⚠️");
        System.err.println("This message has been moved to the Dead-Letter Queue.");
        System.err.println("Possible reasons:");
        System.err.println("  - Student not found in database");
        System.err.println("  - Course not found in database");
        System.err.println("  - Course is not COMPULSORY");
        System.err.println("  - Database connection issue");
        System.err.println("╚════════════════════════════════════════╝\n");
        
        // In a production system, you might want to:
        // 1. Store failed messages in a separate database table
        // 2. Send alerts to administrators
        // 3. Trigger a workflow for manual intervention
        // 4. Log to external monitoring systems (e.g., Sentry, DataDog)
    }
}
