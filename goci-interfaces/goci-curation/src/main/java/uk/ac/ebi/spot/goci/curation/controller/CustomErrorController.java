package uk.ac.ebi.spot.goci.curation.controller;


import java.util.Map;
import uk.ac.ebi.spot.goci.curation.model.errors.ErrorModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by emma on 24/10/2016.
 *
 * @author Cinzia
 *         <p>
 *         CustomErrorController controller. This allow us to create a specific page error.
 *         Whitelabel Error Page is substituted to a specific error page.
 */


@Controller
public class CustomErrorController implements ErrorController {
    private static final String PATH = "/error";

    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping(value = PATH)
    public ModelAndView error(HttpServletRequest request, HttpServletResponse response, Exception ex)  {
        // Appropriate HTTP response code (e.g. 404 or 500) is automatically set by Spring.
        // Here we just define response body.

        ErrorModelAndView error = new ErrorModelAndView(response.getStatus(), getErrorAttributes(request, true));

        return error.getMav();
    }


    @Override
    public String getErrorPath() {
        return PATH;
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
    }
}
