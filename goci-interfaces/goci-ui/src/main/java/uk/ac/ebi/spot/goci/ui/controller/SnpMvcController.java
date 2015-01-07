package uk.ac.ebi.spot.goci.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.ebi.spot.goci.ui.repository.SnpRepository;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 14/11/14
 */
@Controller
public class SnpMvcController {
    private SnpRepository snpRepository;

    @Autowired
    SnpMvcController(SnpRepository snpRepository) {
        this.snpRepository = snpRepository;
    }

    @RequestMapping(value = "/snps", produces = MediaType.TEXT_HTML_VALUE)
    String snps(Model model) {
        model.addAttribute("snps", snpRepository.findAll());
        return "snps";
    }

    @RequestMapping(value = "/snps/{rsId}", produces = MediaType.TEXT_HTML_VALUE)
    String snps(Model model,
                @PathVariable
                String rsId) {
        model.addAttribute("snps", snpRepository.findByRsId(rsId));
        return "snps";
    }

    @RequestMapping(value = "/results", produces = MediaType.TEXT_HTML_VALUE)
    String results(Model model) {
        model.addAttribute("results", snpRepository.findAll());
        return "results";
    }
}
