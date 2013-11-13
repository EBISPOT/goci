package uk.ac.ebi.fgpt.lode.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A servlet that can be embedded within your web application and simply returns the string "OK" if it could be
 * accessed.  This can be used to allow load balancers in the production environment to detect when a web application
 * has crashed and send a notification to the mailing lists.
 */
public class StatusServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        PrintWriter out = null;
        try {
            out = resp.getWriter();
            out.println("OK");
        }
        finally {
            if (null != out) {
                out.flush();
                out.close();
            }
        }

    }
}