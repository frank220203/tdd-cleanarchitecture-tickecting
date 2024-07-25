package frankproject.tdd_cleanarchitecture_ticketing.domain.repository;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Token;

import java.util.List;
import java.util.Optional;

public interface TokenRepository {

    Optional<Long> findMaxPositionByConcertId(long concertId);

    Token save(Token newToken);

    Optional<Token> findByTokenId(long tokenId);

    Token findByCustomerId(long customerId, long concertId);

    List<Token> findPendingTokensByConcertId(long concertId);

    List<Token> findActiveTokensByConcertId(long concertId);
}
