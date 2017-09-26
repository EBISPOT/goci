package uk.ac.ebi.spot.goci.ui.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE) String root(Model model) {
        model.addAttribute("newsDiv", "1");
        return "index";
    }

    @RequestMapping(value = "/home", produces = MediaType.TEXT_HTML_VALUE) String home(Model model) {
        model.addAttribute("newsDiv", "1");
        return "index";
    }
}