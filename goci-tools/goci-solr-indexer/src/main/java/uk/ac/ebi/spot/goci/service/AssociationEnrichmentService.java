package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationDocument;
import uk.ac.ebi.spot.goci.model.EfoDocument;
import uk.ac.ebi.spot.goci.model.StudyDocument;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 13/02/15
 */
@Service
public class AssociationEnrichmentService implements DocumentEnrichmentService<AssociationDocument> {
    private StudyService studyService;
    private TraitService traitService;

    @Autowired
    public AssociationEnrichmentService(StudyService studyService, TraitService traitService) {
        this.studyService = studyService;
        this.traitService = traitService;
    }

    @Override public int getPriority() {
        return 1;
    }

    @Override public void doEnrichment(AssociationDocument document) {
        long id = Long.valueOf(document.getId().split(":")[1]);

        studyService.findByAssociationId(id).forEach(
                study -> document.embed(new StudyDocument(study)));
        traitService.findMappedTraitByAssociationId(id).forEach(
                trait -> document.embed(new EfoDocument(trait)));
    }
}
