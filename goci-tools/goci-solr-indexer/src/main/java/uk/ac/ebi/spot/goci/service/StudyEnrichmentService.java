package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.SnpDocument;
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
    private SingleNucleotidePolymorphismService snpService;
    private GeneService geneService;

    @Autowired
    public StudyEnrichmentService(SingleNucleotidePolymorphismService snpService,
                                  GeneService geneService) {
        this.snpService = snpService;
        this.geneService = geneService;
    }

    @Override public int getPriority() {
        return 1;
    }

    @Override public void doEnrichment(StudyDocument document) {
        long studyId = Long.valueOf(document.getId().split(":")[1]);

        // find snps and embed
        snpService.deepFindByStudyId(studyId).forEach(snp -> document.embed(new SnpDocument(snp)));
    }
}
