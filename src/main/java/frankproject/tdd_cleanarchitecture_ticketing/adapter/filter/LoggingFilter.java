package frankproject.tdd_cleanarchitecture_ticketing.adapter.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class LoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // ContentCachingRequestWrapper로 요청 래핑
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);

        // 로깅할 정보 수집
        String requestUrl = requestWrapper.getRequestURL().toString();
        String httpMethod = requestWrapper.getMethod();
        String clientIp = requestWrapper.getRemoteAddr();

        // 다음 필터 또는 서블릿 호출
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        chain.doFilter(requestWrapper, responseWrapper);

        // 요청 로깅
        String requestBodyForLog = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        logger.info("[Request - {}] [{}] [{}] --- Request Body: {}", httpMethod, requestUrl, clientIp, requestBodyForLog);

        // 응답 로깅
        byte[] responseBody = responseWrapper.getContentAsByteArray();
        String responseBodyForLog = new String(responseBody, StandardCharsets.UTF_8);
        int statusCode = responseWrapper.getStatus();
        logger.info("[Response - {}] [{}] --- Response Body: {}", statusCode, clientIp, responseBodyForLog);

        // responseBody 재복사
        responseWrapper.copyBodyToResponse();
    }
}
