package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Ancestry;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AncestryRepository;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;
import uk.ac.ebi.spot.goci.repository.RiskAlleleRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by dwelter on 09/01/17.
 */

@Service
public class DataDeletionService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    private StudyService studyService;
    private AncestryRepository ancestryRepository;
    private StudyRepository studyRepository;
    private AssociationRepository associationRepository;
    private LocusRepository locusRepository;
    private RiskAlleleRepository riskAlleleRepository;

    @Autowired
    public DataDeletionService(StudyService studyService,
                               StudyRepository studyRepository,
                               AncestryRepository ancestryRepository,
                               AssociationRepository associationRepository,
                               LocusRepository locusRepository,
                               RiskAlleleRepository riskAlleleRepository){
        this.studyService = studyService;
        this.studyRepository = studyRepository;
        this.ancestryRepository = ancestryRepository;
        this.associationRepository = associationRepository;
        this.locusRepository = locusRepository;
        this.riskAlleleRepository = riskAlleleRepository;
    }

    public void deleteNonPublicStudies(){

        List<Study> unpublishedStudies = studyService.deepFindUnPublishedStudies();

        getLog().info("Found " + unpublishedStudies.size() + " unpublished studies to be removed");

        unpublishedStudies.forEach(this::deleteStudy);
    }

    private void deleteStudy(Study study) {

        Collection<Association> associations = study.getAssociations();

        associations.forEach(this::deleteAssociation);

        Collection<Ancestry> ancestries = study.getAncestries();

        ancestries.forEach(
                ancestry -> ancestryRepository.delete(ancestry)
        );

        studyRepository.delete(study);
    }


    private void deleteAssociation(Association association) {

        Collection<RiskAllele> riskAlleles = new ArrayList<>();
        for (Locus locus : association.getLoci()) {
            locus.getStrongestRiskAlleles().forEach(riskAlleles::add);
            locusRepository.delete(locus);
        }
        riskAlleles.forEach(riskAllele -> riskAlleleRepository.delete(riskAllele));

        associationRepository.delete(association);
    }
}
