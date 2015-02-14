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
public class StudyEnrichmentService implements DocumentEnrichmentService<StudyDocument> {
    private AssociationService associationService;
    private SingleNucleotidePolymorphismService snpService;
    private GeneService geneService;
    private TraitService traitService;

    @Autowired
    public StudyEnrichmentService(AssociationService associationService,
                                  SingleNucleotidePolymorphismService snpService,
                                  GeneService geneService,
                                  TraitService traitService) {
        this.associationService = associationService;
        this.snpService = snpService;
        this.geneService = geneService;
        this.traitService = traitService;
    }

    @Override public int getPriority() {
        return 2;
    }

    @Override public void doEnrichment(StudyDocument document) {
        long id = Long.valueOf(document.getId().split(":")[1]);

        associationService.findPublishedAssociationsByStudyId(id).forEach(
                association -> document.embed(new AssociationDocument(association)));
        snpService.findByStudyId(id).forEach(
                snp -> document.embed(new SnpDocument(snp)));
        geneService.findReportedGenesByStudyId(id).forEach(
                gene -> document.embed(new ReportedGeneDocument(gene)));
        geneService.findMappedGenesByStudyId(id).forEach(
                gene -> document.embed(new MappedGeneDocument(gene)));
        traitService.findReportedTraitByStudyId(id).forEach(
                trait -> document.embed(new TraitDocument(trait)));
        traitService.findMappedTraitByStudyId(id).forEach(
                trait -> document.embed(new EfoDocument(trait)));
    }
}
