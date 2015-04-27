package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.curation.model.StudySampleDescription;
import uk.ac.ebi.spot.goci.curation.service.StudySampleDescriptionsDownloadService;
import uk.ac.ebi.spot.goci.model.Ethnicity;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.EthnicityRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by emma on 24/04/2015.
 *
 * @author emma
 *         <p>
 *         Controller designed to handle the download of all studies with sample description and ethnicity data
 */
@Controller
@RequestMapping("/sampledescriptions")
public class StudySampleDesciptionsController {

    // Repositories allowing access to database objects associated with a study
    private EthnicityRepository ethnicityRepository;
    private StudySampleDescriptionsDownloadService studySampleDescriptionsDownloadService;

    @Autowired
    public StudySampleDesciptionsController(EthnicityRepository ethnicityRepository,
                                            StudySampleDescriptionsDownloadService studySampleDescriptionsDownloadService) {
        this.ethnicityRepository = ethnicityRepository;
        this.studySampleDescriptionsDownloadService = studySampleDescriptionsDownloadService;
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public void getStudiesSampleDescriptions(HttpServletResponse response, Model model) {

        // Get all ethnicities, this will also find all studies with ethnicity information
        Collection<Ethnicity> ethnicities = ethnicityRepository.findAll();
        Collection<StudySampleDescription> studySampleDescriptions = new ArrayList<>();

        for (Ethnicity ethnicity : ethnicities) {

            if (ethnicity.getStudy() != null) {

                Study study = ethnicity.getStudy();

                // Study attributes
                String author = study.getAuthor();
                Date studyDate = study.getStudyDate();
                String pubmedId = study.getPubmedId();
                String initialSampleSize = study.getInitialSampleSize();
                String replicateSampleSize = study.getReplicateSampleSize();

                // Housekeeping attributes
                Boolean ethnicityCheckedLevelOne = false;
                Boolean ethnicityCheckedLevelTwo = false;

                if (study.getHousekeeping() != null) {
                    ethnicityCheckedLevelOne = study.getHousekeeping().getEthnicityCheckedLevelOne();
                    ethnicityCheckedLevelTwo = study.getHousekeeping().getEthnicityCheckedLevelTwo();

                }

                // Ethnicity attributes
                String type = ethnicity.getType();
                Integer numberOfIndividuals = ethnicity.getNumberOfIndividuals();
                String ethnicGroup = ethnicity.getEthnicGroup();
                String countryOfOrigin = ethnicity.getCountryOfOrigin();
                String countryOfRecruitment = ethnicity.getCountryOfRecruitment();
                String sampleSizesMatch = ethnicity.getSampleSizesMatch();
                String description = ethnicity.getDescription();
                String notes = ethnicity.getNotes();

                StudySampleDescription studySampleDescription = new StudySampleDescription(author,
                                                                                           studyDate,
                                                                                           pubmedId,
                                                                                           initialSampleSize,
                                                                                           replicateSampleSize,
                                                                                           ethnicityCheckedLevelOne,
                                                                                           ethnicityCheckedLevelTwo,
                                                                                           type,
                                                                                           numberOfIndividuals,
                                                                                           ethnicGroup,
                                                                                           countryOfOrigin,
                                                                                           countryOfRecruitment,
                                                                                           description,
                                                                                           sampleSizesMatch,
                                                                                           notes);

                studySampleDescriptions.add(studySampleDescription);
            }

        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String now = dateFormat.format(date);

        String fileName = "GWASEthnicity".concat("-").concat(now).concat(".tsv");
        response.setContentType("text/tsv");
        response.setHeader("Content-Disposition", "attachement; filename=" + fileName);

        try {
            studySampleDescriptionsDownloadService.createDownloadFile(response.getOutputStream(),
                                                                      studySampleDescriptions);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
