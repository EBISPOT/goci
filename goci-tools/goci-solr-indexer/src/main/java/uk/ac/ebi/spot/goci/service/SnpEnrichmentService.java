package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationDocument;
import uk.ac.ebi.spot.goci.model.EfoDocument;
import uk.ac.ebi.spot.goci.model.MappedGeneDocument;
import uk.ac.ebi.spot.goci.model.ReportedGeneDocument;
import uk.ac.ebi.spot.goci.model.SnpDocument;
import uk.ac.ebi.spot.goci.model.StudyDocument;
import uk.ac.ebi.spot.goci.model.TraitDocument;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 13/02/15
 */
@Service
public class SnpEnrichmentService implements DocumentEnrichmentService<SnpDocument> {
    private StudyService studyService;
    private AssociationService associationService;
    private GeneService geneService;
    private TraitService traitService;

    @Autowired
    public SnpEnrichmentService(StudyService studyService,
                                AssociationService associationService,
                                GeneService geneService,
                                TraitService traitService) {
        this.studyService = studyService;
        this.associationService = associationService;
        this.geneService = geneService;
        this.traitService = traitService;
    }

    @Override public int getPriority() {
        return 1;
    }

    @Override public void doEnrichment(SnpDocument document) {
        long id = Long.valueOf(document.getId().split(":")[1]);

        studyService.findBySnpId(id).forEach(
                study -> document.embed(new StudyDocument(study)));
        associationService.findPublishedAssociationsBySnpId(id).forEach(
                association -> document.embed(new AssociationDocument(association)));
        geneService.findReportedGenesBySnpId(id).forEach(
                gene -> document.embed(new ReportedGeneDocument(gene)));
        geneService.findMappedGenesBySnpId(id).forEach(
                gene -> document.embed(new MappedGeneDocument(gene)));
        traitService.findMappedTraitBySnpId(id).forEach(
                trait -> document.embed(new EfoDocument(trait)));
    }
}
