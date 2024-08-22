package frankproject.tdd_cleanarchitecture_ticketing.adapter.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Payment.PaymentOutbox;
import frankproject.tdd_cleanarchitecture_ticketing.domain.event.PaymentCompletedEvent;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.payment.PaymentMessagePublisher;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.payment.PaymentOutboxWriter;
import frankproject.tdd_cleanarchitecture_ticketing.infrastructure.kafka.PaymentKafkaMessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class PaymentEventListener {

    private final PaymentOutboxWriter paymentOutboxWriter;

    private final PaymentMessagePublisher paymentKafkaMessageProducer;

    public PaymentEventListener(PaymentOutboxWriter paymentOutboxWriter, PaymentKafkaMessageProducer paymentKafkaMessageProducer) {
        this.paymentOutboxWriter = paymentOutboxWriter;
        this.paymentKafkaMessageProducer = paymentKafkaMessageProducer;
    }
    @Async
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void insertOutbox(PaymentCompletedEvent event) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try{
            String message = objectMapper.writeValueAsString(event);
            log.info("결제 완료 전 이벤트 처리를 시작합니다. event: {}", message);
            PaymentOutbox outbox = paymentOutboxWriter.save(new PaymentOutbox(message, "INIT", event.getPaymentDTO().getPaymentId()));
            String result = objectMapper.writeValueAsString(outbox);
            log.info("아웃박스 저장 완료. OutBox: {}", result);
        } catch(Exception e) {
            log.error("Outbox 저장 실패: {}", e.getMessage());
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publicMessage(PaymentCompletedEvent event) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try{
            String message = objectMapper.writeValueAsString(event);
            log.info("결제 완료 후 이벤트 처리를 시작합니다. event: {}", message);
            paymentKafkaMessageProducer.sendMessage(message);
        } catch(Exception e) {
            log.error("Kafka 저장 실패: {}", e.getMessage());
        }
    }
}
