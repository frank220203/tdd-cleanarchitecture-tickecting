package frankproject.tdd_cleanarchitecture_ticketing.domain.repository.payment;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Payment.Payment;

import java.util.Optional;

public interface PaymentRepository {

    Optional<Payment> findById(long paymentId);

    Payment save(Payment payment);

}
