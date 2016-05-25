package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.Date;

/**
 * Created by emma on 25/05/2016.
 *
 * @author emma
 *         <p>
 *         Service class that handles common operations performed on study housekeeping
 */
@Service
public class HousekeepingOperationsService {

    private HousekeepingRepository housekeepingRepository;
    private CuratorRepository curatorRepository;
    private CurationStatusRepository curationStatusRepository;
    private StudyRepository studyRepository;

    @Autowired
    public HousekeepingOperationsService(HousekeepingRepository housekeepingRepository,
                                         CuratorRepository curatorRepository,
                                         CurationStatusRepository curationStatusRepository,
                                         StudyRepository studyRepository) {
        this.housekeepingRepository = housekeepingRepository;
        this.curatorRepository = curatorRepository;
        this.curationStatusRepository = curationStatusRepository;
        this.studyRepository = studyRepository;
    }

    /**
     * Create study housekeeping
     */
    public Housekeeping createHousekeeping() {
        // Create housekeeping object and create the study added date
        Housekeeping housekeeping = new Housekeeping();
        java.util.Date studyAddedDate = new java.util.Date();
        housekeeping.setStudyAddedDate(studyAddedDate);

        // Set status
        CurationStatus status = curationStatusRepository.findByStatus("Awaiting Curation");
        housekeeping.setCurationStatus(status);

        // Set curator
        Curator curator = curatorRepository.findByLastName("Level 1 Curator");
        housekeeping.setCurator(curator);

        // Save housekeeping
        housekeepingRepository.save(housekeeping);

        // Save housekeeping
        return housekeeping;
    }

    /**
     * Save housekeeping
     *
     * @param housekeeping
     * @param study
     */
    public void saveHousekeeping(Study study, Housekeeping housekeeping) {
        // Save housekeeping returned from form
        housekeeping.setLastUpdateDate(new Date());
        housekeepingRepository.save(housekeeping);
        study.setHousekeeping(housekeeping);
        studyRepository.save(study);
    }
}
