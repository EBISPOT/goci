package uk.ac.ebi.fgpt.goci;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.util.HashSet;
import java.util.Set;

/**
 * Driver class for starting up the GOCI Tracking system.  This will startup a new jetty server, serving a deployment of
 * the GOCI tracking web application.
 *
 * @author Tony Burdett
 * @date 12/12/11
 */
public class GOCITrackerDriver {
    public static void main(String[] args) {
        try {
            // create server
            Server server = new Server(8080);

            // find any WAR files on the classpath
            String classpath = System.getProperty("java.class.path");
            String[] classPathElements = classpath.split(":");
            Set<String> warElements = new HashSet<String>();
            for (String s : classPathElements) {
                if (s.toLowerCase().endsWith(".war")) {
                    warElements.add(s);
                }
            }

            // if there is more than one war, regex it
            String warName = null;
            if (warElements.size() != 0) {
                if (warElements.size() == 1) {
                    warName = warElements.iterator().next();
                }
                else {
                    // multiple elements, regex on expected name and pick first
                    for (String s : warElements) {
                        if (s.matches("goci-tracker-web-[\\d*\\.\\d*][A-Z\\-]*.war")) {
                            warName = s;
                            break;
                        }
                    }
                }

                if (warName != null) {
                    // create and configure webapp context
                    WebAppContext webapp = new WebAppContext();
                    webapp.setContextPath("/goci");
                    System.out.println("Starting tracker application using WAR file: " + warName);
                    webapp.setWar(warName);

                    // add webapp context to server
                    server.setHandler(webapp);

                    // start the server
                    server.start();
                    server.join();
                }
                else {
                    System.err.println("Unable to located tracker web application (goci-tracker-web*.war) " +
                                               "on the classpath");
                    System.exit(1);
                }
            }
            else {
                System.err.println("Unable to located tracker web application (goci-tracker-web*.war) " +
                                           "on the classpath");
                System.exit(1);
            }
        }
        catch (InterruptedException e) {
            System.err.println("The server was interrupted and will shutdown");
            e.printStackTrace();
            System.exit(1);
        }
        catch (Exception e) {
            System.err.println("The server encountered an exception and will shutdown");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
