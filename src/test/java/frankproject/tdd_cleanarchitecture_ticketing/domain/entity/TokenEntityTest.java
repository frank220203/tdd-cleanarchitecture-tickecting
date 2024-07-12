package frankproject.tdd_cleanarchitecture_ticketing.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TokenEntityTest {

    @Test
    public void isExpried(){

        // given
        LocalDateTime now = LocalDateTime.now();
        Token givenToken = new Token(1L, 1L, 1L, 1L, "ACTIVE", now.minusMinutes(40), now.minusMinutes(20));

        // when
        boolean result = givenToken.isExpired();

        // then
        assertTrue(result);
    }

    @Test
    public void isActive(){

        // given
        LocalDateTime now = LocalDateTime.now();
        Token givenToken = new Token(1L, 1L, 1L, 1L, "ACTIVE", now.minusMinutes(40), now);

        // when
        boolean result = givenToken.isActive();

        // then
        assertTrue(result);
    }
}
