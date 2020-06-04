package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationDocument;
import uk.ac.ebi.spot.goci.model.DiseaseTraitDocument;
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
    private final DiseaseTraitDocumentCache diseaseTraitCache;
    private final StudyDocumentCache studyCache;
    private StudyService studyService;
    private TraitService traitService;
    private EfoDocumentCache efoCache;

    @Autowired
    public AssociationEnrichmentService(StudyService studyService, TraitService traitService,
                                        EfoDocumentCache efoCache, DiseaseTraitDocumentCache diseaseTraitCache,
                                        StudyDocumentCache studyCache) {
        this.studyService = studyService;
        this.traitService = traitService;
        this.efoCache = efoCache;
        this.diseaseTraitCache = diseaseTraitCache;
        this.studyCache = studyCache;
    }

    @Override public int getPriority() {
        return 2;
    }

    @Override public void doEnrichment(AssociationDocument document) {
        long id = Long.valueOf(document.getId().split(":")[1]);

        studyService.findByAssociationId(id).forEach(
                study -> {
                    StudyDocument studyDocument = studyCache.getDocument(study.getId());
                    if(studyDocument == null){
                        studyDocument = new StudyDocument(study);
                        studyCache.addDocument(study.getId(), studyDocument);
                    }
                    document.addStudyId(String.valueOf(study.getId()));
                    document.embed(studyDocument);
                });

//        traitService.findReportedTraitByAssociationId(id).forEach(
//                trait -> document.embed(new DiseaseTraitDocument(trait)));
        traitService.findReportedTraitByAssociationId(id).forEach(
                trait -> document.embed(diseaseTraitCache.getDocument(trait.getTrait())));

//        traitService.findMappedTraitByAssociationId(id).forEach(
//                trait -> document.embed(new EfoDocument(trait)));
        traitService.findMappedTraitNamesByAssociationId(id).forEach(
                trait -> document.embed(efoCache.getDocument(trait)));

    }
}
