package uk.ac.ebi.fgpt.goci;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

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
            
            // create and configure webapp context
            WebAppContext webapp = new WebAppContext();
            webapp.setContextPath("/goci");
            webapp.setWar("lib/goci-tracker-1.0-SNAPSHOT.war");

            // add webapp context to server 
            server.setHandler(webapp);

            // start the server
            server.start();
            server.join();
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
