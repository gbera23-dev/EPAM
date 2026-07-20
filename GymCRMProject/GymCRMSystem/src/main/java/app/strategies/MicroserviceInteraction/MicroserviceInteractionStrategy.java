package app.strategies.MicroserviceInteraction;

import org.aspectj.lang.ProceedingJoinPoint;

public interface MicroserviceInteractionStrategy {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TRANSACTION_HEADER_NAME = "X-Transaction-ID";

    Object sendTheRequest(ProceedingJoinPoint pjp) throws Throwable;
}
