package frankproject.tdd_cleanarchitecture_ticketing.domain.service;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Token;
import frankproject.tdd_cleanarchitecture_ticketing.domain.repository.TokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TokenService {

    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Token issueToken(long customerId, long concertId) {
        synchronized (this) {

            Token targetToken = tokenRepository.findByCustomerId(customerId, concertId);

            if(targetToken == null) {
                Optional<Long> maxWaitNumber = tokenRepository.findMaxPositionByConcertId(concertId);
                long nextWaitNumber = maxWaitNumber.orElse(0L) + 1;
                Token newToken = new Token(
                        concertId,
                        customerId,
                        nextWaitNumber,
                        "PENDING",
                        LocalDateTime.now(),
                        null
                );

                Token result = tokenRepository.save(newToken);
                if(newToken.getConcertId() == 1L) {
                    System.out.println("토큰 생성 : " + result.toString());
                }
                return result;
            }


            List<Token> activeTokens = tokenRepository.findActiveTokensByConcertId(concertId);
            if(targetToken.getConcertId() == 1L) {
                System.out.println("토큰 존재 : " + targetToken.toString());
            }
            Token lastActiveToken = activeTokens.get(0);

            return new Token(
                    targetToken.getTokenId(),
                    targetToken.getConcertId(),
                    targetToken.getCustomerId(),
                    targetToken.getWaitNumber() - lastActiveToken.getWaitNumber(),
                    targetToken.getStatus(),
                    targetToken.getCreatedAt(),
                    null
            );
        }
    }

    public Optional<Token> findById(long tokenId) {
        return tokenRepository.findById(tokenId);
    }
    public Token findByCustomerId(long customerId, long concertId) {
        return tokenRepository.findByCustomerId(customerId, concertId);
    }

    public List<Token> activeToken(long concertId, int size) {

        List<Token> waitingTokens = tokenRepository.findPendingTokensByConcertId(concertId);
        List<Token> activeTokens = tokenRepository.findActiveTokensByConcertId(concertId);
        for(Token nextToken : waitingTokens){
            if(activeTokens.size() >= size){
                if(concertId == 1){
                    System.out.println("대기열 꽉참 : "+ nextToken);
                }
                return activeTokens;
            }

            nextToken.markAsActive();
            if(concertId == 1){
                System.out.println("대기열 통과 : "+ nextToken);
            }

            tokenRepository.save(nextToken);
            activeTokens.add(nextToken);
        }

        return activeTokens;
    }

    public List<Token> expireToken(long concertId){

        List<Token> tokens = tokenRepository.findActiveTokensByConcertId(concertId);

        return tokens.stream()
                .filter(Token::isExpired)
                .peek(token -> {
                    token.markAsExpired();
                    if(concertId == 1){
                        System.out.println("토큰 만료 : "+ token);
                    }
                    tokenRepository.save(token);
                })
                .collect(Collectors.toList());
    }
}
