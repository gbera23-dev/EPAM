package com.example.Trainer_history_service.aop.methodInterceptors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jspecify.annotations.Nullable;
import org.slf4j.MDC;

import static com.example.Trainer_history_service.infrastructure.constants.TransactionConstants.MDC_KEY;

@Slf4j
@RequiredArgsConstructor
public class TransactionMethodInterceptor implements MethodInterceptor {


    @Override
    public @Nullable Object invoke(MethodInvocation invocation) throws Throwable {
        /*
            We make the transaction id available to the logging framework, so that it will log everything with
            transaction id
         */
        Object[] args = invocation.getArguments();
        String transactionId = (String)args[args.length-1];

        MDC.put(MDC_KEY, transactionId);
        try {
            log.info("transaction id has been successfully added to messaging microservice logs!");
            return invocation.proceed();
        }
        finally {
            MDC.remove(MDC_KEY);
        }
    }
}
