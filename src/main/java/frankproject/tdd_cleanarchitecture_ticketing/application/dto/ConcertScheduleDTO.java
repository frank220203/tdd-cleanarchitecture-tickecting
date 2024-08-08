package frankproject.tdd_cleanarchitecture_ticketing.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConcertScheduleDTO {
    private Long concertScheduleId;
    private Long seatCount;
    private LocalDate concertDate;
}
