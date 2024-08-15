package frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_outbox")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbox_id")
    private long outboxId;

    @Column(name = "message")
    private String message;

    @Column(name = "status")
    private String status;

    @Column(name = "payment_id")
    private long paymentId;

    public PaymentOutbox(String message, String status, long paymentId) {
        this.message = message;
        this.status = status;
        this.paymentId = paymentId;
    }
}
