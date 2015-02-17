package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationDocument;
import uk.ac.ebi.spot.goci.model.DiseaseTraitDocument;
import uk.ac.ebi.spot.goci.model.EfoDocument;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.StudyDocument;

import java.util.HashSet;
import java.util.Set;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 13/02/15
 */
@Service
public class TraitEnrichmentService implements DocumentEnrichmentService<DiseaseTraitDocument> {
    private StudyService studyService;
    private AssociationService associationService;
    private TraitService traitService;

    @Autowired
    public TraitEnrichmentService(StudyService studyService,
                                  AssociationService associationService,
                                  TraitService traitService) {
        this.studyService = studyService;
        this.associationService = associationService;
        this.traitService = traitService;
    }

    @Override public int getPriority() {
        return 3;
    }

    @Override public void doEnrichment(DiseaseTraitDocument document) {
        long id = Long.valueOf(document.getId().split(":")[1]);

        studyService.findByDiseaseTraitId(id).forEach(
                study -> {
                    document.embed(new StudyDocument(study));
                    // collect unique efo traits by study
                    Set<EfoTrait> efoTraits = new HashSet<>();
                    traitService.findMappedTraitByStudyId(study.getId()).forEach(efoTraits::add);
                    // iterate over unique efo traits
                    efoTraits.forEach(
                            trait -> {
                                // embed efo trait info in disease trait document
                                document.embed(new EfoDocument(trait));
                                // and embed all associations mapped to this efo trait
                                associationService.findPublishedAssociationsByEfoTraitId(trait.getId()).forEach(
                                        association -> document.embed(new AssociationDocument(association)));
                            });
                });
    }
}
