package app.strategies.MicroserviceInteraction;

import org.aopalliance.intercept.MethodInvocation;

public interface MicroserviceInteractionStrategy {
    Object sendTheRequest(MethodInvocation invocation) throws Throwable;
}
