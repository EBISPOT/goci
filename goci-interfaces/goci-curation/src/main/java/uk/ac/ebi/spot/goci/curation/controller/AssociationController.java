package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ebi.spot.goci.curation.service.SnpAssociationForm;
import uk.ac.ebi.spot.goci.curation.service.SnpFormRow;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.repository.*;
import uk.ac.ebi.spot.goci.service.AssociationBatchLoaderService;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by emma on 06/01/15.
 *
 * @author emma
 *         Association controller, interpret user input and transform it into a snp/association
 *         model that is represented to the user by the associated HTML page. Used to view, add and edit
 *         existing snp/assocaition information.
 */

@Controller
public class AssociationController {

    private AssociationRepository associationRepository;
    private StudyRepository studyRepository;
    private EfoTraitRepository efoTraitRepository;
    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;
    private AssociationBatchLoaderService associationBatchLoaderService;
    private GeneRepository geneRepository;
    private RiskAlleleRepository riskAlleleRepository;

    @Autowired
    public AssociationController(AssociationRepository associationRepository, StudyRepository studyRepository, EfoTraitRepository efoTraitRepository, SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository, AssociationBatchLoaderService associationBatchLoaderService, GeneRepository geneRepository, RiskAlleleRepository riskAlleleRepository) {
        this.associationRepository = associationRepository;
        this.studyRepository = studyRepository;
        this.efoTraitRepository = efoTraitRepository;
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
        this.associationBatchLoaderService = associationBatchLoaderService;
        this.geneRepository = geneRepository;
        this.riskAlleleRepository = riskAlleleRepository;
    }

    /*  Study SNP/Associations */

