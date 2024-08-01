package frankproject.tdd_cleanarchitecture_ticketing.domain.repository;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Seat;

import java.util.List;
import java.util.Optional;

public interface SeatRepository {

    List<Seat> findAvailableSeats(long concertScheduleId);

    Optional<Seat> findById(long seatId);

    Seat save(Seat seat);

    // 비관적 락을 위한 테스트 메소드
    Optional<Seat> findByIdWithPessimistic(long seatId);
}
