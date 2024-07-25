package frankproject.tdd_cleanarchitecture_ticketing.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Long reservationId;
    private Long customerId;
    private Long seatId;
    private Long concertScheduleId;
    private LocalDateTime reservationTime;
    private String status;
}
