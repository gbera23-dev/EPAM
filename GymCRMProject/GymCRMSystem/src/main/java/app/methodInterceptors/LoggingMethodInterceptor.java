package app.methodInterceptors;

import app.annotations.PersistenceLayer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.Advised;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class LoggingMethodInterceptor implements MethodInterceptor {

    private final String layerLabel;
    private final long slowExecutionThresholdMs;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String methodName = invocation.getMethod().getName();
        String className = resolveTargetClassName(Objects.requireNonNull(invocation.getThis()));

        log.info("{} method {} of {} started execution", layerLabel, methodName, className);
        log.debug("{} method {} called with arguments: {}", layerLabel, methodName, invocation.getArguments());

        long start = System.currentTimeMillis();
        try {
            Object result = invocation.proceed();
            long executionTime = System.currentTimeMillis() - start;

            if (executionTime >= slowExecutionThresholdMs) {
                log.warn("{} method {} of {} exceeded slow execution threshold: {} ms",
                        layerLabel, methodName, className, executionTime);
            } else {
                log.debug("{} method {} of {} completed in {} ms", layerLabel, methodName, className, executionTime);
            }
            log.info("{} method {} of {} finished successfully", layerLabel, methodName, className);
            return result;

        } catch (Throwable throwable) {
            log.error("Exception {} thrown in {} by method {} with message: {}",
                    throwable.getClass().getSimpleName(), className, methodName, throwable.getMessage());
            throw throwable;
        }
    }

    private String resolveTargetClassName(Object target) throws Exception {

        for (Class<?> iface : target.getClass().getInterfaces()) {
            if (iface.isAnnotationPresent(PersistenceLayer.class)) {
                return iface.getSimpleName();
            }
        }

        if (target instanceof Advised advised) {
            Object unwrapped = advised.getTargetSource().getTarget();
            if (unwrapped != null) {
                return unwrapped.getClass().getSimpleName();
            }
        }
        return target.getClass().getSimpleName();
    }
}