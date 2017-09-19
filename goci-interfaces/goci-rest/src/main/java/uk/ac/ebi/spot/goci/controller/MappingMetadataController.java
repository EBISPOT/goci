package uk.ac.ebi.spot.goci.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.spot.goci.model.MappingMetadata;
import uk.ac.ebi.spot.goci.repository.MappingMetadataRepository;

import java.util.Collection;

/**
 * Created by dwelter on 19/09/17.
 */
@RestController
public class MappingMetadataController {

    private final MappingMetadataRepository mappingMetadataRepository;


    @Autowired
    public MappingMetadataController(MappingMetadataRepository mappingMetadataRepository){
        this.mappingMetadataRepository = mappingMetadataRepository;
    }

    @CrossOrigin
    @RequestMapping(value = "/api/metadata",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<?> getData() {


        Collection<MappingMetadata> latest = mappingMetadataRepository.getLatestMapping();

        Resources<MappingMetadata> resources = new Resources<MappingMetadata>(latest);

        return ResponseEntity.ok(resources);

    }

}
