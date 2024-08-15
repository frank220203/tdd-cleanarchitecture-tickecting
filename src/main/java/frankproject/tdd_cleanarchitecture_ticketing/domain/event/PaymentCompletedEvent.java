package frankproject.tdd_cleanarchitecture_ticketing.domain.event;

import frankproject.tdd_cleanarchitecture_ticketing.application.dto.PaymentDTO;
import lombok.Getter;

@Getter
public class PaymentCompletedEvent {

    private final PaymentDTO paymentDTO;

    public PaymentCompletedEvent(PaymentDTO paymentDTO) {
        this.paymentDTO = paymentDTO;
    }

}
