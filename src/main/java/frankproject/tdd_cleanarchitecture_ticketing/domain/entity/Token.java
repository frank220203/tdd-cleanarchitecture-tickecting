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

    // 만료 여부 확인 메소드
    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        // 상태가 PENDING일 때는 만료시키지 않음
        if ("PENDING".equals(status)) {
            return false;
        }
        // 상태가 ACTIVE이고 업데이트된 시간으로부터 20분 이상 경과하면 // 테스트를 위해 2초로 변경
        if ("ACTIVE".equals(status) && updatedAt != null && updatedAt.plusSeconds(2).isBefore(now)) {
//        if ("ACTIVE".equals(status) && updatedAt != null && updatedAt.plusMinutes(20).isBefore(now)) {
            return true;
        }
        return false;
    }

    public boolean isActive(){

        LocalDateTime now = LocalDateTime.now();
        return "ACTIVE".equals(status);
    }

    // 상태 변경 메소드
    public void markAsExpired() {
        if ("ACTIVE".equals(status)) {
            this.status = "EXPIRED";
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void markAsActive() {
        if ("PENDING".equals(status)) {
            this.status = "ACTIVE";
            this.updatedAt = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "Token{" +
                "tokenId=" + tokenId +
                ", concertId=" + concertId +
                ", customerId=" + customerId +
                ", waitNumber=" + waitNumber +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
