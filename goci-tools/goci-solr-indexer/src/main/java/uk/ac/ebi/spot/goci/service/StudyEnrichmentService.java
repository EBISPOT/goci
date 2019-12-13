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
    private final EfoDocumentCache efoCache;
    private final DiseaseTraitDocumentCache diseaseTraitCache;
    private final StudyDocumentCache studyDocumentCache;
    private AssociationService associationService;
    private TraitService traitService;

    @Autowired
    public StudyEnrichmentService(AssociationService associationService,
                                  TraitService traitService, EfoDocumentCache efoCache,
                                  DiseaseTraitDocumentCache diseaseTraitCache, StudyDocumentCache studyDocumentCache) {
        this.associationService = associationService;
        this.traitService = traitService;
        this.efoCache = efoCache;
        this.diseaseTraitCache = diseaseTraitCache;
        this.studyDocumentCache = studyDocumentCache;
    }

    @Override public int getPriority() {
        return 1;
    }

    @Override public void doEnrichment(StudyDocument document) {
        long id = Long.valueOf(document.getId().split(":")[1]);
        if(!studyDocumentCache.hasDocument(id)){
            studyDocumentCache.addDocument(id, document);
        }

        Collection<Association> associations = associationService.findPublishedAssociationsByStudyId(id);
        document.setAssociationCount(associations.size());
        associations.forEach(association -> document.embed(new AssociationDocument(association)));

        traitService.findReportedTraitByStudyId(id).forEach(
                trait -> document.embed(diseaseTraitCache.getDocument(trait.getTrait())));
        traitService.findMappedTraitByStudyId(id).forEach(
                trait -> document.embed(efoCache.getDocument(trait.getTrait())));
    }
}
