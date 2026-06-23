package app.filters;

import app.exceptions.DDOSProtectionException;
import app.services.DDOSProtectionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class DDOSProtectionFilter extends OncePerRequestFilter {

    private static final String HTTP_LOGIN_REQUEST_ENDPOINT = "/api/user/login";

    private final DDOSProtectionService ddosProtectionService;

    public DDOSProtectionFilter(DDOSProtectionService ddosProtectionService) {
        this.ddosProtectionService = ddosProtectionService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        String requestUri = request.getRequestURI();

        //we release all locks that have expired
        ddosProtectionService.releaseUsersWithExpiredLocks();

        //check that API request is sent to login endpoint
        if(requestUri.equals(HTTP_LOGIN_REQUEST_ENDPOINT)) {

            //get unique identifier for the user to know he is trying to log in
            String userUniqueIdentifier = request.getRemoteAddr();

            //if user is blocked, we immediately write appropriate response and stop the filter chain, which
            //will release pressure on the server and protect it from DDOS attacks
            if(ddosProtectionService.userIsBlocked(userUniqueIdentifier)) {
                response.setStatus(429);
                response.getWriter().write("You are blocked due to numerous failed logging attempts. " +
                        "Please, wait for " + ddosProtectionService.
                        timeLeftBeforeLockIsReleased(userUniqueIdentifier) + " seconds to attempt again!");
                return;
            }
        }
        //proceed with filter chain if request is not login request, or it is and user is not blocked
        filterChain.doFilter(request, response);
    }
}
