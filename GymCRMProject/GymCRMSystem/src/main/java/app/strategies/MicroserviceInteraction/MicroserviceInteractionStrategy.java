package app.strategies.MicroserviceInteraction;

import org.aspectj.lang.ProceedingJoinPoint;

public interface MicroserviceInteractionStrategy {
    Object sendTheRequest(ProceedingJoinPoint pjp) throws Throwable;
}
