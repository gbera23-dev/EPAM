package com.example.Trainer_history_service.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class TransactionAspect {

    private final String MDC_KEY = "transactionId";


    @Pointcut("execution(* com.example.Trainer_history_service.consumers.*.*(..))")
    public void consumerLayer() {}

    @Around("consumerLayer() && args(.., transactionId)")
    private Object passTransactionIdToLogs(ProceedingJoinPoint pjp, String transactionId) throws Throwable {
        /*
            We make the transaction id available to the logging framework, so that it will log everything with
            transaction id
         */
        MDC.put(MDC_KEY, transactionId);
        try {
            log.info("transaction id has been successfully added to messaging microservice logs!");
            return pjp.proceed();
        }
        finally {
            MDC.remove(MDC_KEY);
        }
    }
}
