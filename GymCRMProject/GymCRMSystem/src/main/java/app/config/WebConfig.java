package app.config;

import app.methodInterceptors.RestLoggingHandlerInterceptor;
import app.methodInterceptors.RestMetricsHandlerInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RestLoggingHandlerInterceptor restLoggingHandlerInterceptor;
    private final RestMetricsHandlerInterceptor restMetricsHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(restLoggingHandlerInterceptor);
        registry.addInterceptor(restMetricsHandlerInterceptor);
    }
}