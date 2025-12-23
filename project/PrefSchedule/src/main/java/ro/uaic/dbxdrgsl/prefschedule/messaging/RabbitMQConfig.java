package ro.uaic.dbxdrgsl.prefschedule.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {
    
    public static final String GRADE_QUEUE = "grade.queue";
    public static final String GRADE_DLQ = "grade.dlq";
    public static final String GRADE_DLX = "grade.dlx";
    public static final String GRADE_DLQ_ROUTING_KEY = "grade.dlq.routing.key";
    
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long INITIAL_RETRY_INTERVAL = 2000L; // 2 seconds
    private static final double RETRY_MULTIPLIER = 2.0; // Exponential backoff
    private static final long MAX_RETRY_INTERVAL = 10000L; // 10 seconds
    
    /**
     * Main grade queue with DLX configuration
     */
    @Bean
    public Queue gradeQueue() {
        Map<String, Object> args = new HashMap<>();
        // Configure Dead-Letter Exchange for failed messages
        args.put("x-dead-letter-exchange", GRADE_DLX);
        args.put("x-dead-letter-routing-key", GRADE_DLQ_ROUTING_KEY);
        return new Queue(GRADE_QUEUE, true, false, false, args);
    }
    
    /**
     * Dead-Letter Queue for failed messages
     */
    @Bean
    public Queue gradeDLQ() {
        return new Queue(GRADE_DLQ, true);
    }
    
    /**
     * Dead-Letter Exchange
     */
    @Bean
    public DirectExchange gradeDLX() {
        return new DirectExchange(GRADE_DLX);
    }
    
    /**
     * Binding DLQ to DLX
     */
    @Bean
    public Binding gradeDLQBinding() {
        return BindingBuilder.bind(gradeDLQ()).to(gradeDLX()).with(GRADE_DLQ_ROUTING_KEY);
    }
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
    
    /**
     * Message recoverer: sends failed messages to DLQ after max retries
     */
    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, GRADE_DLX, GRADE_DLQ_ROUTING_KEY);
    }
    
    /**
     * Listener container factory with message converter
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setDefaultRequeueRejected(false); // Don't requeue, send to DLQ instead
        return factory;
    }
}
