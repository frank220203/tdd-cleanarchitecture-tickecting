package frankproject.tdd_cleanarchitecture_ticketing.domain.service;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Token;
import frankproject.tdd_cleanarchitecture_ticketing.domain.repository.TokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private TokenService tokenService;

    @Test
    public void generateToken(){

        // given
        long customerId = 1L;
        long concertId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Optional<Long> expectedWaitNumber = Optional.of(1L);
        long nextWaitNumber = expectedWaitNumber.orElse(0L) + 1;
        Token givenToken = new Token(1L, concertId, customerId, nextWaitNumber, "PENDING", now, null);

        // given Mock
        when(tokenRepository.findMaxPositionByConcertId(concertId)).thenReturn(expectedWaitNumber);
        when(tokenRepository.save(any(Token.class))).thenReturn(givenToken);

        // when
        Token actualToken = tokenService.generateToken(customerId, concertId);

        // then
        verify(tokenRepository, times(1)).findMaxPositionByConcertId(concertId);
        verify(tokenRepository, times(1)).save(any(Token.class));
        assertEquals(givenToken, actualToken);
    }

    @Test
    public void findById(){

        // given
        LocalDateTime now = LocalDateTime.now();
        long tokenId = 1L;
        Token givenToken = new Token(tokenId, 1L, 1L, 1, "ACTIVE", now, now);

        // given Mock
        when(tokenRepository.findById(tokenId)).thenReturn(givenToken);

        // when
        Token actualToken = tokenService.findById(tokenId);

        // then
        verify(tokenRepository, times(1)).findById(tokenId);
        assertEquals(givenToken, actualToken);
    }

    // TODO
    @Test
    public void activeToken(){

        // given
        LocalDateTime now = LocalDateTime.now();
        long concertId = 1L;
        Token givenPendingToken = new Token(1L, 1L, concertId, 1L, "PENDING", now, now);
        Token givenToken = new Token(1L, 1L, concertId, 1L, "ACTIVE", now, now);
        // given Mock
        when(tokenRepository.findPendingTokensByConcertId(concertId))
                .thenReturn(Collections.singletonList(givenPendingToken));
        when(tokenRepository.findByIdForUpdate(givenPendingToken.getTokenId())).thenReturn(givenToken);
        when(tokenRepository.save(any(Token.class))).thenReturn(givenToken);

        // when
        Token actualToken = tokenService.activeToken(concertId);

        // then
        verify(tokenRepository, times(1)).save(any(Token.class));
        assertEquals(givenToken.getStatus(), actualToken.getStatus());
    }
}
