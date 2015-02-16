package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationDocument;
import uk.ac.ebi.spot.goci.model.MappedGeneDocument;
import uk.ac.ebi.spot.goci.model.ReportedGeneDocument;
import uk.ac.ebi.spot.goci.model.SnpDocument;
import uk.ac.ebi.spot.goci.model.StudyDocument;
import uk.ac.ebi.spot.goci.model.DiseaseTraitDocument;

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
    private SingleNucleotidePolymorphismService snpService;
    private GeneService geneService;

    @Autowired
    public TraitEnrichmentService(StudyService studyService,
                                  AssociationService associationService,
                                  SingleNucleotidePolymorphismService snpService,
                                  GeneService geneService) {
        this.studyService = studyService;
        this.associationService = associationService;
        this.snpService = snpService;
        this.geneService = geneService;
    }

    @Override public int getPriority() {
        return 3;
    }

    @Override public void doEnrichment(DiseaseTraitDocument document) {
//        long id = Long.valueOf(document.getId().split(":")[1]);
//
//        studyService.findByDiseaseTraitId(id).forEach(
//                study -> document.embed(new StudyDocument(study)));
//        associationService.findPublishedAssociationsByDiseaseTraitId(id).forEach(
//                association -> document.embed(new AssociationDocument(association)));
//        snpService.findByDiseaseTraitId(id).forEach(
//                snp -> document.embed(new SnpDocument(snp)));
//        geneService.findReportedGenesByDiseaseTraitId(id).forEach(
//                gene -> document.embed(new ReportedGeneDocument(gene)));
//        geneService.findMappedGenesByDiseaseTraitId(id).forEach(
//                gene -> document.embed(new MappedGeneDocument(gene)));
    }
}
