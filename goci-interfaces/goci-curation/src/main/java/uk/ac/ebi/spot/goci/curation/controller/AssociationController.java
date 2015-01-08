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
import uk.ac.ebi.spot.goci.curation.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.curation.model.SingleNucleotidePolymorphismXref;
import uk.ac.ebi.spot.goci.curation.repository.*;
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
 *         existing snp/assocaition information. Also creates entry in SNP table for any new SNPS entered in html form
 */

@Controller
public class AssociationController {

    private AssociationRepository associationRepository;
    private StudyRepository studyRepository;
    private EFOTraitRepository efoTraitRepository;
    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;
    private SingleNucleotidePolymorphismXrefRepository singleNucleotidePolymorphismXrefRepository;

    @Autowired
    public AssociationController(AssociationRepository associationRepository, StudyRepository studyRepository, EFOTraitRepository efoTraitRepository, SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository, SingleNucleotidePolymorphismXrefRepository singleNucleotidePolymorphismXrefRepository) {
        this.associationRepository = associationRepository;
        this.studyRepository = studyRepository;
        this.efoTraitRepository = efoTraitRepository;
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
        this.singleNucleotidePolymorphismXrefRepository = singleNucleotidePolymorphismXrefRepository;
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

        // Set the study ID for our association
        studyAssociation.setStudyID(studyId);

        // Save our association information
        associationRepository.save(studyAssociation);

        // ReportedSNPs object holds a collection of SNPs entered by curator
        // For each SNP entered we need need to create an entry in the SNP table

        // TODO NEED TO ONLY CREATE ENTRY IN SNP TABLE IF IT DOESNT ALREADY EXITS

        for (String snp : reportedSNPs.getReportedSNPValue()) {

            // Create new SNP
            SingleNucleotidePolymorphism newSNP = new SingleNucleotidePolymorphism();
            newSNP.setRsID(snp);

            // Save SNP
            singleNucleotidePolymorphismRepository.save(newSNP);

            // Create link in XREF table and save
            SingleNucleotidePolymorphismXref newSNPXref = new SingleNucleotidePolymorphismXref();
            newSNPXref.setAssociationID(studyAssociation.getId());
            newSNPXref.setSnpID(newSNP.getId());
            singleNucleotidePolymorphismXrefRepository.save(newSNPXref);

        }
        return "redirect:/studies/" + studyId + "/associations";
    }

     /* Existing association information */

    // View association information
    @RequestMapping(value = "/associations/{associationId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewAssociation(Model model, @PathVariable Long associationId) {

        // Return association with that ID
        Association associationToView = associationRepository.findOne(associationId);
        model.addAttribute("studyAssociation", associationToView);

        // Find all cross-references to associated SNPs
        Collection<SingleNucleotidePolymorphismXref> xrefs = new ArrayList<>();
        xrefs.addAll(singleNucleotidePolymorphismXrefRepository.findByAssociationID(associationId));

        // For each XREF get the SNP ID
        Collection<Long> snpIDs = new ArrayList<>();
        for (SingleNucleotidePolymorphismXref xref: xrefs){
            snpIDs.add(xref.getSnpID());
        }

        // Get rsID of SNPs associated with those IDs and return to HTML form
        Collection<String> associationSNPs= new ArrayList<>();
        for(Long snpID: snpIDs){
          SingleNucleotidePolymorphism associationSNP = singleNucleotidePolymorphismRepository.findOne(snpID);
          associationSNPs.add(associationSNP.getRsID());
        }

        // Return list of SNPs entered
        CuratorReportedSNP curatorReportedSNP= new CuratorReportedSNP();
        curatorReportedSNP.setReportedSNPValue(associationSNPs);

        // Return curator added snps for editing
        model.addAttribute("reportedSNPs", curatorReportedSNP);

        return "edit_association";
    }

    // Edit existing assoication information
    @RequestMapping(value = "/associations/{associationId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String editAssociation(@ModelAttribute Association association) {

        // TODO ALSO DEAL WITH RETURNED SNPS

        // Saves the new information returned from form
        associationRepository.save(association);
        return "redirect:/studies/" + association.getStudyID() + "/associations";
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
