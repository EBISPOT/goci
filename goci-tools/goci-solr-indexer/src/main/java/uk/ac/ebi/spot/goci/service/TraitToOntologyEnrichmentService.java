package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.TraitDocument;

import java.util.Collection;
import java.util.HashSet;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 28/01/15
 */
@Service
public class TraitToOntologyEnrichmentService implements DocumentEnrichmentService<TraitDocument> {
    private StudyService studyService;

    @Autowired
    public TraitToOntologyEnrichmentService(StudyService studyService) {
        this.studyService = studyService;
    }

    @Override public int getPriority() {
        return 1;
    }

    @Override public void doEnrichment(TraitDocument document) {
        Collection<Study> studies = studyService.deepFindByDiseaseTraitId(Long.valueOf(document.getId().split(":")[1]));

        Collection<EfoTrait> efoTraits = new HashSet<>();
        studies.stream().map(Study::getEfoTraits).forEach(efoTraits::addAll);
        for (EfoTrait efoTrait : efoTraits) {
            document.addTraitUri(efoTrait.getUri());
        }
    }
}
