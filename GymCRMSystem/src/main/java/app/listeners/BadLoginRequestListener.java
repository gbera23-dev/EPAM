package app.listeners;

import app.exceptions.DDOSProtectionException;
import app.services.DDOSProtectionService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class BadLoginRequestListener {


    private final DDOSProtectionService ddosProtectionService;

    public BadLoginRequestListener(DDOSProtectionService ddosProtectionService) {
        this.ddosProtectionService = ddosProtectionService;
    }

    @EventListener
    public void handleBadLoginRequest(AuthenticationFailureBadCredentialsEvent authenticationFailedEvent) {
        Authentication authentication = authenticationFailedEvent.getAuthentication();

        String userIdentifier = authentication.getDetails().toString();

        ddosProtectionService.recordUserAttempt(userIdentifier);

        //if number of failed attempts exceed limit, user will be blocked
        if(ddosProtectionService.userShouldBeBlocked(userIdentifier)) {
            ddosProtectionService.blockUser(userIdentifier);
            throw new DDOSProtectionException("You are blocked due to numerous failed logging attempts. " +
                    "Please, wait for " + ddosProtectionService.
                    timeLeftBeforeLockIsReleased(userIdentifier) + " seconds to attempt again!");
        }
    }


}
