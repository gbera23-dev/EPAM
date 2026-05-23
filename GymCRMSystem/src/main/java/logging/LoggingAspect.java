package logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Class utilizes AOP to provide Logging for the whole application
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    private static final long SLOW_EXECUTION_THRESHOLD_MS = 1000;

    @Pointcut("execution(* services.*.*(..))")
    public void serviceLayer() {}

    @Pointcut("execution(* persistence.*.*(..))")
    public void persistenceLayer() {}

    @Around("serviceLayer()")
    public Object logServiceExecution(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().getName();
        String className = pjp.getSignature().getDeclaringTypeName();

        logger.info("Service method {} of {} started execution", methodName, className);
        logger.debug("Service method {} called with arguments: {}", methodName, pjp.getArgs());

        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long executionTime = System.currentTimeMillis() - start;

        if (executionTime >= SLOW_EXECUTION_THRESHOLD_MS) {
            logger.warn("Service method {} of {} exceeded slow execution threshold: {} ms",
                    methodName, className, executionTime);
        } else {
            logger.debug("Service method {} of {} completed in {} ms", methodName, className, executionTime);
        }

        logger.info("Service method {} of {} finished successfully", methodName, className);
        return result;
    }

    @Around("persistenceLayer()")
    public Object logPersistenceExecution(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().getName();
        String className = pjp.getSignature().getDeclaringTypeName();

        logger.info("Repository method {} of {} started execution", methodName, className);
        logger.debug("Repository method {} called with arguments: {}", methodName, pjp.getArgs());

        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long executionTime = System.currentTimeMillis() - start;

        if (executionTime >= SLOW_EXECUTION_THRESHOLD_MS) {
            logger.warn("Repository method {} of {} exceeded slow execution threshold: {} ms",
                    methodName, className, executionTime);
        } else {
            logger.debug("Repository method {} of {} completed in {} ms", methodName, className, executionTime);
        }

        logger.info("Repository method {} of {} finished successfully", methodName, className);
        return result;
    }

    /**
     * Used As a general logger, when something goes wrong in our application(Exception is thrown)
     * @param joinPoint Where the exception was thrown
     * @param ex Exception
     */
    @AfterThrowing(pointcut = "serviceLayer() || persistenceLayer()", throwing = "ex")
    public void logGlobalError(JoinPoint joinPoint, Exception ex) {
        logger.error("Exception {} thrown in {} by method {} with message: {}",
                ex.getClass().getSimpleName(),
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                ex.getMessage());
    }

}