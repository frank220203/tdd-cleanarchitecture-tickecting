package frankproject.tdd_cleanarchitecture_ticketing.domain.repository;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Token;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TokenRepository {

    public Optional<Long> findMaxPositionByConcertId(long concertId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Token save(Token newToken);

    public Token findById(long tokenId);

    @Query("SELECT t FROM Token t WHERE t.concert_id = :concertId AND t.status = 'PENDING' ORDER BY t.wait_number ASC")
    public List<Token> findPendingTokensByConcertId(@Param("concertId") Long concertId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Token t WHERE t.id = :tokenId")
    public Token findByIdForUpdate(@Param("tokenId") long tokenId);
}
