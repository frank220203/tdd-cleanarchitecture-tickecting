package frankproject.tdd_cleanarchitecture_ticketing.infrastructure.repository;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatJpaRepository extends JpaRepository<Seat, Long> {

    @Query("SELECT s FROM Seat s WHERE s.concertScheduleId = :concertScheduleId " +
            "AND s.finallyReserved = false " +
            "AND (s.tempAssigneeId IS NULL OR s.tempAssigneeId = 0)")
    List<Seat> findAvailableSeats(@Param("concertScheduleId") long concertScheduleId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.seatId = :seatId")
    Optional<Seat> findByIdWithPessimistic(long seatId);
}
