package frankproject.tdd_cleanarchitecture_ticketing.infrastructure.repository;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {
}
