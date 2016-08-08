package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.CountryOfOrigin;
import uk.ac.ebi.spot.goci.curation.model.CountryOfRecruitment;
import uk.ac.ebi.spot.goci.curation.model.EthnicGroup;
import uk.ac.ebi.spot.goci.curation.service.tracking.TrackingOperationService;
import uk.ac.ebi.spot.goci.model.DeletedEthnicity;
import uk.ac.ebi.spot.goci.model.Ethnicity;
import uk.ac.ebi.spot.goci.model.Event;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.DeletedEthnicityRepository;
import uk.ac.ebi.spot.goci.repository.EthnicityRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by emma on 05/08/2016.
 *
 * @author emma
 *         <p>
 *         Service class that handles common operations performed on ethnicity
 */
@Service
public class StudyEthnicityService {

    private EthnicityRepository ethnicityRepository;
    private StudyRepository studyRepository;
    private TrackingOperationService trackingOperationService;
    private DeletedEthnicityRepository deletedEthnicityRepository;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public StudyEthnicityService(EthnicityRepository ethnicityRepository,
                                 StudyRepository studyRepository,
                                 @Qualifier("ethnicityTrackingOperationServiceImpl") TrackingOperationService trackingOperationService,
                                 DeletedEthnicityRepository deletedEthnicityRepository) {
        this.ethnicityRepository = ethnicityRepository;
        this.studyRepository = studyRepository;
        this.trackingOperationService = trackingOperationService;
        this.deletedEthnicityRepository = deletedEthnicityRepository;
    }

    public void addEthnicity(Long studyId, Ethnicity ethnicity, SecureUser user) {
        Study study = studyRepository.findOne(studyId);

        // Set default values when no country of origin or recruitment supplied
        if (ethnicity.getCountryOfOrigin() == null) {
            ethnicity.setCountryOfOrigin("NR");
        }

        if (ethnicity.getCountryOfOrigin() != null && ethnicity.getCountryOfOrigin().isEmpty()) {
            ethnicity.setCountryOfOrigin("NR");
        }

        if (ethnicity.getCountryOfRecruitment() == null) {
            ethnicity.setCountryOfRecruitment("NR");
        }

        if (ethnicity.getCountryOfRecruitment() != null && ethnicity.getCountryOfRecruitment().isEmpty()) {
            ethnicity.setCountryOfRecruitment("NR");
        }

        // Set the study for our ethnicity and save
        ethnicity.setStudy(study);
        trackingOperationService.create(ethnicity, user);
        ethnicityRepository.save(ethnicity);
        getLog().info("Ethnicity ".concat(ethnicity.getId().toString()).concat(" created"));
    }


    public void updateEthnicity(Ethnicity ethnicity,
                                CountryOfOrigin countryOfOrigin,
                                CountryOfRecruitment countryOfRecruitment,
                                EthnicGroup ethnicGroup,
                                SecureUser user) {

        // Set country of origin based on values returned
        List<String> listOfOriginCountries = Arrays.asList(countryOfOrigin.getOriginCountryValues());

        if (listOfOriginCountries.size() > 0) {
            String countryOfOriginJoined = String.join(",", listOfOriginCountries);
            ethnicity.setCountryOfOrigin(countryOfOriginJoined);
        }
        else {
            ethnicity.setCountryOfOrigin("NR");
        }

        // Set country of recruitment based on values returned
        List<String> listOfRecruitmentCountries = Arrays.asList(countryOfRecruitment.getRecruitmentCountryValues());

        if (listOfRecruitmentCountries.size() > 0) {
            String countryOfRecruitmentJoined = String.join(",", listOfRecruitmentCountries);
            ethnicity.setCountryOfRecruitment(countryOfRecruitmentJoined);
        }
        else {
            ethnicity.setCountryOfRecruitment("NR");
        }

        // Set ethnic group
        List<String> listOfEthnicGroups = Arrays.asList(ethnicGroup.getEthnicGroupValues());
        String ethnicGroupJoined = String.join(",", listOfEthnicGroups);
        ethnicity.setEthnicGroup(ethnicGroupJoined);

        // Saves the new information returned from form
        trackingOperationService.update(ethnicity, user, EventType.ETHNICITY_UPDATED);
        ethnicityRepository.save(ethnicity);
        getLog().info("Ethnicity ".concat(ethnicity.getId().toString()).concat(" updated"));
    }

    @Async
    public void deleteAll(Long studyId, SecureUser user) {
        // Get all study ethnicity's
        Study study = studyRepository.findOne(studyId);
        Collection<Ethnicity> studyEthnicity = ethnicityRepository.findByStudyId(studyId);

        // Delete ethnicity
        studyEthnicity.forEach(ethnicity -> deleteEthnicity(ethnicity, user));
    }

    @Async
    public void deleteChecked(Collection<Ethnicity> studyEthnicity, SecureUser user) {
        // Delete ethnicity
        studyEthnicity.forEach(ethnicity -> deleteEthnicity(ethnicity, user));
    }

    private void deleteEthnicity(Ethnicity ethnicity, SecureUser user) {
        getLog().warn("Deleting ethnicity: ".concat(String.valueOf(ethnicity.getId())));

        // Add deletion event
        trackingOperationService.delete(ethnicity, user);
        DeletedEthnicity deletedEthnicity = createDeletedEthnicity(ethnicity);

        // Delete ethnicity
        ethnicityRepository.delete(ethnicity);

        // Save deleted details
        getLog().info("Saving details of deleted ethnicity: ".concat(String.valueOf(deletedEthnicity.getId())));
        deletedEthnicityRepository.save(deletedEthnicity);
    }

    private DeletedEthnicity createDeletedEthnicity(Ethnicity ethnicity) {
        Collection<Event> events = ethnicity.getEvents();
        Long id = ethnicity.getId();
        Long studyId = ethnicity.getStudy().getId();
        return new DeletedEthnicity(id, studyId, events);
    }
}