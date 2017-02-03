package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AncestryRepository;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;
import uk.ac.ebi.spot.goci.repository.RiskAlleleRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

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
    private StudyRepository studyRepository;

    @Autowired
    public DataDeletionService(StudyService studyService,
                               StudyRepository studyRepository,
                               AncestryRepository ancestryRepository,
                               AssociationRepository associationRepository,
                               LocusRepository locusRepository,
                               RiskAlleleRepository riskAlleleRepository){
        this.studyService = studyService;
        this.studyRepository = studyRepository;
    }

    public void deleteNonPublicStudies(){

        List<Study> unpublishedStudies = studyService.deepFindUnPublishedStudies();

        getLog().info("Found " + unpublishedStudies.size() + " unpublished studies to be removed");


        unpublishedStudies.forEach(this::deleteStudy);

        getLog().info("Study deletion process successfully completed");
    }

    private void deleteStudy(Study study) {
        System.out.println("Removing study \t" + study.getAuthor() + "\t (ID:" + study.getId() + ") with \t"
                               + study.getAssociations().size() + "\t association and \t"
                               + study.getAncestries().size() + "\t ancestries");
        getLog().debug("Removing study \t" + study.getAuthor() + "\t (ID:" + study.getId() + ") with \t"
                               + study.getAssociations().size() + "\t association and \t"
                                + study.getAncestries().size() + "\t ancestries");

        studyRepository.delete(study);

    }


}
