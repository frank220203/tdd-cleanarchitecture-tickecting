package frankproject.tdd_cleanarchitecture_ticketing.domain.service.payment;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Payment.PaymentOutbox;
import frankproject.tdd_cleanarchitecture_ticketing.domain.repository.payment.PaymentOutboxRepository;
import frankproject.tdd_cleanarchitecture_ticketing.infrastructure.repository.PaymentOutboxRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class PaymentOutboxWriter {

    private final PaymentOutboxRepository paymentOutboxRepository;

    public PaymentOutboxWriter(PaymentOutboxRepositoryImpl paymentOutboxRepository) {
        this.paymentOutboxRepository = paymentOutboxRepository;
    }

    public Optional<PaymentOutbox> findById(long outboxId) {
        return paymentOutboxRepository.findById(outboxId);
    }

    public PaymentOutbox save(PaymentOutbox outbox) {
        return paymentOutboxRepository.save(outbox);
    }

    @Transactional
    public void complete(long paymentId) {
        List<PaymentOutbox> targetList = paymentOutboxRepository.findByPaymentId(paymentId);
        for(PaymentOutbox paymentOutbox : targetList) {
            PaymentOutbox newOutbox = new PaymentOutbox(paymentOutbox.getOutboxId(), paymentOutbox.getMessage(), "PUBLISHED", paymentOutbox.getPaymentId());
            log.info("target: {}", paymentOutbox);
            paymentOutboxRepository.save(newOutbox);
        }
    }
}
