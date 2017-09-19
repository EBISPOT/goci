package uk.ac.ebi.spot.goci.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository;


/**
 * Created by dwelter on 19/09/17.
 */

//@RepositoryRestController
@RestController
public class SnpLocationController {


    private final PagedResourcesAssembler snpAssembler;

    private final SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;


    @Autowired
    public SnpLocationController(SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository, PagedResourcesAssembler snpAssembler){
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
        this.snpAssembler = snpAssembler;
    }

    @CrossOrigin
    @RequestMapping(value = "/api/snpLocation/{range}",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> search(@PathVariable String range,
                                    @PageableDefault(size = 20, page = 0) Pageable pageable) {

        String chrom = range.split(":")[0];
        String locs = range.split(":")[1];

        int start = Integer.parseInt(locs.split("-")[0]);
        int end = Integer.parseInt(locs.split("-")[1]);

        Page<SingleNucleotidePolymorphism>
                snps =
                singleNucleotidePolymorphismRepository.findByLocationsChromosomeNameAndLocationsChromosomePositionBetween(
                        chrom,
                        start,
                        end,
                        pageable);

        return new ResponseEntity(snpAssembler.toResource(snps), HttpStatus.OK);

//        PagedResources<Resource<SingleNucleotidePolymorphism>> resource = snpAssembler.toResource(snps, (ResourceAssembler) resourceAssembler);
//        return new ResponseEntity(resource, HttpStatus.OK);

    }
}
