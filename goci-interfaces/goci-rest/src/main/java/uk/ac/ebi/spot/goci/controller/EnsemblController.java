package uk.ac.ebi.spot.goci.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository;

import java.util.List;

/**
 * Created by dwelter on 03/02/17.
 */

@Controller
public class EnsemblController {

    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;

    @Autowired
    public EnsemblController(SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository){
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
    }

    @RequestMapping(value = "/api/singleNucleotidePolymorphisms/{range}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<SingleNucleotidePolymorphism> search(@PathVariable String range) {

        String chrom = range.split(":")[0];
        String locs = range.split(":")[1];

        int start = Integer.parseInt(locs.split("-")[0]);
        int end = Integer.parseInt(locs.split("-")[1]);

        List<SingleNucleotidePolymorphism>
                snps = singleNucleotidePolymorphismRepository.findByLocationsChromosomeNameAndLocationsChromosomePositionBetween(chrom, start, end);

         return snps;
    }
}
