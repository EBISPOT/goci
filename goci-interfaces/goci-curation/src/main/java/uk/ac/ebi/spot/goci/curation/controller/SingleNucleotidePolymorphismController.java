package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.curation.model.Ethnicity;
import uk.ac.ebi.spot.goci.curation.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.curation.repository.SingleNucleotidePolymorphismRepository;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 21/11/14.
 *
 * @author emma
 *         <p/>
 *         SNP Controllers interpret user input and transform it into a SNP
 *         model that is represented to the user by the associated HTML page.
 */

@Controller
@RequestMapping("/snps")
public class SingleNucleotidePolymorphismController {

    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;

    @Autowired
    public SingleNucleotidePolymorphismController(SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository) {
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
    }

    // Returns list of all snps
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    String snps(Model model) {
        model.addAttribute("snps", singleNucleotidePolymorphismRepository.findAll());
        return "snps";
    }

    // Return snp by rsID
    @RequestMapping(value = "/{rsID}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    String snp(Model model, @PathVariable String rsID) {
        model.addAttribute("snps", singleNucleotidePolymorphismRepository.findByRsIDIgnoreCase(rsID));
        return "snps";
    }

    // TODO COULD EXPAND THIS AND HAVE A PAGE TO SEE ALL GENES ASSOCIATED WITH A SNP
}
