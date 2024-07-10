package frankproject.tdd_cleanarchitecture_ticketing.domain.service;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Token;
import frankproject.tdd_cleanarchitecture_ticketing.domain.repository.TokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Transactional
    public Token generateToken(long customerId, long concertId){

        Optional<Long> maxWaitNumber = tokenRepository.findMaxPositionByConcertId(concertId);
        long nextWaitNumber = maxWaitNumber.orElse(0L) + 1;
        Token newToken = new Token(
                customerId,
                concertId,
                nextWaitNumber,
                "PENDING",
                LocalDateTime.now(),
                null
        );

        return tokenRepository.save(newToken);
    }

    public Token findById(long tokenId){
        return tokenRepository.findById(tokenId);
    }

    @Transactional
    public Token activeToken(long concertId){

        List<Token> tokens = tokenRepository.findPendingTokensByConcertId(concertId);
        Token nextTokenToPass = tokens.get(0);
        Token lockedToken = tokenRepository.findByIdForUpdate(nextTokenToPass.getTokenId());
        Token newToken = new Token(
                lockedToken.getCustomerId(),
                lockedToken.getConcertId(),
                lockedToken.getWaitNumber(),
                "ACTIVE",
                lockedToken.getCreatedAt(),
                LocalDateTime.now()
        );

        return tokenRepository.save(newToken);
    }
}
