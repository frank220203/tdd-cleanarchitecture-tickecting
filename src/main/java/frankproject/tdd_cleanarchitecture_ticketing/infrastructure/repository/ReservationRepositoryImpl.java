package frankproject.tdd_cleanarchitecture_ticketing.infrastructure.repository;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Reservation;
import frankproject.tdd_cleanarchitecture_ticketing.domain.repository.ReservationRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ReservationRepositoryImpl implements ReservationRepository {
    private final ReservationJpaRepository reservationJpaRepository;

    public ReservationRepositoryImpl(ReservationJpaRepository reservationJpaRepository) {
        this.reservationJpaRepository = reservationJpaRepository;
    }

    @Override
    public Optional<Reservation> findById(long reservationId) {
        return reservationJpaRepository.findById(reservationId);
    }

    @Override
    public List<Reservation> findByStatusAndReservationTimeBefore(String status, LocalDateTime cutoffTime) {
        return reservationJpaRepository.findByStatusAndReservationTimeBefore(status, cutoffTime);
    }

    @Override
    public Reservation save(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }
}
