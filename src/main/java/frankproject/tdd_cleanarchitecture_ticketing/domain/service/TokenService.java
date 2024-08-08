package frankproject.tdd_cleanarchitecture_ticketing.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import frankproject.tdd_cleanarchitecture_ticketing.domain.common.CoreException;
import frankproject.tdd_cleanarchitecture_ticketing.domain.common.ErrorCode;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.RedisToken;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Token;
import frankproject.tdd_cleanarchitecture_ticketing.domain.repository.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TokenService {

    private static final String QUEUE_PREFIX = "queue:";
    private static final String ACTIVE_PREFIX = "active:";

    private final RedisTemplate<String, Object> redisTemplate;

    private final TokenRepository tokenRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    public TokenService(TokenRepository tokenRepository, RedisTemplate<String, Object> redisTemplate) {
        this.tokenRepository = tokenRepository;
        this.redisTemplate = redisTemplate;
    }

    // 콘서트 대기열 참가
    @Transactional
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

    // 레디스를 이용한 콘서트 대기열 참가
    public RedisToken generateNewTokenWithRedis(long customerId) {
        log.info("1번 토큰 생성: {}", customerId);
        RedisToken targetToken = null;
        RedisToken newToken = null;
        try{
            targetToken = checkTokenWithRedis(customerId);
        } catch(Exception e) {
            long now = System.currentTimeMillis();
            UUID uuid = UUID.randomUUID();
            // 대기열의 현재 마지막 순번을 Redis에서 가져옴
            ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
            newToken = new RedisToken(customerId, uuid);
            String jsonString = "";
            try {
                // 객체를 JSON 문자열로 변환
                jsonString = objectMapper.writeValueAsString(newToken);
            } catch (JsonProcessingException jpe) {
                log.error("예외 발생 오류 메시지: {}", jpe.getMessage());
            }

            // Redis 리스트에 새로운 토큰 추가
            zSetOps.add(QUEUE_PREFIX, jsonString, now);

            return newToken;
        }

        if(targetToken != null) {
            throw new CoreException(ErrorCode.ALREADY_IN_QUEUE);
        }

        return newToken;
    }

    // 본인 콘서트 대기열 조회
    @Transactional
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

    // 레디스 대기열 조회
    public RedisToken checkTokenWithRedis(long customerId) {

        // Redis의 ZSetOperations를 사용하여 ZSet 가져오기
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        log.info("Service == 고객 ID : {}", customerId);

        // 고객의 토큰을 ZSet에서 찾기
        Set<ZSetOperations.TypedTuple<Object>> zSet = zSetOps.rangeWithScores(QUEUE_PREFIX, 0, -1);
        String targetValue = "";
        RedisToken targetToken = null;
        RedisToken newToken = null;
        log.info("zSet 크기 : {}", zSet.size());
        if (zSet != null) {
            for (ZSetOperations.TypedTuple<Object> tuple : zSet) {
                String value = (String) tuple.getValue();
                log.info("value : {}", value);
                try {
                    // JSON 문자열을 파싱하여 원하는 필드를 추출
                    targetToken = objectMapper.readValue(value, RedisToken.class);
                    if (targetToken.getCustomerId() == customerId) {
                        log.info("Service == 찾은 고객 ID : {}", targetToken.getCustomerId());
                        targetValue =  value; // JSON 문자열 반환
                        newToken = targetToken;
                        log.info("target value : {}", targetValue);
                    }
                } catch (IOException e) {
                    log.error("예외 발생 오류 메시지 : {}", e.getMessage());
                }
            }
        }

        long rank = -1;
        if(targetValue.isEmpty()) {
            throw new CoreException(ErrorCode.TOKEN_NOT_FOUND);
        } else {
            rank = zSetOps.rank(QUEUE_PREFIX, targetValue);
        }

        // 토큰 순서를 계산해서 리턴
        return new RedisToken(newToken.getCustomerId(), newToken.getTokenID(), rank);
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

    // 레디스 토큰 활성화 여부 조회
    public boolean isActiveTokenWithRedis(UUID tokenId) {
        // Redis의 ZSetOperations를 사용하여 ZSet 가져오기
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        log.info("Service == 고객 ID : {}", tokenId);

        // 고객의 토큰을 ZSet에서 찾기
        Set<ZSetOperations.TypedTuple<Object>> zSet = zSetOps.rangeWithScores(ACTIVE_PREFIX, 0, -1);
        String targetValue = "";
        RedisToken targetToken = null;
        log.info("zSet 크기 : {}", zSet.size());
        if (zSet != null) {
            for (ZSetOperations.TypedTuple<Object> tuple : zSet) {
                String value = (String) tuple.getValue();
                log.info("value : {}", value);
                try {
                    // JSON 문자열을 파싱하여 원하는 필드를 추출
                    targetToken = objectMapper.readValue(value, RedisToken.class);
                    log.info("UUID : {}", tokenId);
                    log.info("tokenID : {}", targetToken.getTokenID());
                    if (targetToken.getTokenID().equals(tokenId)) {
                        log.info("Service == 찾은 고객 ID : {}", targetToken.getTokenID());
                        targetValue =  value; // JSON 문자열 반환
                        log.info("target value : {}", targetValue);

                        return true;
                    }
                } catch (IOException e) {
                    log.error("예외 발생 오류 메시지 : {}", e.getMessage());
                }
            }
        }
        return false;
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

    // 레디스 토큰 활성화 (대기열 통과)
    public void activeTokenWithRedis(int size) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<Object>> zSetQueue = zSetOps.rangeWithScores(QUEUE_PREFIX, 0, -1);
        int emptyActive = 0;
        if(zSetQueue != null) {
            Set<ZSetOperations.TypedTuple<Object>> zSetActive = zSetOps.rangeWithScores(ACTIVE_PREFIX, 0, -1);
            if(zSetActive != null) {
                emptyActive = size - zSetActive.size();
                for(int i = 0; i < emptyActive; i++) {
                    ZSetOperations.TypedTuple<Object> minScoreItem = zSetQueue.stream().min(Comparator.comparingDouble(ZSetOperations.TypedTuple::getScore)).orElse(null);
                    zSetOps.add(ACTIVE_PREFIX, minScoreItem.getValue(), System.currentTimeMillis());
                    log.info("[토큰 활성화] {}", minScoreItem.getValue());
                    zSetOps.remove(QUEUE_PREFIX, minScoreItem.getValue());
                }
            }
        }
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

    // 레디스 토큰 만료
    public void expireTokenWithRedis() {

        // Redis의 ZSetOperations를 사용하여 Active ZSet 가져오기
        long now = System.currentTimeMillis();
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<Object>> zSet = zSetOps.rangeWithScores(ACTIVE_PREFIX, 0, -1);

        // 10분 지난 토큰 만료
        if (zSet != null) {
            for (ZSetOperations.TypedTuple<Object> tuple : zSet) {
                String value = (String) tuple.getValue();
                Double score = tuple.getScore() + 60000;
                if(score < now) {
                    zSetOps.remove(ACTIVE_PREFIX, value);
                    log.info("[토큰 만료] {}", value);
                }
            }
        }
    }

    // 토큰 저장 및 update
    public Token save(Token token) {
        return tokenRepository.save(token);
    }
}
