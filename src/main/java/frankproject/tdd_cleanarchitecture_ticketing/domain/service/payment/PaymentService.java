package frankproject.tdd_cleanarchitecture_ticketing.domain.service.payment;

import frankproject.tdd_cleanarchitecture_ticketing.domain.common.CoreException;
import frankproject.tdd_cleanarchitecture_ticketing.domain.common.ErrorCode;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Payment.Payment;
import frankproject.tdd_cleanarchitecture_ticketing.domain.repository.payment.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment findById(long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CoreException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }
}
