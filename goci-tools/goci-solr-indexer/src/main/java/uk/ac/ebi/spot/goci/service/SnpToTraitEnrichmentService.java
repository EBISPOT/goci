package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.SnpDocument;

import java.util.Collection;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 04/02/15
 */
@Service
public class SnpToTraitEnrichmentService implements DocumentEnrichmentService<SnpDocument> {
    private AssociationService associationService;

    @Autowired
    public SnpToTraitEnrichmentService(AssociationService associationService) {
        this.associationService = associationService;
    }

    @Override public int getPriority() {
        return 1;
    }

    @Override public void doEnrichment(SnpDocument document) {
        Collection<Association> associations = associationService
                .deepFindBySingleNucleotidePolymorphismId(Long.valueOf(document.getId().split(":")[1]));
        associations.forEach(association -> {
            if (association.getPvalueText() != null) {
                document.addQualifier(association.getPvalueText());
            }
            association.getEfoTraits().forEach(trait -> document.addTraitUri(trait.getUri()));
        });
    }

}
