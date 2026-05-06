package Logging;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    public static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* services.*.*(..))")
    public void serviceLayer() {}

    @Pointcut("execution(* persistence.*.*(..))")
    public void persistenceLayer() {}

    @Before("serviceLayer()")
    public void logServiceBefore(JoinPoint joinPoint) {
        logger.info("Service Method {} of the {} has started running...",
                joinPoint.getSignature().getName(),
                joinPoint.getSignature().getDeclaringTypeName());
    }

    @After("serviceLayer()")
    public void logServiceAfter(JoinPoint joinPoint) {
        logger.info("Service Method {} of the {} has successfully finished execution",
                joinPoint.getSignature().getName(),
                joinPoint.getSignature().getDeclaringTypeName());
    }

    @Before("persistenceLayer()")
    public void logPersistenceBefore(JoinPoint joinPoint) {
        logger.info("DAO Method {} of the {} has started running...",
                joinPoint.getSignature().getName(),
                joinPoint.getSignature().getDeclaringTypeName()
                );
    }

    @After("persistenceLayer()")
    public void logPersistenceAfter(JoinPoint joinPoint) {
        logger.info("DAO Method {} of the {} has successfully finished execution...",
                joinPoint.getSignature().getName(),
                joinPoint.getSignature().getDeclaringTypeName()
        );
    }

    @Around("serviceLayer() || persistenceLayer()")
    public Object logExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = pjp.proceed();

        long executionTime = System.currentTimeMillis() - start;
        logger.info("{} executed in {} ms", pjp.getSignature(), executionTime);

        return proceed;
    }

    @AfterThrowing(pointcut = "serviceLayer() || persistenceLayer()", throwing = "ex")
    public void logGlobalError(JoinPoint joinPoint, Exception ex) {
        logger.error(
                "Exception {} was thrown in the class {} by the method {}",
                ex.getClass(), joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
    }



}
