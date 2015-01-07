package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.curation.model.Association;
import uk.ac.ebi.spot.goci.curation.model.EFOTrait;
import uk.ac.ebi.spot.goci.curation.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.curation.repository.EFOTraitRepository;
import uk.ac.ebi.spot.goci.curation.repository.StudyRepository;
import uk.ac.ebi.spot.goci.curation.service.CuratorReportedSNP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by emma on 06/01/15.
 *
 * @author emma
 *         Association controller, interpret user input and transform it into a snp/association
 *         model that is represented to the user by the associated HTML page. Used to view, add and edit
 *         existing snp/assocaition information
 */

@Controller
public class AssociationController {

    private AssociationRepository associationRepository;
    private StudyRepository studyRepository;
    private EFOTraitRepository efoTraitRepository;

    @Autowired
    public AssociationController(AssociationRepository associationRepository, StudyRepository studyRepository, EFOTraitRepository efoTraitRepository) {
        this.associationRepository = associationRepository;
        this.studyRepository = studyRepository;
        this.efoTraitRepository = efoTraitRepository;
    }

    /*  SNP/Associations associated with a study */

    // Generate list of SNP associations linked to a study
    @RequestMapping(value = "/studies/{studyId}/associations", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudySnps(Model model, @PathVariable String studyId) {

        Collection<Association> associations = new ArrayList<>();
        associations.addAll(associationRepository.findByStudyID(studyId));
        model.addAttribute("studyAssociations", associations);

        // Return an empty association object so curators can add new association/snp information to study
        model.addAttribute("studyAssociation", new Association());
        model.addAttribute("reportedSNPs", new CuratorReportedSNP());

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(Long.valueOf(studyId).longValue()));
        return "study_association";
    }

    // Add new association/snp information to a study
    @RequestMapping(value = "/studies/{studyId}/associations", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String addStudySnps(@ModelAttribute CuratorReportedSNP reportedSNPs, @ModelAttribute Association studyAssociation, @PathVariable String studyId) {

        // ReportedSNPs object holds a collection of SNPs entered by curator


        // Set the study ID for our association
        studyAssociation.setStudyID(studyId);

        // Save our association information
        Association updatedAssociation = associationRepository.save(studyAssociation);
        return "redirect:/studies/" + studyId + "/associations";
    }

     /* Existing association information */

    // View association information
    @RequestMapping(value = "/associations/{associationId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewAssociation(Model model, @PathVariable Long associationId) {

        Association associationToView = associationRepository.findOne(associationId);
        model.addAttribute("studyAssociation", associationToView);
        return "edit_association";
    }






    /* Model Attributes :
    *  Used for dropdowns in HTML forms
    */

    // EFO traits
    @ModelAttribute("efoTraits")
    public List<EFOTrait> populateEFOTraits(Model model) {
        return efoTraitRepository.findAll();
    }


}
