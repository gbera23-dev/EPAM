package app.filters.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
@Component
public class TransactionFilter extends OncePerRequestFilter {

    private final String TRANSACTION_HEADER_NAME = "X-Transaction-ID";
    private final String MDC_KEY = "transactionId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        /*check whether HTTP request already contains transaction id*/
        String transactionId = request.getHeader(TRANSACTION_HEADER_NAME);

        /*if transaction id is not present in the header, we generate the new one and add it as a header to the
          HTTP request*/
        if(transactionId == null) {
            transactionId = UUID.randomUUID().toString();
        }

        /*
            We make the transaction id available to the logging framework, so that it will log everything with
            transaction id
         */
        try {
            MDC.put(MDC_KEY, transactionId);
            filterChain.doFilter(request, response);
        }
        finally {
            MDC.remove(MDC_KEY);
        }

    }
}
