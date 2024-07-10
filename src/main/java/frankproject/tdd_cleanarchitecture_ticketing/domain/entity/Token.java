package frankproject.tdd_cleanarchitecture_ticketing.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "token")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private long tokenId;

    @Column(name = "customer_id")
    private long customerId;

    @Column(name = "concert_id")
    private long concertId;

    @Column(name = "wait_number")
    private long waitNumber;

    @Column(name = "status")
    // PENDING(대기) ACTIVE(활성화) EXPIRED(만료)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Token(Long concertId, Long customerId, long waitNumber, String status, LocalDateTime createdAt, LocalDateTime passedAt) {
        this.customerId = customerId;
        this.concertId = concertId;
        this.waitNumber = waitNumber;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = passedAt;
    }
}
