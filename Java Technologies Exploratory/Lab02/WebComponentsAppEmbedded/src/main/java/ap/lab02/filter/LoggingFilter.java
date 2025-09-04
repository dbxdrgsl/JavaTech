package ap.lab02.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public class LoggingFilter implements Filter {
    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        req.getServletContext().log(String.format("[LOG] %s %s ip=%s ua=%s",
                req.getMethod(), req.getRequestURI(), req.getRemoteAddr(), req.getHeader("User-Agent")));
        chain.doFilter(request, response);
    }
}
