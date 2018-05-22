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
import uk.ac.ebi.spot.goci.model.StudyNote;
import uk.ac.ebi.spot.goci.repository.AncestryRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.PublicationService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
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
    private PublicationService publicationService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public PrintController(StudyRepository studyRepository,
                           AncestryRepository ancestryRepository,
                           StudyPrintService studyPrintService,
                           PublicationService publicationService) {
        this.studyRepository = studyRepository;
        this.ancestryRepository = ancestryRepository;
        this.studyPrintService = studyPrintService;
        this.publicationService = publicationService;
    }


    protected ArrayList<Object> getStudyInfoToPrint(Study studyToPrint) {
        // Get relevant study details
        Long studyId = studyToPrint.getId();

        // Get association information
        Collection<SnpAssociationTableView> snpAssociationTableViews = studyPrintService.generatePrintView(studyId);

        // Get housekeeping and ancestry information
        Housekeeping housekeeping = studyToPrint.getHousekeeping();
        Collection<StudyNote> studyNotes = studyToPrint.getNotes();
        String initialSampleDescription = studyToPrint.getInitialSampleSize();
        String replicateSampleDescription = studyToPrint.getReplicateSampleSize();

        // Creating a Hashtable

        ArrayList<Object> infoToPrint = new ArrayList<Object>();
        infoToPrint.add(studyToPrint);
        infoToPrint.add(housekeeping);
        infoToPrint.add(initialSampleDescription);
        infoToPrint.add(replicateSampleDescription);
        infoToPrint.add(ancestryRepository.findByStudyIdAndType(studyId, "initial"));
        infoToPrint.add(ancestryRepository.findByStudyIdAndType(studyId, "replication"));
        infoToPrint.add(snpAssociationTableViews);
        infoToPrint.add(studyNotes);


        return infoToPrint;

    }

    // View a study
    @RequestMapping(value = "/studies/{studyId}/printview",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public Callable<String> viewPrintableDetailsOfStudy(Model model, @PathVariable Long studyId) {

        return () -> {
            Hashtable<String, ArrayList<Object>> listStudies = new Hashtable<String, ArrayList<Object>>();

            Study studyToPrint = studyRepository.findOne(studyId);
            ArrayList<Object> infoToPrint = getStudyInfoToPrint(studyToPrint);
            listStudies.put(studyToPrint.getId().toString(), infoToPrint);

            model.addAttribute("studiesToPrint", listStudies);
            return "study_printview";
        };
    }

    // View a pubmed by study_id (list of studies related)
    @RequestMapping(value = "/pubmed/{pubmedId}/printview",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public Callable<String> viewPrintableDetailsOfPubmed(Model model, @PathVariable String pubmedId) {
        return () -> {
            Hashtable<String, ArrayList<Object>> listStudies = new Hashtable<String, ArrayList<Object>>();

            // THOR
            Collection<Study> allStudiesByPubmedId = publicationService.findStudiesByPubmedId(pubmedId);

            for (Study studyToPrint : allStudiesByPubmedId) {
                ArrayList<Object> infoToPrint = getStudyInfoToPrint(studyToPrint);
                listStudies.put(studyToPrint.getId().toString(), infoToPrint);
            }

            model.addAttribute("studiesToPrint", listStudies);
            return "study_printview";
        };
    }


}