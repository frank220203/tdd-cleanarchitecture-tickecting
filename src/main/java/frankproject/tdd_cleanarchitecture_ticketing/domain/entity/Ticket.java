package frankproject.tdd_cleanarchitecture_ticketing.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long concertId;
    private int quantity;
    private boolean reserved;
    private boolean purchased;

    public Ticket(Long id, Long userId, Long concertId, int quantity, boolean reserved, boolean purchased) {
        this.id = id;
        this.userId = userId;
        this.concertId = concertId;
        this.quantity = quantity;
        this.reserved = reserved;
        this.purchased = purchased;
    }
}
