package frankproject.tdd_cleanarchitecture_ticketing.domain.entity;

import frankproject.tdd_cleanarchitecture_ticketing.domain.common.CoreException;
import frankproject.tdd_cleanarchitecture_ticketing.domain.common.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "seat")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private long seatId;

    @Column(name = "concert_schedule_id")
    private long concertScheduleId;

    @Column(name = "seat_number")
    private int seatNumber;

    @Column(name = "price")
    private long price;

    @Column(name = "finally_reserved", nullable = false)
    private boolean finallyReserved = false;

    @Column(name = "temp_assignee_id")
    private long tempAssigneeId = 0;

    @Column(name = "temp_assign_expires_at")
    private LocalDateTime tempAssignExpiresAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private int version;

    public Seat(long concertScheduleId, int seatNumber, long price, boolean finallyReserved,
                long tempAssigneeId, LocalDateTime tempAssignExpiresAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.concertScheduleId = concertScheduleId;
        this.seatNumber = seatNumber;
        this.price = price;
        this.finallyReserved = finallyReserved;
        this.tempAssigneeId = tempAssigneeId;
        this.tempAssignExpiresAt = tempAssignExpiresAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 좌석 예약
     *
     * @param customerId 예약할 고객의 ID
     */
    public void reserveSeat(long customerId) {
        if (isTempAssignmentValid()) {
            // 좌석이 이미 예약된 경우 예외를 발생
            throw new CoreException(ErrorCode.SEAT_ALREADY_RESERVED);
        }

        // 좌석을 예약 상태로 설정
        this.tempAssigneeId = customerId;
        this.tempAssignExpiresAt = LocalDateTime.now().plusMinutes(5); // 5분 임시 배정
    }

    /**
     * 좌석 예약 취소
     */
    public void cancel() {
        // 좌석 예약 취소
        this.tempAssigneeId = 0;
        this.tempAssignExpiresAt = null;
    }

    /**
     * 임시 배정이 유효한지 확인
     *
     * @return 임시 배정이 유효하면 true를 반환, 그렇지 않으면 false를 반환
     */
    public boolean isTempAssignmentValid() {
        return this.tempAssignExpiresAt != null && LocalDateTime.now().isBefore(this.tempAssignExpiresAt);
    }

    /**
     * 결제 완료
     */
    public void completed() {
        this.finallyReserved = true;
    }
}
