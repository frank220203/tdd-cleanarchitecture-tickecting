package frankproject.tdd_cleanarchitecture_ticketing.domain.service;

import frankproject.tdd_cleanarchitecture_ticketing.domain.common.CoreException;
import frankproject.tdd_cleanarchitecture_ticketing.domain.common.ErrorCode;
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

    // 콘서트 대기열 참가
    public Token generateNewToken(long customerId, long concertId) {
        Token targetToken = findByCustomerId(customerId, concertId);

        if(targetToken != null) {
            throw new CoreException(ErrorCode.ALREADY_IN_QUEUE);
        }

        // 대기열에 PENDING 중인 사람의 마지막 순번을 가져와서 +1 해준 후, 신규 토큰의 순번으로 넣는다.
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

        return save(newToken);
    }

    // 본인 콘서트 대기열 조회
    public Token checkToken(long customerId, long concertId) {
        Token targetToken = findByCustomerId(customerId, concertId);

        if(targetToken == null) {
            throw new CoreException(ErrorCode.TOKEN_NOT_FOUND);
        }

        // 대기열을 통과한 마지막 토큰의 순번 조회 후, 본인 토큰의 순번 update
        long lastNum = 0L;
        List<Token> activeTokens = findActiveTokensByConcertId(concertId);
        if(!activeTokens.isEmpty()){
            lastNum = activeTokens.get(0).getWaitNumber();
        }

        return new Token(
                targetToken.getTokenId(),
                targetToken.getConcertId(),
                targetToken.getCustomerId(),
                targetToken.getWaitNumber() - lastNum,
                targetToken.getStatus(),
                targetToken.getCreatedAt(),
                null
        );
    }

    // 콘서트 별 활성화 토큰 조회
    public List<Token> findActiveTokensByConcertId(long concertId) {
        return tokenRepository.findActiveTokensByConcertId(concertId);
    }

    // 토큰 활성화 여부 조회
    public boolean isActiveToken(long tokenId){

        Optional<Token> tokenOptional = tokenRepository.findByTokenId(tokenId);
        if (tokenOptional.isEmpty()) {
            throw new CoreException(ErrorCode.TOKEN_NOT_FOUND);
        }

        return tokenOptional
                .map(token -> {
                    if (!token.isActive()) {
                        throw new CoreException(ErrorCode.UNAUTHORIZED);
                    }
                    return true;
                })
                .orElseThrow(() -> new CoreException(ErrorCode.TOKEN_NOT_FOUND));
    }

    // 고객 ID로 토큰 조회
    public Token findByCustomerId(long customerId, long concertId) {
        return tokenRepository.findByCustomerId(customerId, concertId);
    }

    // 토큰 활성화 (대기열 통과)
    public List<Token> activeToken(long concertId, int size) {

        List<Token> waitingTokens = tokenRepository.findPendingTokensByConcertId(concertId);
        List<Token> activeTokens = findActiveTokensByConcertId(concertId);
        for(Token nextToken : waitingTokens){
            if(activeTokens.size() >= size){
                return activeTokens;
            }

            nextToken.markAsActive();

            this.save(nextToken);
            activeTokens.add(nextToken);
        }

        return activeTokens;
    }

    // 토큰 만료
    public List<Token> expireToken(long concertId) {

        List<Token> tokens = findActiveTokensByConcertId(concertId);

        return tokens.stream()
                .filter(Token::isExpired)
                .peek(token -> {
                    token.markAsExpired();
                    this.save(token);
                })
                .collect(Collectors.toList());
    }

    // 토큰 저장 및 update
    public Token save(Token token) {
        return tokenRepository.save(token);
    }
}
