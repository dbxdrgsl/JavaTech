package ro.uaic.dbxdrgsl.quickgrade.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ro.uaic.dbxdrgsl.quickgrade.config.RabbitMQConfig;
import ro.uaic.dbxdrgsl.quickgrade.dto.GradeEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradePublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    public void publishGrade(GradeEvent gradeEvent) {
        log.info("Publishing grade event: {}", gradeEvent);
        rabbitTemplate.convertAndSend(RabbitMQConfig.GRADE_QUEUE, gradeEvent);
        log.info("Grade event published successfully");
    }
}
