package frankproject.tdd_cleanarchitecture_ticketing.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private long reservationId;

    @Column(name = "customer_id")
    private long customerId;

    @Column(name = "seat_id")
    private long seatId;

    @Column(name = "concert_schedule_id")
    private long concertScheduleId;

    @Column(name = "reservation_time")
    private LocalDateTime reservationTime = LocalDateTime.now();

    @Column(name = "status")
    private String status = "PENDING";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Reservation(long customerId, long seatId, long concertScheduleId) {
        this.customerId = customerId;
        this.seatId = seatId;
        this.concertScheduleId = concertScheduleId;
    }

    // 예약 취소
    public void cancel() {
        this.status = "CANCELLED";
    }

    // 결제 완료
    public void completed() {
        this.status = "COMPLETED";
    }

}
