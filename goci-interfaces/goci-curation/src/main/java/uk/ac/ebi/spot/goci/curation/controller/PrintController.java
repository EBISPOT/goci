package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationTableView;
import uk.ac.ebi.spot.goci.curation.service.AssociationViewService;
import uk.ac.ebi.spot.goci.curation.service.SingleSnpMultiSnpAssociationService;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Ethnicity;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.EthnicityRepository;
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.ArrayList;
import java.util.Collection;

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
    private EthnicityRepository ethnicityRepository;
    private AssociationRepository associationRepository;
    private AssociationViewService associationViewService;

    @Autowired
    public PrintController(StudyRepository studyRepository,
                           HousekeepingRepository housekeepingRepository,
                           EthnicityRepository ethnicityRepository,
                           AssociationRepository associationRepository,
                           SingleSnpMultiSnpAssociationService singleSnpMultiSnpAssociationService,
                           AssociationViewService associationViewService) {
        this.studyRepository = studyRepository;
        this.ethnicityRepository = ethnicityRepository;
        this.associationRepository = associationRepository;
        this.associationViewService = associationViewService;
    }

    // View a study
    @RequestMapping(value = "/studies/{studyId}/printview",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String viewPrintableDetailsOfStudy(Model model, @PathVariable Long studyId) {

        // Get relevant study details
        Study studyToView = studyRepository.findOne(studyId);
        Housekeeping housekeeping = studyToView.getHousekeeping();
        String initialSampleDescription = studyToView.getInitialSampleSize();
        String replicateSampleDescription = studyToView.getReplicateSampleSize();

        model.addAttribute("study", studyToView);
        model.addAttribute("housekeeping", housekeeping);
        model.addAttribute("initialSampleDescription", initialSampleDescription);
        model.addAttribute("replicateSampleDescription", replicateSampleDescription);

        // Two types of ethnicity information which the view needs to form two different tables
        Collection<Ethnicity> initialStudyEthnicityDescriptions = new ArrayList<>();
        Collection<Ethnicity> replicationStudyEthnicityDescriptions = new ArrayList<>();

        String initialType = "initial";
        String replicationType = "replication";

        initialStudyEthnicityDescriptions.addAll(ethnicityRepository.findByStudyIdAndType(studyId, initialType));
        replicationStudyEthnicityDescriptions.addAll(ethnicityRepository.findByStudyIdAndType(studyId,
                                                                                              replicationType));

        model.addAttribute("initialStudyEthnicityDescriptions", initialStudyEthnicityDescriptions);
        model.addAttribute("replicationStudyEthnicityDescriptions", replicationStudyEthnicityDescriptions);

        // Association information
        Collection<Association> associations = new ArrayList<>();
        associations.addAll(associationRepository.findByStudyId(studyId));

        // For our associations create a table view object and return
        Collection<SnpAssociationTableView> snpAssociationTableViews = new ArrayList<SnpAssociationTableView>();
        for (Association association : associations) {
            SnpAssociationTableView snpAssociationTableView =
                    associationViewService.createSnpAssociationTableView(association);
            snpAssociationTableViews.add(snpAssociationTableView);
        }
        model.addAttribute("snpAssociationTableViews", snpAssociationTableViews);

        return "printview";
    }

}
