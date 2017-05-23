package uk.ac.ebi.spot.goci.curation.model.errors;

import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * Created by cinzia on 24/10/2016.
 * This class (constructor) generates a ModelAndView instance that shows a specific error page. (404/500 whiteleaf)
 *
 */
public class ErrorModelAndView {
    public ModelAndView mav;


    public ErrorModelAndView(int status, Map<String, Object> errorAttributes) {
        mav = new ModelAndView();

        String sTrace ="-";
        String error_code = errorAttributes.get("status").toString();

        mav.addObject("status", error_code);
        mav.addObject("timestamp",errorAttributes.get("timestamp").toString());
        mav.addObject("error", errorAttributes.get("error"));
        mav.addObject("message", errorAttributes.get("message"));
        mav.addObject("path", errorAttributes.get("path"));
        mav.addObject("exception", errorAttributes.get("exception"));

        if (error_code.equals("404")) {
            mav.addObject("user_error", "Page Not Found");
            mav.addObject("user_error_alternative", "The requested page is not found");
        }
        else if (error_code.equals("500")) {
            mav.addObject("user_error", "Internal Server Error");
            mav.addObject("user_error_alternative", "Ops, something went wrong. ");
        }
        else {
            mav.addObject("user_error", errorAttributes.get("error"));
            mav.addObject("user_error_alternative", "Sorry, an error has occured!");
        }

        if (errorAttributes.get("trace") != null) {
            //sTrace = error_attributes.get("trace").toString().substring(0, 200);
            sTrace = errorAttributes.get("trace").toString();
        }
        mav.addObject("trace",sTrace);
        mav.setViewName("error");

    }

    public ModelAndView getMav() {
        return mav;
    }

}


