package frankproject.tdd_cleanarchitecture_ticketing.infrastructure.kafka;

import frankproject.tdd_cleanarchitecture_ticketing.domain.service.payment.PaymentMessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentKafkaMessageProducer implements PaymentMessagePublisher {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "payment-topic";

    public void sendMessage(String message) {
        kafkaTemplate.send(TOPIC, message);
    }
}
