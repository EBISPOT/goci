package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationDocument;
import uk.ac.ebi.spot.goci.model.DiseaseTraitDocument;
import uk.ac.ebi.spot.goci.model.EfoDocument;
import uk.ac.ebi.spot.goci.model.StudyDocument;

import java.util.Collection;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 13/02/15
 */
@Service
public class StudyEnrichmentService implements DocumentEnrichmentService<StudyDocument> {
    private AssociationService associationService;
    private TraitService traitService;

    @Autowired
    public StudyEnrichmentService(AssociationService associationService,
                                  TraitService traitService) {
        this.associationService = associationService;
        this.traitService = traitService;
    }

    @Override public int getPriority() {
        return 1;
    }

    @Override public void doEnrichment(StudyDocument document) {
        long id = Long.valueOf(document.getId().split(":")[1]);

        Collection<Association> associations = associationService.findPublishedAssociationsByStudyId(id);
        document.setAssociationCount(associations.size());
        associations.forEach(association -> document.embed(new AssociationDocument(association)));

        traitService.findReportedTraitByStudyId(id).forEach(
                trait -> document.embed(new DiseaseTraitDocument(trait)));
        traitService.findMappedTraitByStudyId(id).forEach(
                trait -> document.embed(new EfoDocument(trait)));
    }
}
