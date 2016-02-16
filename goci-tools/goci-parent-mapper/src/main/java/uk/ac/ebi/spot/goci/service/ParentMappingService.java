package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Trait;
import uk.ac.ebi.spot.goci.ontology.owl.OntologyLoader;

import java.util.List;
import java.util.Map;

/**
 * Created by Dani on 15/02/2016.
 */

@Service
public class ParentMappingService {


    @Autowired
    private OntologyLoader ontologyLoader;

    public List<Trait> mapTraits(Map<String, List<Trait>> unmappedTraits) {
        return null;
    }
}
