package frankproject.tdd_cleanarchitecture_ticketing.domain.repository;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Concert;

import java.util.List;
import java.util.Optional;

public interface ConcertRepository {

    List<Concert> findAll();

    Optional<Concert> findById(long concertId);

    Concert save(Concert concert);
}
