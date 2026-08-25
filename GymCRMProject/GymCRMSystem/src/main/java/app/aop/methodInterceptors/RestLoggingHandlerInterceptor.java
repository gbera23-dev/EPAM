package app.aop.methodInterceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class RestLoggingHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        log.info("Request with uri {} has been received by API. HTTP method type {}", uri, method);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        int status = response.getStatus();

        if (ex != null) {
            log.error("Request with uri {} and HTTP method type {} ran into problems!", uri, method);
            log.error("Exception {} was thrown with message: {}", ex.getClass(), ex.getMessage());
            return;
        }

        log.info("Request with uri {} and HTTP method type {} has been resolved with status {} {}",
                uri, method, status, org.springframework.http.HttpStatus.valueOf(status).name());
    }
}