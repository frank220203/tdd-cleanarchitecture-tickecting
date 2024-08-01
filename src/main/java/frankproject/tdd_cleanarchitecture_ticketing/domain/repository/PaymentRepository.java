package frankproject.tdd_cleanarchitecture_ticketing.domain.repository;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Payment;

import java.util.Optional;

public interface PaymentRepository {

    Optional<Payment> findById(long paymentId);

    Payment save(Payment payment);

}