    // Generate list of SNP associations linked to a study
    @RequestMapping(value = "/studies/{studyId}/associations", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudySnps(Model model, @PathVariable Long studyId) {

        Collection<Association> associations = new ArrayList<>();
        associations.addAll(associationRepository.findByStudyId(studyId));
        model.addAttribute("studyAssociations", associations);

        // Return form object and by default add one row
        SnpAssociationForm emptyForm = new SnpAssociationForm();
        model.addAttribute("snpAssociationForm", new SnpAssociationForm());

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "study_association";
    }

    // Generate a empty form page
    @RequestMapping(value = "/studies/{studyId}/associations/add", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String addStandardSnps(Model model, @PathVariable Long studyId) {

        // Return form object and by default add one row
        SnpAssociationForm emptyForm = new SnpAssociationForm();
        model.addAttribute("snpAssociationForm", new SnpAssociationForm());

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "add_standard_or_multi_snp_association";
    }



    @RequestMapping(value = "/studies/{studyId}/associations/add", params = {"addRow"})
    public String addRow(SnpAssociationForm snpAssociationForm, Model model, @PathVariable Long studyId) {
        snpAssociationForm.getSnpFormRows().add(new SnpFormRow());

        // Pass back updated form
        model.addAttribute("snpAssociationForm", snpAssociationForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_standard_or_multi_snp_association";
    }

    @RequestMapping(value = "/studies/{studyId}/associations/add", params = {"removeRow"})
    public String removeRow(SnpAssociationForm snpAssociationForm, HttpServletRequest req, Model model, @PathVariable Long studyId) {
        final Integer rowId = Integer.valueOf(req.getParameter("removeRow"));

        //TODO DOESNT WORK
        snpAssociationForm.getSnpFormRows().remove(rowId.intValue());

        // Pass back updated form
        model.addAttribute("snpAssociationForm", snpAssociationForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_standard_or_multi_snp_association";
    }

    // Add new association/snp information to a study
    @RequestMapping(value = "/studies/{studyId}/associations/add", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String addStudySnps(@ModelAttribute SnpAssociationForm snpAssociationForm, @PathVariable Long studyId) {

        // Get our study object
        Study study = studyRepository.findOne(studyId);

        // Create an association object from details in returned form
        Association newAssociation = createAssociation(snpAssociationForm);

        // Set the study ID for our association
        newAssociation.setStudy(study);

        // Save our association information
        associationRepository.save(newAssociation);

        return "redirect:/studies/" + studyId + "/associations";
    }


    // Upload a spreadsheet of snp association information
    @RequestMapping(value = "/studies/{studyId}/associations/upload", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String uploadStudySnps(@RequestParam("file") MultipartFile file, @PathVariable Long studyId, Model model) {

        if (!file.isEmpty()) {
            // Save the uploaded file received in a multipart request as a file in the upload directory
            // The default temporary-file directory is specified by the system property java.io.tmpdir.

            String uploadDir = System.getProperty("java.io.tmpdir");

            // Create file
            File uploadedFile = new File(uploadDir + file.getOriginalFilename());

            // Copy contents of multipart request to newly created file
            try {
                file.transferTo(uploadedFile);
            } catch (IOException e) {
                throw new RuntimeException(
                        "Unable to to upload file ", e);
            }

            String uploadedFilePath = uploadedFile.getAbsolutePath();

            // Set permissions
            uploadedFile.setExecutable(true, false);
            uploadedFile.setReadable(true, false);
            uploadedFile.setWritable(true, false);

            // Send file, including path, to SNP batch loader process
            try {
                ArrayList<Association> associationsFromFile = associationBatchLoaderService.processData(uploadedFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "redirect:/studies/" + studyId + "/associations";

        } else {
            // File is empty so let user know
            model.addAttribute("study", studyRepository.findOne(studyId));
            return "empty_snpfile_upload_warning";
        }
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

/*
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
*/

/*
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
    }*/


/*
    // Edit existing snp(s) linked to association
    @RequestMapping(value = "/associations/{associationId}/snps", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String editAssociationSNPs(@ModelAttribute CuratorReportedSNP reportedSNPs, @PathVariable Long associationId) {
        Association association = associationRepository.findOne(associationId);

        // Array to hold checked snp ids
        Collection<Long> checkedSNPs = addSnps(association, reportedSNPs.getReportedSNPValue());

        // remove any SNPs that are not in the checkedSNPs cache
        removeSnps(association, checkedSNPs);

        // Save our association
        associationRepository.save(association);
        return "redirect:/associations/" + associationId + "/snps";
    }
*/


    /* Model Attributes :
    *  Used for dropdowns in HTML forms
    */

    // EFO traits
    @ModelAttribute("efoTraits")
    public List<EfoTrait> populateEfoTraits() {
        return efoTraitRepository.findAll();
    }

   /* General purpose methods */

    // Takes information in addSNPForm and creates association
    private Association createAssociation(SnpAssociationForm snpAssociationForm) {

        Association association = new Association();

        // Set simple string and float association attributes
        association.setRiskFrequency(snpAssociationForm.getRiskFrequency());
        association.setPvalueText(snpAssociationForm.getPvalueText());
        association.setOrPerCopyNum(snpAssociationForm.getOrPerCopyNum());
        association.setOrType(snpAssociationForm.getOrType());
        association.setSnpType(snpAssociationForm.getSnpType());
        association.setMultiSnpHaplotype(snpAssociationForm.getMultiSnpHaplotype());
        association.setSnpInteraction(snpAssociationForm.getSnpInteraction());
        association.setPvalueMantissa(snpAssociationForm.getPvalueMantissa());
        association.setPvalueExponent(snpAssociationForm.getPvalueExponent());
        association.setOrPerCopyRecip(snpAssociationForm.getOrPerCopyRecip());
        association.setOrPerCopyStdError(snpAssociationForm.getOrPerCopyStdError());
        association.setOrPerCopyRange(snpAssociationForm.getOrPerCopyRange());
        association.setOrPerCopyUnitDescr(snpAssociationForm.getOrPerCopyUnitDescr());

        // Add collection of Efo traits
        association.setEfoTraits(snpAssociationForm.getEfoTraits());

        // Add loci to association
        Collection<Locus> loci = new ArrayList<>();

        // If its a multi-snp haplotype create only one locus or standard case
        if (!snpAssociationForm.getSnpInteraction().equals("1")) {

            Locus locus = new Locus();

            // Create gene from each string entered, may sure to check pre-existence
            Collection<String> authorReportedGenes = snpAssociationForm.getAuthorReportedGenes();
            Collection<Gene> locusGenes = addGenes(authorReportedGenes);

            // Set locus attribute
            locus.setAuthorReportedGenes(locusGenes);

            // Handle rows entered for haplotype by curator
            Collection<SnpFormRow> rows = snpAssociationForm.getSnpFormRows();
            Collection<RiskAllele> locusRiskAlleles = new ArrayList<>();

            for (SnpFormRow row : rows) {

                // Get the curator entered risk allele
                String curatorEnteredRiskAllele = row.getStrongestRiskAllele();
                RiskAllele riskAllele = createRiskAllele(curatorEnteredRiskAllele);

                // For allele assign a SNP
                String curatorEnteredSNP = row.getSnp();
                SingleNucleotidePolymorphism snp = createSnp(curatorEnteredSNP);
                riskAllele.setSnp(snp);

                // Save risk allele
                riskAlleleRepository.save(riskAllele);
                locusRiskAlleles.add(riskAllele);
            }

            loci.add(locus);
        }// end multi-snp haplotype


        else if (snpAssociationForm.getSnpInteraction().equals("1")) {
            // TODO THINK OF LOGIC
        }

        association.setLoci(loci);
        return association;

    }


    private Collection<Gene> addGenes(Collection<String> authorReportedGenes) {
        Collection<Gene> locusGenes = new ArrayList<>();
        for (String authorReportedGene : authorReportedGenes) {

            // Check if gene already exists
            Gene gene = geneRepository.findByGeneName(authorReportedGene);

            // If gene doesn't exist then create and save
            if (gene == null) {
                // Create new gene
                Gene newGene = new Gene();
                newGene.setGeneName(authorReportedGene);

                // Save gene
                gene = geneRepository.save(newGene);
            }
            // Add genes to collection
            locusGenes.add(gene);
        }
        return locusGenes;
    }

    private RiskAllele createRiskAllele(String curatorEnteredRiskAllele) {

        // Check if it exists
        RiskAllele riskAllele = riskAlleleRepository.findByRiskAlleleName(curatorEnteredRiskAllele);

        // If it doesn't exist create it
        if (riskAllele == null) {
            //Create new risk allele
            RiskAllele newRiskAllele = new RiskAllele();
            newRiskAllele.setRiskAlleleName(curatorEnteredRiskAllele);
            riskAllele = newRiskAllele;
        }

        return riskAllele;
    }

    private SingleNucleotidePolymorphism createSnp(String curatorEnteredSNP) {

        // Check if SNP already exists database
        SingleNucleotidePolymorphism snp = singleNucleotidePolymorphismRepository.findByRsIdIgnoreCase(curatorEnteredSNP);

        // If SNP doesn't already exist, create and save
        if (snp == null) {
            // Create new SNP
            SingleNucleotidePolymorphism newSNP = new SingleNucleotidePolymorphism();
            newSNP.setRsId(curatorEnteredSNP);

            // Save SNP
            snp = singleNucleotidePolymorphismRepository.save(newSNP);
        }

        return snp;

    }

/*    private Collection<Long> addSnps(Association association, Collection<String> rsIds) {
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
    }*/

    // Remove any snps that should no longer be linked to association
/*    private void removeSnps(Association association, Collection<Long> checkedSnps) {
        // Get all snps linked to association
        Iterator<SingleNucleotidePolymorphism> linkedSnpIt = association.getSnps().iterator();
        while (linkedSnpIt.hasNext()) {
            SingleNucleotidePolymorphism linkedSnp = linkedSnpIt.next();

            // If checkedSnps does not contain this snp then the user has removed it thus delete link to association
            if (!checkedSnps.contains(linkedSnp.getId())) {
                linkedSnpIt.remove();
            }
        }
    }*/
}
