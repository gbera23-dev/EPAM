package app.strategies.MicroserviceInteraction;

import org.aspectj.lang.ProceedingJoinPoint;

public interface MicroserviceInteractionStrategy {
    public static final String AUTHORIZATION_HEADER = "Authorization";

    Object sendTheRequest(ProceedingJoinPoint pjp) throws Throwable;
}
