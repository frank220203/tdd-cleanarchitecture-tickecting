package frankproject.tdd_cleanarchitecture_ticketing.infrastructure.repository;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Seat;
import frankproject.tdd_cleanarchitecture_ticketing.domain.repository.SeatRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SeatRepositoryImpl implements SeatRepository {
    private final SeatJpaRepository seatJpaRepository;

    public SeatRepositoryImpl(SeatJpaRepository seatJpaRepository) {
        this.seatJpaRepository = seatJpaRepository;
    }

    @Override
    public List<Seat> findAvailableSeats(long concertScheduleId) {
        return seatJpaRepository.findAvailableSeats(concertScheduleId);
    }

    @Override
    public Optional<Seat> findById(long seatId) {
        return seatJpaRepository.findById(seatId);
    }

    @Override
    public Seat save(Seat seat) {
        return seatJpaRepository.save(seat);
    }

    // 비관적 락을 위한 테스트 메소드
    @Override
    public Optional<Seat> findByIdWithPessimistic(long seatId) {
        return seatJpaRepository.findByIdWithPessimistic(seatId);
    }
}
