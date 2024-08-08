package frankproject.tdd_cleanarchitecture_ticketing.application.usecase;

import frankproject.tdd_cleanarchitecture_ticketing.application.dto.ConcertDTO;
import frankproject.tdd_cleanarchitecture_ticketing.application.dto.ConcertScheduleDTO;
import frankproject.tdd_cleanarchitecture_ticketing.application.dto.SeatDTO;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Concert;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.ConcertSchedule;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Seat;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.ConcertScheduleService;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.ConcertService;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.SeatService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConcertUsecase {

    private final ConcertService concertService;
    private final ConcertScheduleService concertScheduleService;
    private final SeatService seatService;

    public ConcertUsecase(ConcertService concertService, ConcertScheduleService concertScheduleService, SeatService seatService) {
        this.concertService = concertService;
        this.concertScheduleService = concertScheduleService;
        this.seatService = seatService;
    }

    /**
     * 모든 콘서트 조회
     *
     * @return List<ConcertDTO> 모든 콘서트의 정보를 담은 ConcertDTO 객체 리스트
     */
    public List<ConcertDTO> getAllConcerts() {
        List<Concert> concerts = concertService.findAll();
        return concerts.stream()
                .map(concert -> new ConcertDTO(concert.getConcertId(), concert.getConcertName()))
                .collect(Collectors.toList());
    }

    /**
     * 예약 가능 날짜 목록 조회
     *
     * @return List<ConcertScheduleDTO> 예약 가능한 날짜 정보를 담은 ConcertDTO 객체 리스트
     */
    public List<ConcertScheduleDTO> getAvailableDates(long concertId) {
        List<ConcertSchedule> scheduls = concertScheduleService.findByConcertId(concertId);
        return scheduls.stream()
                .map(concertSchedule -> new ConcertScheduleDTO(concertSchedule.getConcertScheduleId(), concertSchedule.getSeatCount(), concertSchedule.getConcertDate()))
                .collect(Collectors.toList());
    }

    /**
     * 예약 가능 좌석 목록 조회
     *
     * @return List<SeatDTO> 예약 가능한 좌석 정보를 담은 ConcertDTO 객체 리스트
     */
    public List<SeatDTO> getAvailableSeats(long concertScheduleId) {
        List<Seat> seats = seatService.findAvailableSeats(concertScheduleId);
        return seats.stream()
                .map(seat -> new SeatDTO(seat.getSeatId(), seat.getConcertScheduleId(), seat.getSeatNumber(), seat.getPrice(), seat.isFinallyReserved(), seat.getTempAssigneeId()))
                .collect(Collectors.toList());
    }
}

