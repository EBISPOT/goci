package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.CountryOfOrigin;
import uk.ac.ebi.spot.goci.curation.model.CountryOfRecruitment;
import uk.ac.ebi.spot.goci.curation.model.EthnicGroup;
import uk.ac.ebi.spot.goci.curation.service.tracking.TrackingOperationService;
import uk.ac.ebi.spot.goci.model.Ethnicity;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.EthnicityRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.Arrays;
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

    @Autowired
    public StudyEthnicityService(EthnicityRepository ethnicityRepository,
                                 StudyRepository studyRepository,
                                 @Qualifier("EthnicityTrackingOperationServiceImpl") TrackingOperationService trackingOperationService) {
        this.ethnicityRepository = ethnicityRepository;
        this.studyRepository = studyRepository;
        this.trackingOperationService = trackingOperationService;
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
    }


}
