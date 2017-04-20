package uk.ac.ebi.spot.goci.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by xinhe on 19/04/2017.
 */
@Controller
public class TraitController {

    @RequestMapping(path = "/beta/trait/{traitName}",method = RequestMethod.GET)
    public String traitPage(){
        return "trait-page2";
    }


}
