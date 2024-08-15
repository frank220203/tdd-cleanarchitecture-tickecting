package frankproject.tdd_cleanarchitecture_ticketing.application.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import frankproject.tdd_cleanarchitecture_ticketing.application.dto.PaymentDTO;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Payment.Payment;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Payment.PaymentOutbox;
import frankproject.tdd_cleanarchitecture_ticketing.domain.event.PaymentCompletedEvent;
import frankproject.tdd_cleanarchitecture_ticketing.domain.repository.payment.PaymentOutboxRepository;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.payment.PaymentMessagePublisher;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.payment.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@EnableScheduling
public class KafkaSchedule {

    @Autowired
    private PaymentOutboxRepository paymentOutboxRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentMessagePublisher paymentKafkaMessageProducer;

    @Scheduled(fixedRate = 30000)
    public void reProduceKafka() {
        log.info("Kafka 발행 재수행 이벤트 처리를 시작합니다.");
        List<PaymentOutbox> outboxList = paymentOutboxRepository.findAllByStatus("INIT");
        int num = 0;
        for(PaymentOutbox paymentOutbox : outboxList) {
            log.info("시도 횟수 : {}", ++num);
            Payment payment = paymentService.findById(paymentOutbox.getPaymentId());
            PaymentDTO paymentDTO = new PaymentDTO(
                    payment.getPaymentId(),
                    payment.getCustomerId(),
                    payment.getReservationId(),
                    payment.getAmount(),
                    payment.getPaymentTime(),
                    payment.getCreatedAt(),
                    payment.getUpdatedAt()
            );
            PaymentCompletedEvent event = new PaymentCompletedEvent(paymentDTO);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            try{
                String message = objectMapper.writeValueAsString(event);
                log.info("Kafka 재발행 event: {}", message);
                paymentKafkaMessageProducer.sendMessage(message);
            } catch(Exception e) {
                log.error("Kafka 저장 실패: {}", e.getMessage());
            }
        }
    }
}
