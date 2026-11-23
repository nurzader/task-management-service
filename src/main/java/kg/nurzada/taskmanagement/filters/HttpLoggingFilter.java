package kg.nurzada.taskmanagement.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class HttpLoggingFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().startsWith("/h2-console");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        int maxCacheBytes = 1024 * 1024;
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, maxCacheBytes);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            String requestBody = getPayload(requestWrapper.getContentAsByteArray());
            String responseBody = getPayload(responseWrapper.getContentAsByteArray());

            log.info("Запрос: {} {} | Тело запроса: {} | Статус: {} | Время выполнения: {} мс | Тело ответа: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    requestBody,
                    response.getStatus(),
                    duration,
                    responseBody);

            responseWrapper.copyBodyToResponse();
        }
    }

    private String getPayload(byte[] payloadBytes) {
        if (payloadBytes == null || payloadBytes.length == 0) {
            return "[]";
        }
        return new String(payloadBytes, StandardCharsets.UTF_8)
                .replaceAll("\\s+", " ")
                .trim();
    }
}
