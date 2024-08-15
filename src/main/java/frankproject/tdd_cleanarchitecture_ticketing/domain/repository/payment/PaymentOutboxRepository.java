package frankproject.tdd_cleanarchitecture_ticketing.domain.repository.payment;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Payment.PaymentOutbox;

import java.util.List;
import java.util.Optional;

public interface PaymentOutboxRepository {

    Optional<PaymentOutbox> findById(long outboxId);

    PaymentOutbox save(PaymentOutbox outbox);

    List<PaymentOutbox> findByPaymentId(long paymentId);

    List<PaymentOutbox> findAllByStatus(String status);
}
