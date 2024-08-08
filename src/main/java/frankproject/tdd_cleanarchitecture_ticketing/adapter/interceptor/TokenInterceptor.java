package frankproject.tdd_cleanarchitecture_ticketing.adapter.interceptor;

import frankproject.tdd_cleanarchitecture_ticketing.application.usecase.TokenUsecase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    private final TokenUsecase tokenUsecase;

    @Autowired
    public TokenInterceptor(TokenUsecase tokenUsecase) {
        this.tokenUsecase = tokenUsecase;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        // Request에서 토큰 가져오기
        String tokenHeader = request.getHeader("Authorization");

        // Authorization 헤더가 없는 경우
        if (tokenHeader == null || tokenHeader.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        UUID token;

        try {
            // 헤더의 값이 숫자로 파싱 가능한지 확인
            token = UUID.fromString(tokenHeader);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 유효한 토큰인지 확인
        if (!tokenUsecase.isActiveTokenWithRedis(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        return true;
    }
}
