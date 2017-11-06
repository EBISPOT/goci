package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Ancestry;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AncestryRepository;

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
    private LociAttributesService lociAttributesService;
    private AssociationService associationService;

    @Autowired
    public DataDeletionService(StudyService studyService,
                               AncestryRepository ancestryRepository,
                               CuratorTrackingService curatorTrackingService,
                               WeeklyTrackingService weeklyTrackingService,
                               LociAttributesService lociAttributesService,
                               AssociationService associationService){
        this.studyService = studyService;
        this.ancestryRepository = ancestryRepository;
        this.lociAttributesService = lociAttributesService;
        this.associationService = associationService;
    }

    public void deleteNonPublicStudies(){

        List<Study> unpublishedStudies = studyService.deepFindUnPublishedStudies();

        getLog().info("Found " + unpublishedStudies.size() + " unpublished studies to be removed");


        unpublishedStudies.forEach(this::deleteStudy);

        getLog().info("Study deletion process successfully completed");
    }

    private void deleteStudy(Study study) {
        System.out.println("Removing study \t" + study.getPublicationId().getFirstAuthor().getFullname() + "\t (ID:" + study.getId() + ") with \t"
                               + study.getAssociations().size() + "\t association and \t"
                               + study.getAncestries().size() + "\t ancestries");
        getLog().debug("Removing study \t" + study.getPublicationId().getFirstAuthor().getFullname() + "\t (ID:" + study.getId() + ") with \t"
                               + study.getAssociations().size() + "\t association and \t"
                                + study.getAncestries().size() + "\t ancestries");


        Collection<Association> associations = study.getAssociations();

        associations.forEach(this::deleteAssociation);

        Collection<Ancestry> ancestries = ancestryRepository.findByStudyId(study.getId());

        for (Ancestry ancestry : ancestries) {
            ancestryRepository.delete(ancestry);
        }

        // WeeklyTracking, CuratorTracking and Note. Please use this method!
        // Shared with === DataDeletionService ===
        studyService.deleteRelatedInfoByStudy(study);

        // Delete study
        studyService.deleteByStudyId(study.getId());


    }

    public void deleteAssociation(Association association) {

        getLog().info("Deleting association ".concat(String.valueOf(association.getId())));
        //delete all the loci and risk alleles that are dependent on this association
        lociAttributesService.deleteLocusAndRiskAlleles(association);

        // Delete association
         associationService.deleteByAssociationId(association.getId());
    }



}
