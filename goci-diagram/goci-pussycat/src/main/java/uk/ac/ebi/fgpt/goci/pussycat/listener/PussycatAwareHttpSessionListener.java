package uk.ac.ebi.fgpt.goci.pussycat.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.fgpt.goci.pussycat.manager.PussycatManager;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 21/08/12
 */
public class PussycatAwareHttpSessionListener implements HttpSessionListener {
    private Logger log = LoggerFactory.getLogger(getClass());

    public Logger getLog() {
        return log;
    }

    @Override public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        HttpSession session = httpSessionEvent.getSession();
        getLog().debug("HttpSession '" + session.getId() + "' created - taking no action, " +
                       "resources will be allocated on first request");
    }

    @Override public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        HttpSession session = httpSessionEvent.getSession();
        ServletContext servletContext = session.getServletContext();
        WebApplicationContext appContext =
                (WebApplicationContext) servletContext.getAttribute(
                        WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        PussycatManager pussycatManager = appContext.getBean("pussycatManager", PussycatManager.class);

        getLog().debug("HttpSession '" + session.getId() + "' destroyed - attempting to unbind resources");
        boolean unbound = pussycatManager.unbindResources(session);
        getLog().debug("Unbinding resources for HttpSession '" + session.getId() + "' " + (unbound ? "ok" : "failed"));
    }
}
