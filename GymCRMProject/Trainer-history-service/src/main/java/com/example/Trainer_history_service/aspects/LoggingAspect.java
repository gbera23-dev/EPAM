package com.example.Trainer_history_service.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Class utilizes AOP to provide Logging for the whole application
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private static final long SLOW_EXECUTION_THRESHOLD_MS = 1000;

    @Pointcut("execution(* com.example.Trainer_history_service.services.*.*(..))")
    public void serviceLayer() {}

    @Pointcut("execution(* com.example.Trainer_history_service.repository.*.*(..))")
    public void persistenceLayer() {}


    @Pointcut("execution(* com.example.Trainer_history_service.facade.*.*(..))")
    public void facadeLayer() {}



    @Around("facadeLayer()")
    public Object logFacadeLayerExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();

        log.info("Facade method {} of {} started execution", methodName, className);
        log.debug("Facade method {} called with arguments: {}", methodName, joinPoint.getArgs());

        Object result = joinPoint.proceed();

        log.info("Facade method {} of {} finished successfully", methodName, className);

        return result;
    }


    @Around("serviceLayer()")
    public Object logServiceExecution(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().getName();
        String className = pjp.getSignature().getDeclaringTypeName();

        log.info("Service method {} of {} started execution", methodName, className);
        log.debug("Service method {} called with arguments: {}", methodName, pjp.getArgs());

        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long executionTime = System.currentTimeMillis() - start;

        if (executionTime >= SLOW_EXECUTION_THRESHOLD_MS) {
            log.warn("Service method {} of {} exceeded slow execution threshold: {} ms",
                    methodName, className, executionTime);
        } else {
            log.debug("Service method {} of {} completed in {} ms", methodName, className, executionTime);
        }

        log.info("Service method {} of {} finished successfully", methodName, className);
        return result;
    }

    @Around("persistenceLayer()")
    public Object logPersistenceExecution(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().getName();
        String className = pjp.getSignature().getDeclaringTypeName();

        log.info("Repository method {} of {} started execution", methodName, className);
        log.debug("Repository method {} called with arguments: {}", methodName, pjp.getArgs());

        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long executionTime = System.currentTimeMillis() - start;

        if (executionTime >= SLOW_EXECUTION_THRESHOLD_MS) {
            log.warn("Repository method {} of {} exceeded slow execution threshold: {} ms",
                    methodName, className, executionTime);
        } else {
            log.debug("Repository method {} of {} completed in {} ms", methodName, className, executionTime);
        }

        log.info("Repository method {} of {} finished successfully", methodName, className);
        return result;
    }

    /**
     * Used As a general log, when something goes wrong in our application(Exception is thrown)
     * @param joinPoint Where the exception was thrown
     * @param ex Exception
     */
    @AfterThrowing(pointcut = "serviceLayer() || persistenceLayer()", throwing = "ex")
    public void logGlobalError(JoinPoint joinPoint, Exception ex) {
        log.error("Exception {} thrown in {} by method {} with message: {}",
                ex.getClass().getSimpleName(),
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                ex.getMessage());
    }

}