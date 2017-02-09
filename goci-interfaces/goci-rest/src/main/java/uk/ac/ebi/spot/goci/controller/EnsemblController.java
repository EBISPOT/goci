package uk.ac.ebi.spot.goci.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository;

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

    @RequestMapping(value = "/api/snpLocation/{range}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<PagedResources<SingleNucleotidePolymorphism>> search(@PathVariable String range,
                                                                           @PageableDefault(size = 20, page = 0) Pageable pageable) {

        String chrom = range.split(":")[0];
        String locs = range.split(":")[1];

        int start = Integer.parseInt(locs.split("-")[0]);
        int end = Integer.parseInt(locs.split("-")[1]);

        Page<SingleNucleotidePolymorphism>
                snps = singleNucleotidePolymorphismRepository.findByLocationsChromosomeNameAndLocationsChromosomePositionBetween(chrom, start, end, pageable);

        Resource<SingleNucleotidePolymorphism> snpResource = new Resource(snps);

        return new ResponseEntity(snpResource, HttpStatus.OK);
    }
}
