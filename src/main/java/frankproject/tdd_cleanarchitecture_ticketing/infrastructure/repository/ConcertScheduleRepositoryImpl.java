package frankproject.tdd_cleanarchitecture_ticketing.infrastructure.repository;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.ConcertSchedule;
import frankproject.tdd_cleanarchitecture_ticketing.domain.repository.ConcertScheduleRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConcertScheduleRepositoryImpl implements ConcertScheduleRepository {
    private final ConcertScheduleJpaRepository concertScheduleJpaRepository;

    public ConcertScheduleRepositoryImpl(ConcertScheduleJpaRepository concertScheduleJpaRepository) {
        this.concertScheduleJpaRepository = concertScheduleJpaRepository;
    }

    @Override
    public List<ConcertSchedule> findByConcertId(long concertId) {
        return concertScheduleJpaRepository.findByConcertId(concertId);
    }

    @Override
    public ConcertSchedule save(ConcertSchedule concertSchedule) {
        return concertScheduleJpaRepository.save(concertSchedule);
    }
}
