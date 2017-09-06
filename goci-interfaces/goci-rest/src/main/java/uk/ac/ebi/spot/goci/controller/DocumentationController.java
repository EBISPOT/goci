package uk.ac.ebi.spot.goci.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by dwelter on 24/11/16.
 */

@Controller
public class DocumentationController {

    @RequestMapping(value = {"docs", "/", ""})
    public String showDocsIndex(Model model) {
        return "redirect:docs/index";
    }
//    // ok, this is bad, need to find a way to deal with trailing slashes and constructing relative URLs in the thymeleaf template...
    @RequestMapping({"docs/"})
    public String showDocsIndex2(Model model) {
        return "redirect:index";
    }
    @RequestMapping({"docs/{page}"})
    public String showDocs(@PathVariable("page") String pageName, Model model) {


        model.addAttribute("page", pageName);
        return "docs-template";
    }



}
