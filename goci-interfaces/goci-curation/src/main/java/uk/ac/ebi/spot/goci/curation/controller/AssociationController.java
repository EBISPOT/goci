package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.curation.service.CuratorReportedSNP;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;
import uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by emma on 06/01/15.
 *
 * @author emma
 *         Association controller, interpret user input and transform it into a snp/association
 *         model that is represented to the user by the associated HTML page. Used to view, add and edit
 *         existing snp/assocaition information. Also creates entry in SNP table for any new SNPs entered in html form
 */

@Controller
public class AssociationController {

    private AssociationRepository associationRepository;
    private StudyRepository studyRepository;
    private EfoTraitRepository efoTraitRepository;
    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;

    @Autowired
    public AssociationController(AssociationRepository associationRepository, StudyRepository studyRepository, EfoTraitRepository efoTraitRepository, SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository) {
        this.associationRepository = associationRepository;
        this.studyRepository = studyRepository;
        this.efoTraitRepository = efoTraitRepository;
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
    }


    /*  Study SNP/Associations */

    // Generate list of SNP associations linked to a study
    @RequestMapping(value = "/studies/{studyId}/associations", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudySnps(Model model, @PathVariable String studyId) {

        Collection<Association> associations = new ArrayList<>();
        associations.addAll(associationRepository.findByStudyId(Long.parseLong(studyId)));
        model.addAttribute("studyAssociations", associations);

        // Return an empty association object so curators can add new association/snp information to study
        model.addAttribute("studyAssociation", new Association());

        // Return an empty object to hold SNPs curators enter
        model.addAttribute("reportedSNPs", new CuratorReportedSNP());

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(Long.valueOf(studyId).longValue()));
        return "study_association";
    }

    // Add new association/snp information to a study
    @RequestMapping(value = "/studies/{studyId}/associations", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String addStudySnps(@ModelAttribute CuratorReportedSNP reportedSNPs, @ModelAttribute Association studyAssociation, @PathVariable String studyId) {

        // ReportedSNPs object holds a collection of SNPs entered by curator
        Study study = studyRepository.findOne(Long.parseLong(studyId));

        // Set the study ID for our association
        studyAssociation.setStudy(study);

        // Save our association information
        associationRepository.save(studyAssociation);

        // ReportedSNPs object holds a collection of SNPs entered by curator
        // For each SNP entered we need need to create an entry in the SNP table
        addSnps(studyAssociation, reportedSNPs.getReportedSNPValue());
        return "redirect:/studies/" + studyId + "/associations";
    }

     /* Existing association information */

    // View association information
    @RequestMapping(value = "/associations/{associationId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewAssociation(Model model, @PathVariable Long associationId) {

        // Return association with that ID
        Association associationToView = associationRepository.findOne(associationId);
        model.addAttribute("association", associationToView);
        return "edit_association";
    }

    //Edit existing association
    @RequestMapping(value = "/associations/{associationId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String editAssociation(@ModelAttribute Association association, @PathVariable Long associationId) {
        // Find the existing snps for association and ensure they get linked to edited information
        Association oldAssociation = associationRepository.findOne(associationId);
        Collection<SingleNucleotidePolymorphism> oldSnps = oldAssociation.getSnps();
        association.setSnps(oldSnps);

        // Save the association information returned from form
        associationRepository.save(association);
        return "redirect:/associations/" + associationId;
    }


    // View existing snp(s) linked to association
    @RequestMapping(value = "/associations/{associationId}/snps", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewAssociationSNPs(Model model, @PathVariable Long associationId) {

        // Find all cross-references to associated SNPs
        Association association = associationRepository.findOne(associationId);
        Collection<SingleNucleotidePolymorphism> snps = association.getSnps();

        // Get rsID of SNPs associated with those IDs and return to HTML form
        Collection<String> associationSNPs = new ArrayList<>();
        for (SingleNucleotidePolymorphism snp : snps) {
            associationSNPs.add(snp.getRsId());
        }

        // Return list of SNPs entered
        CuratorReportedSNP curatorReportedSNP = new CuratorReportedSNP();
        curatorReportedSNP.setReportedSNPValue(associationSNPs);

        // Return curator added snps for editing
        model.addAttribute("reportedSNPs", curatorReportedSNP);

        // Also passes back association object to view so we can create links back to main study association page
        model.addAttribute("association", association);

        return "edit_snp";


    }


    // Edit existing snp(s) linked to association
    @RequestMapping(value = "/associations/{associationId}/snps", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String editAssociationSNPs(@ModelAttribute CuratorReportedSNP reportedSNPs, @PathVariable Long associationId) {
        Association association = associationRepository.findOne(associationId);

        // Array to hold checked snp ids
        Collection<Long> checkedSNPs = addSnps(association, reportedSNPs.getReportedSNPValue());

        // remove any SNPs that are not in the checkedSNPs cache
        removeSnps(association, checkedSNPs);

        return "redirect:/associations/" + associationId + "/snps";
    }


    /* Model Attributes :
    *  Used for dropdowns in HTML forms
    */

    // EFO traits
    @ModelAttribute("efoTraits")
    public List<EfoTrait> populateEfoTraits() {
        return efoTraitRepository.findAll();
    }

    /* General purpose methods */
    private Collection<Long> addSnps(Association association, Collection<String> rsIds) {
        Collection<Long> checkedSNPs = new ArrayList<>();
        for (String rsId : rsIds) {
            // Check if SNP already exists database
            SingleNucleotidePolymorphism snp = singleNucleotidePolymorphismRepository.findByRsIdIgnoreCase(rsId);

            // If SNP doesn't already exist, create and save
            if (snp == null) {
                // Create new SNP
                SingleNucleotidePolymorphism newSNP = new SingleNucleotidePolymorphism();
                newSNP.setRsId(rsId);

                // Save SNP
                snp = singleNucleotidePolymorphismRepository.save(newSNP);
            }

            // Does current association already have a link to that snp?
            if (!association.getSnps().contains(snp)) {
                // if not, add link from association
                association.getSnps().add(snp);
            }

            // and add snp id to cache
            checkedSNPs.add(snp.getId());
        }
        return checkedSNPs;
    }

    private void removeSnps(Association association, Collection<Long> checkedSnps) {
        Iterator<SingleNucleotidePolymorphism> linkedSnpIt = association.getSnps().iterator();
        while (linkedSnpIt.hasNext()) {
            SingleNucleotidePolymorphism linkedSnp = linkedSnpIt.next();
            if (!checkedSnps.contains(linkedSnp.getId())) {
                linkedSnpIt.remove();
            }
        }
    }
}
