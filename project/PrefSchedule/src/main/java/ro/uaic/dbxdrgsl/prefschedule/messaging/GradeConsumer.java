package ro.uaic.dbxdrgsl.prefschedule.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GradeConsumer {
    
    @RabbitListener(queues = RabbitMQConfig.GRADE_QUEUE)
    public void consumeGrade(GradeEvent gradeEvent) {
        log.info("========================================");
        log.info("Received grade event from QuickGrade:");
        log.info("  Student Code: {}", gradeEvent.getStudentCode());
        log.info("  Course Code:  {}", gradeEvent.getCourseCode());
        log.info("  Grade:        {}", gradeEvent.getGrade());
        log.info("========================================");
        
        // Print to console for visibility
        System.out.println("========================================");
        System.out.println("GRADE EVENT RECEIVED:");
        System.out.println("  Student: " + gradeEvent.getStudentCode());
        System.out.println("  Course:  " + gradeEvent.getCourseCode());
        System.out.println("  Grade:   " + gradeEvent.getGrade());
        System.out.println("========================================");
    }
}
