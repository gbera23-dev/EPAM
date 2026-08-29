package com.example.Trainer_history_service.api.errorHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

@Component
@Slf4j
public class GlobalJmsErrorHandler implements ErrorHandler {

    @Override
    public void handleError(Throwable t) {
        log.error("Message processing failed: {}", t.getMessage());
    }

}
