package uk.ac.ebi.fgpt.lode.servlet.health;

import javax.servlet.*;
import java.io.IOException;

/**
 * A health check filter that complies with EBI E.S. guidelines about how to filter health requests to your webapp to
 * prevent excessive logging of automated checks.
 *
 * @author Tony Burdett
 * @date 13/10/11
 */
public class HealthFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        servletRequest.setAttribute("health", true);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
    }
}