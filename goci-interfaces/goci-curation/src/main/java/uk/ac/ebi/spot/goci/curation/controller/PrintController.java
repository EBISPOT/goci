package uk.ac.ebi.spot.goci.curation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationTableView;
import uk.ac.ebi.spot.goci.curation.service.StudyPrintService;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AncestryRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * Created by emma on 17/04/2015.
 *
 * @author emma
 *         <p>
 *         Controller used to create a printable view of a study emtry.
 */
@Controller
public class PrintController {

    private StudyRepository studyRepository;
    private AncestryRepository ancestryRepository;
    private StudyPrintService studyPrintService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public PrintController(StudyRepository studyRepository,
                           AncestryRepository ancestryRepository,
                           StudyPrintService studyPrintService) {
        this.studyRepository = studyRepository;
        this.ancestryRepository = ancestryRepository;
        this.studyPrintService = studyPrintService;
    }

    // View a study
    @RequestMapping(value = "/studies/{studyId}/printview",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public Callable<String> viewPrintableDetailsOfStudy(Model model, @PathVariable Long studyId) {

        return () -> {
            // Get relevant study details
            Study studyToView = studyRepository.findOne(studyId);

            // Get association information
            Collection<SnpAssociationTableView> snpAssociationTableViews = studyPrintService.generatePrintView(studyId);

            // Get housekeeping and ancestry information
            Housekeeping housekeeping = studyToView.getHousekeeping();
            String initialSampleDescription = studyToView.getInitialSampleSize();
            String replicateSampleDescription = studyToView.getReplicateSampleSize();

            model.addAttribute("study", studyToView);
            model.addAttribute("housekeeping", housekeeping);
            model.addAttribute("initialSampleDescription", initialSampleDescription);
            model.addAttribute("replicateSampleDescription", replicateSampleDescription);

            // Two types of ancestry information which the view needs to form two different tables
            model.addAttribute("initialStudyAncestryDescriptions",
                               ancestryRepository.findByStudyIdAndType(studyId, "initial"));
            model.addAttribute("replicationStudyAncestryDescriptions",
                               ancestryRepository.findByStudyIdAndType(studyId,
                                                                       "replication"));

            model.addAttribute("snpAssociationTableViews", snpAssociationTableViews);
            return "study_printview";
        };
    }
}