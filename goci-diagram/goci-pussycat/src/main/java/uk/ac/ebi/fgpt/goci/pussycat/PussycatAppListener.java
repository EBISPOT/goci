package uk.ac.ebi.fgpt.goci.pussycat;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Date;
import java.util.Properties;

/**
 * Javadocs go here!
 *
 * @author Rob Davey
 * @date 30-Sep-2009
 */
public class PussycatAppListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent event) {
        ServletContext application = event.getServletContext();
        if (checkProxyConfig(application)) {
            Properties systemSettings = System.getProperties();
            String pStatus = application.getInitParameter("useProxy");
            if (pStatus.equals("true")) {
                String pHost = application.getInitParameter("httpProxyHost");
                String pPort = application.getInitParameter("httpProxyPort");

                systemSettings.put("http.proxyHost", pHost);
                systemSettings.put("http.proxyPort", pPort);
                systemSettings.put("http.nonProxyHosts", "localhost");

                //some proxies mess with read timeouts
                systemSettings.put("sun.net.client.defaultConnectTimeout", "1000");
                systemSettings.put("sun.net.client.defaultReadTimeout", "20000");

                System.setProperties(systemSettings);
                log(application, "Setting proxy to - " +
                        systemSettings.getProperty("http.proxyHost") + ":" +
                        systemSettings.getProperty("http.proxyPort"));
            }
            else {
                systemSettings.put("useProxy", "false");
                log(application, "useProxy set to false. Continuing...");
            }
        }
        else {
            log(application, "No proxy info found. Continuing...");
        }

        // probably need to initialize a PussycatService
        log(application,
            "Application Context Initialized: " + new Date());
    }

    public void contextDestroyed(ServletContextEvent event) {
        ServletContext application = event.getServletContext();
        WebApplicationContext context =
                WebApplicationContextUtils.getWebApplicationContext(application);

        // maybe shutdown pussycat?
        log(application, "Application Context Destroyed: " + new Date());
    }

    private void log(ServletContext application, Object message) {
        // contextName is the <display-name> for the web app defined in web.xml
        String applicationName = application.getServletContextName();

        // log data to server's event log and stdout
        application.log(applicationName + ": " + message.toString());
        System.out.println(applicationName + ": " + message);
    }

    private boolean checkProxyConfig(ServletContext application) {
        return (application.getInitParameter("useProxy") != null &&
                application.getInitParameter("httpProxyHost") != null &&
                application.getInitParameter("httpProxyPort") != null);
    }
}