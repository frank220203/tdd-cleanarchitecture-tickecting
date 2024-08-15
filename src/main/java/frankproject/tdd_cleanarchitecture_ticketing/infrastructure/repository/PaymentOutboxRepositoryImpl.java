package frankproject.tdd_cleanarchitecture_ticketing.infrastructure.repository;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Payment.PaymentOutbox;
import frankproject.tdd_cleanarchitecture_ticketing.domain.repository.payment.PaymentOutboxRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PaymentOutboxRepositoryImpl implements PaymentOutboxRepository {

    private final PaymentOutboxJpaRepository paymentOutboxJpaRepository;

    public PaymentOutboxRepositoryImpl(PaymentOutboxJpaRepository paymentOutboxJpaRepository) {
        this.paymentOutboxJpaRepository = paymentOutboxJpaRepository;
    }

    @Override
    public Optional<PaymentOutbox> findById(long outboxId) {
        return paymentOutboxJpaRepository.findById(outboxId);
    }

    @Override
    public PaymentOutbox save(PaymentOutbox paymentOutbox) {
        return paymentOutboxJpaRepository.save(paymentOutbox);
    }

    @Override
    public List<PaymentOutbox> findByPaymentId(long paymentId) {
        return paymentOutboxJpaRepository.findByPaymentId(paymentId);
    }

    @Override
    public List<PaymentOutbox> findAllByStatus(String status) {
        return paymentOutboxJpaRepository.findAllByStatus(status);
    }
}
