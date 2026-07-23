package app.strategies.MicroserviceInteraction;

import org.aspectj.lang.ProceedingJoinPoint;

public interface MicroserviceInteractionStrategy {
    String AUTHORIZATION_HEADER = "Authorization";
    String TRAINING_BATCH_UPDATE_CHANNEL = "training-batch-update-channel";
    String TRAINING_UPDATE_CHANNEL = "training-update-channel";

    Object sendTheRequest(ProceedingJoinPoint pjp) throws Throwable;
}
