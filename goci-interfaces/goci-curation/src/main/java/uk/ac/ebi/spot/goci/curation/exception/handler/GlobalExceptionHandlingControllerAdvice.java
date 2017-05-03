package uk.ac.ebi.spot.goci.curation.exception.handler;

import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.ebi.spot.goci.curation.model.errors.ErrorModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Logger;


/**
 * Created by xinhe on 11/04/2017. folloing https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc#sample-application
 * This is a controller specifically used to handle un-caught exception from other controllers
 */
@ControllerAdvice
public class GlobalExceptionHandlingControllerAdvice {
    protected Logger logger;
    public static final String DEFAULT_ERROR_VIEW = "error";

    private org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
    protected org.slf4j.Logger getLog() {
        return log;
    }

    //    //xintodo more
    //    @ResponseStatus(value=HttpStatus.CONFLICT, reason="Data integrity violation")  // 409
    //    @ExceptionHandler(DataIntegrityViolationException.class)
    //    public void conflict() {
    //        // Nothing to do
    //    }

    /**
     * Created by xinhe on 11/04/2017.
     * This method is the dedault place for un-handled exceptions raised from controllers.
     */
    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        // If the exception is annotated with @ResponseStatus rethrow it and let
        // the framework handle it - like the OrderNotFoundException example
        // at the start of this post.
        // AnnotationUtils is a Spring Framework utility class.
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null)
            throw e;

        getLog().warn("Unhandled exception caught GlobalExceptionHandlingControllerAdvice when User <" + req.getRemoteUser().toString() + "> requesting " + req.getRequestURL().toString() + " with HTTP-" + req.getMethod());
        getLog().warn("Exception: " + e);


        //        getLog().warn("Request Details:");

        //        Enumeration<String> headerNames = req.getHeaderNames();
        //        while(headerNames.hasMoreElements()) {
        //            String headerName = (String)headerNames.nextElement();
        //            getLog().warn("Header Name - " + headerName + ", Value - " + req.getHeader(headerName));
        //        }
        //
        //
        //        Enumeration<String> params = req.getParameterNames();
        //        while(params.hasMoreElements()){
        //            String paramName = (String)params.nextElement();
        //            getLog().warn("Parameter Name - "+paramName+", Value - "+req.getParameter(paramName));
        //        }


        //print stacktrace
        getLog().warn("StackTrace: ");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        getLog().warn(sw.toString());



        // Otherwise setup and send the user to a default error-view.
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", e);
        mav.addObject("url", req.getRequestURL());
        mav.addObject("trace",e.getStackTrace());
        mav.addObject("path", req.getRequestURL());
        mav.setViewName(DEFAULT_ERROR_VIEW);
        return mav;
    }



    /**
     * add mode attribute for all controller returned model
     * @param model
     */
    //    @ModelAttribute
    //    public void globalAttributes(Model model) {
    //        model.addAttribute("msg", "Welcome to My World!");
    //    }



}
