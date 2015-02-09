package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ebi.spot.goci.curation.exception.DataIntegrityException;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationForm;
import uk.ac.ebi.spot.goci.curation.model.SnpFormRow;
import uk.ac.ebi.spot.goci.curation.service.AssociationBatchLoaderService;
import uk.ac.ebi.spot.goci.curation.service.AssociationCalculationService;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.repository.*;

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

    // Repositories
    private AssociationRepository associationRepository;
    private StudyRepository studyRepository;
    private EfoTraitRepository efoTraitRepository;
    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;
    private GeneRepository geneRepository;
    private RiskAlleleRepository riskAlleleRepository;
    private LocusRepository locusRepository;

    // Services
    private AssociationBatchLoaderService associationBatchLoaderService;
    private AssociationCalculationService associationCalculationService;

    @Autowired
    public AssociationController(AssociationRepository associationRepository, StudyRepository studyRepository, EfoTraitRepository efoTraitRepository, SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository, GeneRepository geneRepository, RiskAlleleRepository riskAlleleRepository, LocusRepository locusRepository, AssociationBatchLoaderService associationBatchLoaderService, AssociationCalculationService associationCalculationService) {
        this.associationRepository = associationRepository;
        this.studyRepository = studyRepository;
        this.efoTraitRepository = efoTraitRepository;
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
        this.geneRepository = geneRepository;
        this.riskAlleleRepository = riskAlleleRepository;
        this.locusRepository = locusRepository;
        this.associationBatchLoaderService = associationBatchLoaderService;
        this.associationCalculationService = associationCalculationService;
    }

    /*  Study SNP/Associations */

    // Generate list of SNP associations linked to a study
    @RequestMapping(value = "/studies/{studyId}/associations", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewStudySnps(Model model, @PathVariable Long studyId) {

        Collection<Association> associations = new ArrayList<>();
        associations.addAll(associationRepository.findByStudyId(studyId));

        // For our associations create a form object and return
        Collection<SnpAssociationForm> snpAssociationForms = new ArrayList<SnpAssociationForm>();
        for (Association association : associations) {
            // TODO WOULD NEED SOME SORT OF CHECK FOR SNP:SNP INTERACTION
            SnpAssociationForm snpAssociationForm = createSnpAssociationForm(association);
            snpAssociationForms.add(snpAssociationForm);
        }
        model.addAttribute("snpAssociationForms", snpAssociationForms);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "study_association";
    }

    // Upload a spreadsheet of snp association information
    @RequestMapping(value = "/studies/{studyId}/associations/upload", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String uploadStudySnps(@RequestParam("file") MultipartFile file, @PathVariable Long studyId, Model model) {

        // Establish our study object
        Study study = studyRepository.findOne(studyId);

        if (!file.isEmpty()) {
            // Save the uploaded file received in a multipart request as a file in the upload directory
            // The default temporary-file directory is specified by the system property java.io.tmpdir.

            String uploadDir = System.getProperty("java.io.tmpdir") + File.separator + "gwas_batch_upload" + File.separator;

            // Create file
            File uploadedFile = new File(uploadDir + file.getOriginalFilename());
            uploadedFile.getParentFile().mkdirs();

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
            Collection<SnpAssociationForm> snpAssociationForms = new ArrayList<>();
            try {
                snpAssociationForms = associationBatchLoaderService.processData(uploadedFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Create our associations
            if (!snpAssociationForms.isEmpty()) {
                for (SnpAssociationForm snpAssociationForm : snpAssociationForms) {
                    Association association = createStandardAssociation(snpAssociationForm);

                    // Set the study ID for our association
                    association.setStudy(study);

                    // Save our association information
                    associationRepository.save(association);
                }

            }
            return "redirect:/studies/" + studyId + "/associations";

        } else {
            // File is empty so let user know
            model.addAttribute("study", studyRepository.findOne(studyId));
            return "empty_snpfile_upload_warning";
        }

    }

    // Generate a empty form page to add standard or multi-snp haplotype
    @RequestMapping(value = "/studies/{studyId}/associations/add", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String addStandardSnps(Model model, @PathVariable Long studyId) {

        // Return form object
        SnpAssociationForm emptyForm = new SnpAssociationForm();
        model.addAttribute("snpAssociationForm", new SnpAssociationForm());

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "add_standard_or_multi_snp_association";
    }

    // Generate a empty form page to add standard or multi-snp haplotype
    @RequestMapping(value = "/studies/{studyId}/associations/add_interaction", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String addSnpInteraction(Model model, @PathVariable Long studyId) {

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "add_snp_interaction_association";
    }

    // Add multiple rows to table
    @RequestMapping(value = "/studies/{studyId}/associations/add", params = {"addRows"})
    public String addRows(SnpAssociationForm snpAssociationForm, Model model, @PathVariable Long studyId) {
        Integer numberOfRows = snpAssociationForm.getMultiSnpHaplotypeNum();

        // Add number of rows curator selected
        while (numberOfRows != 0) {
            snpAssociationForm.getSnpFormRows().add(new SnpFormRow());
            numberOfRows--;
        }

        // Pass back updated form
        model.addAttribute("snpAssociationForm", snpAssociationForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_standard_or_multi_snp_association";
    }

    // Add single row to table
    @RequestMapping(value = "/studies/{studyId}/associations/add", params = {"addRow"})
    public String addRow(SnpAssociationForm snpAssociationForm, Model model, @PathVariable Long studyId) {
        snpAssociationForm.getSnpFormRows().add(new SnpFormRow());

        // Pass back updated form
        model.addAttribute("snpAssociationForm", snpAssociationForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_standard_or_multi_snp_association";
    }

    // Remove row from table
    @RequestMapping(value = "/studies/{studyId}/associations/add", params = {"removeRow"})
    public String removeRow(SnpAssociationForm snpAssociationForm, HttpServletRequest req, Model model, @PathVariable Long studyId) {

        //Index of value to remove
        final Integer rowId = Integer.valueOf(req.getParameter("removeRow"));

        // Remove row
        snpAssociationForm.getSnpFormRows().remove(rowId.intValue());

        // Pass back updated form
        model.addAttribute("snpAssociationForm", snpAssociationForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_standard_or_multi_snp_association";
    }


    // Add new standard or multi-snp haplotype association/snp information to a study
    @RequestMapping(value = "/studies/{studyId}/associations/add", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String addStudySnps(@ModelAttribute SnpAssociationForm snpAssociationForm, @PathVariable Long studyId) {

        // Get our study object
        Study study = studyRepository.findOne(studyId);

        // Create an association object from details in returned form
        Association newAssociation = createStandardAssociation(snpAssociationForm);

        // Set the study ID for our association
        newAssociation.setStudy(study);

        // Save our association information
        associationRepository.save(newAssociation);

        return "redirect:/studies/" + studyId + "/associations";
    }


     /* Existing association information */

    // View association information
    @RequestMapping(value = "/associations/{associationId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewAssociation(Model model, @PathVariable Long associationId) {

        // Return association with that ID
        Association associationToView = associationRepository.findOne(associationId);

        // Establish study
        Long studyId = associationToView.getStudy().getId();

        // Placeholder until we get something working
        if (associationToView.getSnpInteraction() != null && associationToView.getSnpInteraction()) {
            model.addAttribute("study", studyRepository.findOne(studyId));
            return "edit_snp_interaction_association";

        } else {
            // Create form and return to user
            SnpAssociationForm snpAssociationForm = createSnpAssociationForm(associationToView);
            model.addAttribute("snpAssociationForm", snpAssociationForm);

            // Also passes back study object to view so we can create links back to main study page
            model.addAttribute("study", studyRepository.findOne(studyId));

            return "edit_standard_or_multi_snp_association";

        }
    }


    //Edit existing association
    @RequestMapping(value = "/associations/{associationId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String editAssociation(@ModelAttribute SnpAssociationForm snpAssociationForm, @PathVariable Long associationId) {

        //Create association
        Association editedAssociation = createStandardAssociation(snpAssociationForm);

        // Set ID of new  association to the ID of the association we're currently editing
        editedAssociation.setId(associationId);

        // Set study to one currently linked to association
        Association currentAssociation = associationRepository.findOne(associationId);
        Study associationStudy = currentAssociation.getStudy();
        editedAssociation.setStudy(associationStudy);

        // Save our association information
        associationRepository.save(editedAssociation);

        return "redirect:/studies/" + editedAssociation.getStudy().getId() + "/associations";
    }


    // Add multiple rows to table
    @RequestMapping(value = "/associations/{associationId}", params = {"addRows"})
    public String addRowsEditMode(SnpAssociationForm snpAssociationForm, Model model, @PathVariable Long associationId) {
        Integer numberOfRows = snpAssociationForm.getMultiSnpHaplotypeNum();

        // Add number of rows curator selected
        while (numberOfRows != 0) {
            snpAssociationForm.getSnpFormRows().add(new SnpFormRow());
            numberOfRows--;
        }

        // Pass back updated form
        model.addAttribute("snpAssociationForm", snpAssociationForm);

        // Also passes back study object to view so we can create links back to main study page
        Association currentAssociation = associationRepository.findOne(associationId);
        Study associationStudy = currentAssociation.getStudy();
        Long studyId = associationStudy.getId();
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "edit_standard_or_multi_snp_association";
    }

    // Add single row to table
    @RequestMapping(value = "/associations/{associationId}", params = {"addRow"})
    public String addRowEditMode(SnpAssociationForm snpAssociationForm, Model model, @PathVariable Long associationId) {
        snpAssociationForm.getSnpFormRows().add(new SnpFormRow());

        // Pass back updated form
        model.addAttribute("snpAssociationForm", snpAssociationForm);

        // Also passes back study object to view so we can create links back to main study page
        Association currentAssociation = associationRepository.findOne(associationId);
        Study associationStudy = currentAssociation.getStudy();
        Long studyId = associationStudy.getId();
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "edit_standard_or_multi_snp_association";
    }

    // Remove row from table
    @RequestMapping(value = "/associations/{associationId}", params = {"removeRow"})
    public String removeRowEditMode(SnpAssociationForm snpAssociationForm, HttpServletRequest req, Model model, @PathVariable Long associationId) {

        //Index of value to remove
        final Integer rowId = Integer.valueOf(req.getParameter("removeRow"));

        // Remove row
        snpAssociationForm.getSnpFormRows().remove(rowId.intValue());

        // Pass back updated form
        model.addAttribute("snpAssociationForm", snpAssociationForm);

        // Also passes back study object to view so we can create links back to main study page
        Association currentAssociation = associationRepository.findOne(associationId);
        Study associationStudy = currentAssociation.getStudy();
        Long studyId = associationStudy.getId();
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "edit_standard_or_multi_snp_association";
    }


    // Delete an association
    @RequestMapping(value = "associations/{associationId}/delete", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewAssociationToDelete(Model model, @PathVariable Long associationId) {

        // Return association as a form
        Association associationToView = associationRepository.findOne(associationId);
        SnpAssociationForm snpAssociationForm = createSnpAssociationForm(associationToView);
        model.addAttribute("snpAssociationForm", snpAssociationForm);

        // Return study, this will be used to create link back to page containing all studies for that association
        Study study = studyRepository.findOne(associationToView.getStudy().getId());
        model.addAttribute("study", study);

        return "delete_standard_or_multisnp_association";
    }

    // Delete an association
    @RequestMapping(value = "associations/{associationId}/delete", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String deleteAssociation(Model model, @PathVariable Long associationId) {


        // Get association
        Association associationToDelete = associationRepository.findOne(associationId);

        // Get study Id for redirect
        Long studyId = associationToDelete.getStudy().getId();

        // Get all loci for association
        Collection<Locus> loci = associationToDelete.getLoci();

        // Delete each locus, which in turn deletes link to genes via author_reported_gene table,
        // Snp and risk allele are not deleted as they may be used in other associations
        for (Locus locus : loci) {
            locusRepository.delete(locus);
        }

        // Delete association
        associationRepository.delete(associationToDelete);

        // Get study
        Study study = studyRepository.findOne(associationToDelete.getStudy().getId());

        return "redirect:/studies/" + studyId + "/associations";
    }

   /*  Approve snp associations */
    // Approve all SNPs
    @RequestMapping(value = "/studies/{studyId}/associations/approve_all", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String approveAll(Model model, @PathVariable Long studyId) {

        // Get all associations
        Collection<Association> studyAssociations = associationRepository.findByStudyId(studyId);

        // For each one set snpChecked attribute to true
        for (Association association : studyAssociations) {
            association.setSnpChecked(true);
            associationRepository.save(association);
        }
        return "redirect:/studies/" + studyId + "/associations";

    }

   /* General purpose methods */

    // Takes information in addSNPForm and creates association
    private Association createStandardAssociation(SnpAssociationForm snpAssociationForm) throws DataIntegrityException {

        Association association = new Association();

        // Set simple string, boolean and float association attributes
        association.setRiskFrequency(snpAssociationForm.getRiskFrequency());
        association.setPvalueText(snpAssociationForm.getPvalueText());
        association.setOrType(snpAssociationForm.getOrType());
        association.setSnpType(snpAssociationForm.getSnpType());
        association.setMultiSnpHaplotype(snpAssociationForm.getMultiSnpHaplotype());
        association.setSnpInteraction(snpAssociationForm.getSnpInteraction());
        association.setSnpChecked(snpAssociationForm.getSnpChecked());

        // Add collection of EFO traits
        association.setEfoTraits(snpAssociationForm.getEfoTraits());

        // Set mantissa and exponent
        association.setPvalueMantissa(snpAssociationForm.getPvalueMantissa());
        association.setPvalueExponent(snpAssociationForm.getPvalueExponent());

        // Calculate p-value float
        Integer pvalueMantissa = snpAssociationForm.getPvalueMantissa();
        Integer pvalueExponent = snpAssociationForm.getPvalueExponent();

        if (pvalueMantissa != null && pvalueExponent != null) {
            association.setPvalueFloat(associationCalculationService.calculatePvalueFloat(pvalueMantissa, pvalueExponent));
        } else {
            association.setPvalueFloat(Float.valueOf(0));
        }

        // If reciprocal is given, program will calculate OR from that
        // This logic is retained from Dani's original code
        Float orPerCopyNum = snpAssociationForm.getOrPerCopyNum();
        Float orPerCopyRecip = snpAssociationForm.getOrPerCopyRecip();
        Float orPerCopyStdError = snpAssociationForm.getOrPerCopyStdError();
        String orPerCopyRange = snpAssociationForm.getOrPerCopyRange();
        boolean recipReverse = false;

        // Calculate OR per copy num
        if ((orPerCopyRecip != null) && (orPerCopyNum == null)) {
            orPerCopyNum = ((100 / orPerCopyRecip) / 100);
            association.setOrPerCopyNum(orPerCopyNum);
            recipReverse = true;
        }
        // Otherwise set to whatever is in form
        else {
            association.setOrPerCopyNum(snpAssociationForm.getOrPerCopyNum());
        }

        // Set OrPerCopyRecip
        association.setOrPerCopyRecip(snpAssociationForm.getOrPerCopyRecip());

        // This logic is retained from Dani's original code
        if ((orPerCopyRecip != null) && (orPerCopyRange != null) && recipReverse) {
            orPerCopyRange = associationCalculationService.reverseCI(orPerCopyRange);
            association.setOrPerCopyRange(orPerCopyRange);
        } else if ((orPerCopyRange == null) && (orPerCopyRange.isEmpty()) && (orPerCopyStdError != null)) {
            orPerCopyRange = associationCalculationService.setRange(orPerCopyStdError, orPerCopyNum);
            association.setOrPerCopyRange(orPerCopyRange);
        } else {
            association.setOrPerCopyRange(snpAssociationForm.getOrPerCopyRange());
        }

        association.setOrPerCopyStdError(snpAssociationForm.getOrPerCopyStdError());
        association.setOrPerCopyUnitDescr(snpAssociationForm.getOrPerCopyUnitDescr());

        // Add loci to association or if we are editing an existing one find it
        // For multi-snp and standard snps we assume their is only one locus
        Collection<Locus> loci = new ArrayList<>();
        Locus locus = new Locus();

        // Check for existing locus
        if (!association.getLoci().isEmpty()) {
            Association associationUserIsEditing = associationRepository.findOne(snpAssociationForm.getAssociationId());
            Collection<Locus> associationLoci = associationUserIsEditing.getLoci();

            // Based on assumption we have only one locus for standard and multi-snp haplotype
            for (Locus associationLocus : associationLoci) {
                locus = associationLocus;
            }
        }

        // Set locus description and haplotype count
        // Set this number to the number of rows entered by curator
        Integer numberOfRows = snpAssociationForm.getSnpFormRows().size();
        if (numberOfRows > 1) {
            locus.setHaplotypeSnpCount(numberOfRows);
        }

        locus.setDescription(snpAssociationForm.getMultiSnpHaplotypeDescr());

        // Create gene from each string entered, may sure to check pre-existence
        Collection<String> authorReportedGenes = snpAssociationForm.getAuthorReportedGenes();
        Collection<Gene> locusGenes = createGene(authorReportedGenes);

        // Set locus genes
        locus.setAuthorReportedGenes(locusGenes);

        // Handle rows entered for haplotype by curator
        Collection<SnpFormRow> rows = snpAssociationForm.getSnpFormRows();
        Collection<RiskAllele> locusRiskAlleles = new ArrayList<>();

        for (SnpFormRow row : rows) {

            // Create snps from row information
            String curatorEnteredSNP = row.getSnp();
            SingleNucleotidePolymorphism snp = createSnp(curatorEnteredSNP);

            // Get the curator entered risk allele
            String curatorEnteredRiskAllele = row.getStrongestRiskAllele();
            RiskAllele riskAllele = createRiskAllele(curatorEnteredRiskAllele);

            // For allele assign SNP if one isn't already present
            if (riskAllele.getSnp() == null) {
                riskAllele.setSnp(snp);

                // Save changes to risk allele
                riskAlleleRepository.save(riskAllele);
            } else {
                if (!riskAllele.getSnp().equals(snp)) {
                    throw new DataIntegrityException("Risk allele: " + riskAllele.getRiskAlleleName() + " has SNP " + riskAllele.getSnp().getRsId() + " attached in database, cannot also add " + snp.getRsId());

                }
            }

            locusRiskAlleles.add(riskAllele);
        }

        // Assign all created risk alleles to locus
        locus.setStrongestRiskAlleles(locusRiskAlleles);

        // Save our newly created locus
        locusRepository.save(locus);

        // Add locus to collection and link to our repository
        loci.add(locus);
        association.setLoci(loci);

        return association;

    }


    private Collection<Gene> createGene(Collection<String> authorReportedGenes) {
        Collection<Gene> locusGenes = new ArrayList<>();
        for (String authorReportedGene : authorReportedGenes) {

            // Check if gene already exists, note we may have duplicates so for moment just take first one
            List<Gene> genesInDatabase = geneRepository.findByGeneNameIgnoreCase(authorReportedGene);
            Gene gene;

            // Exists in database already
            if (genesInDatabase.size() > 0) {
                gene = genesInDatabase.get(0);
            }


            // If gene doesn't exist then create and save
            else {
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

        // If it doesn't exist create and save
        if (riskAllele == null) {
            //Create new risk allele
            RiskAllele newRiskAllele = new RiskAllele();
            newRiskAllele.setRiskAlleleName(curatorEnteredRiskAllele);

            // Save risk allele
            riskAllele = riskAlleleRepository.save(newRiskAllele);
        }

        return riskAllele;
    }

    private SingleNucleotidePolymorphism createSnp(String curatorEnteredSNP) {

        // Check if SNP already exists database
        SingleNucleotidePolymorphism snp = singleNucleotidePolymorphismRepository.findByRsIdIgnoreCase(curatorEnteredSNP);

        // If SNP doesn't exist, create and save
        if (snp == null) {
            // Create new SNP
            SingleNucleotidePolymorphism newSNP = new SingleNucleotidePolymorphism();
            newSNP.setRsId(curatorEnteredSNP);

            // Save SNP
            snp = singleNucleotidePolymorphismRepository.save(newSNP);
        }

        return snp;

    }

    // Creates form which we can then return to view for editing etc.
    private SnpAssociationForm createSnpAssociationForm(Association association) {

        SnpAssociationForm snpAssociationForm = new SnpAssociationForm();

        // Set association ID
        snpAssociationForm.setAssociationId(association.getId());

        // Set simple string and float association attributes
        snpAssociationForm.setRiskFrequency(association.getRiskFrequency());
        snpAssociationForm.setPvalueText(association.getPvalueText());
        snpAssociationForm.setOrPerCopyNum(association.getOrPerCopyNum());
        snpAssociationForm.setOrType(association.getOrType());
        snpAssociationForm.setSnpType(association.getSnpType());
        snpAssociationForm.setMultiSnpHaplotype(association.getMultiSnpHaplotype());
        snpAssociationForm.setSnpChecked(association.getSnpChecked());
        snpAssociationForm.setSnpInteraction(association.getSnpInteraction());
        snpAssociationForm.setPvalueMantissa(association.getPvalueMantissa());
        snpAssociationForm.setPvalueExponent(association.getPvalueExponent());
        snpAssociationForm.setOrPerCopyRecip(association.getOrPerCopyRecip());
        snpAssociationForm.setOrPerCopyStdError(association.getOrPerCopyStdError());
        snpAssociationForm.setOrPerCopyRange(association.getOrPerCopyRange());
        snpAssociationForm.setOrPerCopyUnitDescr(association.getOrPerCopyUnitDescr());
        snpAssociationForm.setPvalueFloat(association.getPvalueFloat());

        // Add collection of Efo traits
        snpAssociationForm.setEfoTraits(association.getEfoTraits());

        // For each locus get genes and risk alleles
        // For multi-snp and standard snps we assume their is only one locus
        Collection<Locus> loci = association.getLoci();

        Collection<Gene> locusGenes = new ArrayList<>();
        Collection<RiskAllele> locusRiskAlleles = new ArrayList<>();

        for (Locus locus : loci) {
            locusGenes.addAll(locus.getAuthorReportedGenes());
            locusRiskAlleles.addAll(locus.getStrongestRiskAlleles());

            // There should only be one locus thus should be safe to set these here
            snpAssociationForm.setMultiSnpHaplotypeNum(locus.getHaplotypeSnpCount());
            snpAssociationForm.setMultiSnpHaplotypeDescr(locus.getDescription());
        }

        // Get name of gene and add to form
        Collection<String> authorReportedGenes = new ArrayList<>();
        for (Gene locusGene : locusGenes) {
            authorReportedGenes.add(locusGene.getGeneName());
        }
        snpAssociationForm.setAuthorReportedGenes(authorReportedGenes);

        // Handle snp rows
        List<SnpFormRow> snpFormRows = new ArrayList<SnpFormRow>();
        for (RiskAllele riskAllele : locusRiskAlleles) {
            SnpFormRow snpFormRow = new SnpFormRow();
            snpFormRow.setStrongestRiskAllele(riskAllele.getRiskAlleleName());
            snpFormRow.setSnp(riskAllele.getSnp().getRsId());
            snpFormRows.add(snpFormRow);
        }

        snpAssociationForm.setSnpFormRows(snpFormRows);
        return snpAssociationForm;
    }


  /*  Exception handling */

    @ExceptionHandler(DataIntegrityException.class)
    public String handleDataIntegrityException(DataIntegrityException dataIntegrityException, Model model) {
        return dataIntegrityException.getMessage();
    }


    /* Model Attributes :
    *  Used for dropdowns in HTML forms
    */

    // EFO traits
    @ModelAttribute("efoTraits")
    public List<EfoTrait> populateEfoTraits() {
        return efoTraitRepository.findAll();
    }

}

