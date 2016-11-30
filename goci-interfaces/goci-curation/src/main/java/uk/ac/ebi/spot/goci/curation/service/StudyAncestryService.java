package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.CountryOfOrigin;
import uk.ac.ebi.spot.goci.curation.model.CountryOfRecruitment;
import uk.ac.ebi.spot.goci.curation.model.AncestralGroup;
import uk.ac.ebi.spot.goci.model.DeletedAncestry;
import uk.ac.ebi.spot.goci.model.Ancestry;
import uk.ac.ebi.spot.goci.model.Event;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.DeletedAncestryRepository;
import uk.ac.ebi.spot.goci.repository.AncestryRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.TrackingOperationService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by emma on 05/08/2016.
 *
 * @author emma
 *         <p>
 *         Service class that handles common operations performed on ancestry
 */
@Service
public class StudyAncestryService {

    private AncestryRepository ancestryRepository;
    private StudyRepository studyRepository;
    private TrackingOperationService trackingOperationService;
    private DeletedAncestryRepository deletedAncestryRepository;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public StudyAncestryService(AncestryRepository ancestryRepository,
                                StudyRepository studyRepository,
                                @Qualifier("ancestryTrackingOperationServiceImpl") TrackingOperationService trackingOperationService,
                                DeletedAncestryRepository deletedAncestryRepository) {
        this.ancestryRepository = ancestryRepository;
        this.studyRepository = studyRepository;
        this.trackingOperationService = trackingOperationService;
        this.deletedAncestryRepository = deletedAncestryRepository;
    }

    public void addAncestry(Long studyId, Ancestry ancestry, SecureUser user) {
        Study study = studyRepository.findOne(studyId);

        // Set default values when no country of origin or recruitment supplied
        if (ancestry.getCountryOfOrigin() == null) {
            ancestry.setCountryOfOrigin("NR");
        }

        if (ancestry.getCountryOfOrigin() != null && ancestry.getCountryOfOrigin().isEmpty()) {
            ancestry.setCountryOfOrigin("NR");
        }

        if (ancestry.getCountryOfRecruitment() == null) {
            ancestry.setCountryOfRecruitment("NR");
        }

        if (ancestry.getCountryOfRecruitment() != null && ancestry.getCountryOfRecruitment().isEmpty()) {
            ancestry.setCountryOfRecruitment("NR");
        }

        // Set the study for our ancestry and save
        ancestry.setStudy(study);
        trackingOperationService.create(ancestry, user);
        ancestryRepository.save(ancestry);
        getLog().info("Ancestry ".concat(ancestry.getId().toString()).concat(" created"));
    }


    public void updateAncestry(Ancestry ancestry,
                                CountryOfOrigin countryOfOrigin,
                                CountryOfRecruitment countryOfRecruitment,
                                AncestralGroup ancestralGroup,
                                SecureUser user) {

        // Set country of origin based on values returned
        List<String> listOfOriginCountries = Arrays.asList(countryOfOrigin.getOriginCountryValues());

        if (listOfOriginCountries.size() > 0) {
            String countryOfOriginJoined = String.join(",", listOfOriginCountries);
            ancestry.setCountryOfOrigin(countryOfOriginJoined);
        }
        else {
            ancestry.setCountryOfOrigin("NR");
        }

        // Set country of recruitment based on values returned
        List<String> listOfRecruitmentCountries = Arrays.asList(countryOfRecruitment.getRecruitmentCountryValues());

        if (listOfRecruitmentCountries.size() > 0) {
            String countryOfRecruitmentJoined = String.join(",", listOfRecruitmentCountries);
            ancestry.setCountryOfRecruitment(countryOfRecruitmentJoined);
        }
        else {
            ancestry.setCountryOfRecruitment("NR");
        }

        // Set ancestral group
        List<String> listOfAncestralGroups = Arrays.asList(ancestralGroup.getAncestralGroupValues());
        String ancestralGroupJoined = String.join(",", listOfAncestralGroups);
        ancestry.setAncestralGroup(ancestralGroupJoined);

        // Saves the new information returned from form
        trackingOperationService.update(ancestry, user, "ANCESTRY_UPDATED");
        ancestryRepository.save(ancestry);
        getLog().info("Ancestry ".concat(ancestry.getId().toString()).concat(" updated"));
    }

    public void deleteAll(Long studyId, SecureUser user) {
        // Get all study ancestry's
        Study study = studyRepository.findOne(studyId);
        Collection<Ancestry> studyAncestry = ancestryRepository.findByStudyId(studyId);

        // Delete ancestry
        studyAncestry.forEach(ancestry -> deleteAncestry(ancestry, user));
    }

    public void deleteChecked(Collection<Ancestry> studyAncestry, SecureUser user) {
        // Delete ancestry
        studyAncestry.forEach(ancestry -> deleteAncestry(ancestry, user));
    }

    private void deleteAncestry(Ancestry ancestry, SecureUser user) {
        getLog().warn("Deleting ancestry: ".concat(String.valueOf(ancestry.getId())));

        // Add deletion event
        trackingOperationService.delete(ancestry, user);
        DeletedAncestry deletedAncestry = createDeletedAncestry(ancestry);

        // Delete ancestry
        ancestryRepository.delete(ancestry);

        // Save deleted details
        getLog().info("Saving details of deleted ancestry: ".concat(String.valueOf(deletedAncestry.getId())));
        deletedAncestryRepository.save(deletedAncestry);
    }

    private DeletedAncestry createDeletedAncestry(Ancestry ancestry) {
        Collection<Event> events = ancestry.getEvents();
        Long id = ancestry.getId();
        Long studyId = ancestry.getStudy().getId();
        return new DeletedAncestry(id, studyId, events);
    }
}