package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ebi.spot.goci.curation.exception.DataIntegrityException;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationForm;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationInteractionForm;
import uk.ac.ebi.spot.goci.curation.model.SnpFormColumn;
import uk.ac.ebi.spot.goci.curation.model.SnpFormRow;
import uk.ac.ebi.spot.goci.curation.service.AssociationBatchLoaderService;
import uk.ac.ebi.spot.goci.curation.service.AssociationDownloadService;
import uk.ac.ebi.spot.goci.curation.service.LociAttributesService;
import uk.ac.ebi.spot.goci.curation.service.SingleSnpMultiSnpAssociationService;
import uk.ac.ebi.spot.goci.curation.service.SnpInteractionAssociationService;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by emma on 06/01/15.
 *
 * @author emma
 *         <p>
 *         Association controller, interpret user input and transform it into a snp/association model that is
 *         represented to the user by the associated HTML page. Used to view, add and edit existing snp/assocaition
 *         information.
 */

@Controller
public class AssociationController {

    // Repositories
    private AssociationRepository associationRepository;
    private StudyRepository studyRepository;
    private EfoTraitRepository efoTraitRepository;
    private LocusRepository locusRepository;

    // Services
    private AssociationBatchLoaderService associationBatchLoaderService;
    private AssociationDownloadService associationDownloadService;
    private SingleSnpMultiSnpAssociationService singleSnpMultiSnpAssociationService;
    private SnpInteractionAssociationService snpInteractionAssociationService;
    private LociAttributesService lociAttributesService;

    @Autowired
    public AssociationController(AssociationRepository associationRepository,
                                 StudyRepository studyRepository,
                                 EfoTraitRepository efoTraitRepository,
                                 LocusRepository locusRepository,
                                 AssociationBatchLoaderService associationBatchLoaderService,
                                 AssociationDownloadService associationDownloadService,
                                 SingleSnpMultiSnpAssociationService singleSnpMultiSnpAssociationService,
                                 SnpInteractionAssociationService snpInteractionAssociationService,
                                 LociAttributesService lociAttributesService) {
        this.associationRepository = associationRepository;
        this.studyRepository = studyRepository;
        this.efoTraitRepository = efoTraitRepository;
        this.locusRepository = locusRepository;
        this.associationBatchLoaderService = associationBatchLoaderService;
        this.associationDownloadService = associationDownloadService;
        this.singleSnpMultiSnpAssociationService = singleSnpMultiSnpAssociationService;
        this.snpInteractionAssociationService = snpInteractionAssociationService;
        this.lociAttributesService = lociAttributesService;
    }

    /*  Study SNP/Associations */

    // Generate list of SNP associations linked to a study
    @RequestMapping(value = "/studies/{studyId}/associations",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String viewStudySnps(Model model, @PathVariable Long studyId) {

        // Get all associations for a study
        Collection<Association> associations = new ArrayList<>();
        associations.addAll(associationRepository.findByStudyId(studyId));

        // For our associations create a form object and return
        Collection<SnpAssociationForm> snpAssociationForms = new ArrayList<SnpAssociationForm>();
        for (Association association : associations) {

            // TODO WOULD NEED SOME SORT OF CHECK FOR SNP:SNP INTERACTION
            SnpAssociationForm snpAssociationForm = singleSnpMultiSnpAssociationService.createSnpAssociationForm(
                    association);
            snpAssociationForms.add(snpAssociationForm);
        }
        model.addAttribute("snpAssociationForms", snpAssociationForms);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "study_association";
    }

    @RequestMapping(value = "/studies/{studyId}/associations/sortpvalue",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String sortStudySnpsByPvalue(Model model,
                                        @PathVariable Long studyId,
                                        @RequestParam(required = true) String direction) {

        // Get all associations for a study and perform relevant sorting
        Collection<Association> associations = new ArrayList<>();
        if (direction.equals("asc")) {
            associations.addAll(associationRepository.findByStudyId(studyId, sortByPvalueExponentAndMantissaAsc()));
        }

        else if (direction.equals("desc")) {
            associations.addAll(associationRepository.findByStudyId(studyId, sortByPvalueExponentAndMantissaDesc()));
        }

        else {
            associations.addAll(associationRepository.findByStudyId(studyId));
        }

        // For our associations create a form object and return
        List<SnpAssociationForm> snpAssociationForms = new ArrayList<SnpAssociationForm>();
        for (Association association : associations) {

            // TODO WOULD NEED SOME SORT OF CHECK FOR SNP:SNP INTERACTION
            SnpAssociationForm snpAssociationForm = singleSnpMultiSnpAssociationService.createSnpAssociationForm(
                    association);
            snpAssociationForms.add(snpAssociationForm);
        }

        model.addAttribute("snpAssociationForms", snpAssociationForms);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "study_association";
    }


    @RequestMapping(value = "/studies/{studyId}/associations/sortrsid",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String sortStudySnpsByRsid(Model model,
                                      @PathVariable Long studyId,
                                      @RequestParam(required = true) String direction) {

        // Get all associations for a study and perform relevant sorting
        Collection<Association> associations = new ArrayList<>();

        // Sorting will not work for multi-snp haplotype so need to check for that
        Boolean isMultiSnpHaplotype = false;

        if (direction.equals("asc")) {
            associations.addAll(associationRepository.findByStudyId(studyId, sortByRsidAsc()));
        }

        else if (direction.equals("desc")) {
            associations.addAll(associationRepository.findByStudyId(studyId, sortByRsidDesc()));
        }

        else {
            associations.addAll(associationRepository.findByStudyId(studyId));
        }



        // For our associations create a form object and return
        List<SnpAssociationForm> snpAssociationForms = new ArrayList<SnpAssociationForm>();
        for (Association association : associations) {

            // TODO WOULD NEED SOME SORT OF CHECK FOR SNP:SNP INTERACTION
            SnpAssociationForm snpAssociationForm = singleSnpMultiSnpAssociationService.createSnpAssociationForm(
                    association);

            // Record if we have a multi-snp haplotype
            if (snpAssociationForm.getSnpFormRows() != null) {
                if (snpAssociationForm.getSnpFormRows().size() > 1) {
                    isMultiSnpHaplotype = true;
                }
            }

            snpAssociationForms.add(snpAssociationForm);
        }

        // Only return sorted results if its not a multi-snp haplotype
        if (isMultiSnpHaplotype == false){

            model.addAttribute("snpAssociationForms", snpAssociationForms);

            // Also passes back study object to view so we can create links back to main study page
            model.addAttribute("study", studyRepository.findOne(studyId));
            return "study_association";

        }

        else{
            return "redirect:/studies/" + studyId + "/associations";
        }

    }


    // Upload a spreadsheet of snp association information
    @RequestMapping(value = "/studies/{studyId}/associations/upload",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String uploadStudySnps(@RequestParam("file") MultipartFile file, @PathVariable Long studyId, Model model) {

        // Establish our study object
        Study study = studyRepository.findOne(studyId);

        if (!file.isEmpty()) {
            // Save the uploaded file received in a multipart request as a file in the upload directory
            // The default temporary-file directory is specified by the system property java.io.tmpdir.

            String uploadDir =
                    System.getProperty("java.io.tmpdir") + File.separator + "gwas_batch_upload" + File.separator;

            // Create file
            File uploadedFile = new File(uploadDir + file.getOriginalFilename());
            uploadedFile.getParentFile().mkdirs();

            // Copy contents of multipart request to newly created file
            try {
                file.transferTo(uploadedFile);
            }
            catch (IOException e) {
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
                snpAssociationForms = associationBatchLoaderService.processData(uploadedFilePath, efoTraitRepository);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            // Create our associations
            if (!snpAssociationForms.isEmpty()) {
                for (SnpAssociationForm snpAssociationForm : snpAssociationForms) {
                    Association association = singleSnpMultiSnpAssociationService.createAssociation(snpAssociationForm);

                    // Set the study ID for our association
                    association.setStudy(study);

                    // Save our association information
                    associationRepository.save(association);
                }

            }
            return "redirect:/studies/" + studyId + "/associations";

        }
        else {
            // File is empty so let user know
            model.addAttribute("study", studyRepository.findOne(studyId));
            return "empty_snpfile_upload_warning";
        }

    }

    // Generate a empty form page to add standard snp
    @RequestMapping(value = "/studies/{studyId}/associations/add_standard",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String addStandardSnps(Model model, @PathVariable Long studyId) {

        // Return form object
        SnpAssociationForm emptyForm = new SnpAssociationForm();

        // Add one row by default
        emptyForm.getSnpFormRows().add(new SnpFormRow());
        model.addAttribute("snpAssociationForm", emptyForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "add_standard_snp_association";
    }

    // Generate a empty form page to add multi-snp haplotype
    @RequestMapping(value = "/studies/{studyId}/associations/add_multi",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String addMultiSnps(Model model, @PathVariable Long studyId) {

        // Return form object
        SnpAssociationForm emptyForm = new SnpAssociationForm();
        model.addAttribute("snpAssociationForm", emptyForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "add_multi_snp_association";
    }

    // Generate a empty form page to add a interaction association
    @RequestMapping(value = "/studies/{studyId}/associations/add_interaction",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String addSnpInteraction(Model model, @PathVariable Long studyId) {

        // Return form object
        SnpAssociationInteractionForm emptyForm = new SnpAssociationInteractionForm();
        model.addAttribute("snpAssociationInteractionForm", emptyForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "add_snp_interaction_association";
    }

    // Add multiple rows to table
    @RequestMapping(value = "/studies/{studyId}/associations/add_multi", params = {"addRows"})
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

        return "add_multi_snp_association";
    }

    // Add multiple rows to table
    @RequestMapping(value = "/studies/{studyId}/associations/add_interaction", params = {"addCols"})
    public String addRows(SnpAssociationInteractionForm snpAssociationInteractionForm, Model model, @PathVariable Long studyId) {
        Integer numberOfCols = snpAssociationInteractionForm.getNumOfInteractions();

        // Add number of cols curator selected
        while (numberOfCols != 0) {
            snpAssociationInteractionForm.getSnpFormColumns().add(new SnpFormColumn());
            numberOfCols--;
        }

        // Pass back updated form
        model.addAttribute("snpAssociationInteractionForm", snpAssociationInteractionForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_snp_interaction_association";
    }


    // Add single row to table
    @RequestMapping(value = "/studies/{studyId}/associations/add_multi", params = {"addRow"})
    public String addRow(SnpAssociationForm snpAssociationForm, Model model, @PathVariable Long studyId) {
        snpAssociationForm.getSnpFormRows().add(new SnpFormRow());

        // Pass back updated form
        model.addAttribute("snpAssociationForm", snpAssociationForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_multi_snp_association";
    }

    // Remove row from table
    @RequestMapping(value = "/studies/{studyId}/associations/add_multi", params = {"removeRow"})
    public String removeRow(SnpAssociationForm snpAssociationForm,
                            HttpServletRequest req,
                            Model model,
                            @PathVariable Long studyId) {

        //Index of value to remove
        final Integer rowId = Integer.valueOf(req.getParameter("removeRow"));

        // Remove row
        snpAssociationForm.getSnpFormRows().remove(rowId.intValue());

        // Pass back updated form
        model.addAttribute("snpAssociationForm", snpAssociationForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_multi_snp_association";
    }


    // Add new standard association/snp information to a study
    @RequestMapping(value = "/studies/{studyId}/associations/add_standard",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String addStandardSnps(@ModelAttribute SnpAssociationForm snpAssociationForm, @PathVariable Long studyId) {

        // Get our study object
        Study study = studyRepository.findOne(studyId);

        // Create an association object from details in returned form
        Association newAssociation = singleSnpMultiSnpAssociationService.createAssociation(snpAssociationForm);

        // Set the study ID for our association
        newAssociation.setStudy(study);

        // Save our association information
        associationRepository.save(newAssociation);

        return "redirect:/studies/" + studyId + "/associations";
    }

    @RequestMapping(value = "/studies/{studyId}/associations/add_multi",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String addMultiSnps(@ModelAttribute SnpAssociationForm snpAssociationForm, @PathVariable Long studyId) {

        // Get our study object
        Study study = studyRepository.findOne(studyId);

        // Create an association object from details in returned form
        Association newAssociation = singleSnpMultiSnpAssociationService.createAssociation(snpAssociationForm);

        // Set the study ID for our association
        newAssociation.setStudy(study);

        // Save our association information
        associationRepository.save(newAssociation);

        return "redirect:/studies/" + studyId + "/associations";
    }

    @RequestMapping(value = "/studies/{studyId}/associations/add_interaction",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String addSnpInteraction(@ModelAttribute SnpAssociationInteractionForm snpAssociationInteractionForm, @PathVariable Long studyId) {

        // Get our study object
        Study study = studyRepository.findOne(studyId);

        // Create an association object from details in returned form
        Association newAssociation = snpInteractionAssociationService.createAssociation(snpAssociationInteractionForm);

        // Set the study ID for our association
        newAssociation.setStudy(study);

        // Save our association information
        associationRepository.save(newAssociation);

        return "redirect:/studies/" + studyId + "/associations";
    }

     /* Existing association information */

    // View association information
    @RequestMapping(value = "/associations/{associationId}",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String viewAssociation(Model model, @PathVariable Long associationId) {

        // Return association with that ID
        Association associationToView = associationRepository.findOne(associationId);

        // Establish study
        Long studyId = associationToView.getStudy().getId();

        // Figure out the number of risk alleles linked to a single association
        // From this we can decide which view to return
        List<RiskAllele> riskAlleles = new ArrayList<>();
        for (Locus locus : associationToView.getLoci()) {
            for (RiskAllele riskAllele : locus.getStrongestRiskAlleles()) {
                riskAlleles.add(riskAllele);
            }
        }

        // Create form and return to user
        SnpAssociationForm snpAssociationForm = singleSnpMultiSnpAssociationService.createSnpAssociationForm(
                associationToView);
        model.addAttribute("snpAssociationForm", snpAssociationForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));

        // TODO MAYBE HAVE THIS COUNT LOCI
        // Placeholder until we get something working
        if (associationToView.getSnpInteraction() != null && associationToView.getSnpInteraction()) {
            model.addAttribute("study", studyRepository.findOne(studyId));
            return "edit_snp_interaction_association";

        }
        // If editing multi-snp haplotype
        else if (riskAlleles.size() > 1) {
            return "edit_multi_snp_association";
        }
        else {
            return "edit_standard_snp_association";
        }

    }


    //Edit existing association
    @RequestMapping(value = "/associations/{associationId}",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String editAssociation(@ModelAttribute SnpAssociationForm snpAssociationForm,
                                  @PathVariable Long associationId) {

        //Create association
        Association editedAssociation = singleSnpMultiSnpAssociationService.createAssociation(snpAssociationForm);

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
    public String addRowsEditMode(SnpAssociationForm snpAssociationForm,
                                  Model model,
                                  @PathVariable Long associationId) {
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

        return "edit_multi_snp_association";
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

        return "edit_multi_snp_association";
    }

    // Remove row from table
    @RequestMapping(value = "/associations/{associationId}", params = {"removeRow"})
    public String removeRowEditMode(SnpAssociationForm snpAssociationForm,
                                    HttpServletRequest req,
                                    Model model,
                                    @PathVariable Long associationId) {

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

        return "edit_multi_snp_association";
    }


    // View an association to delele
    @RequestMapping(value = "associations/{associationId}/delete",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String viewAssociationToDelete(Model model, @PathVariable Long associationId) {

        // Return association as a form
        Association associationToView = associationRepository.findOne(associationId);
        SnpAssociationForm snpAssociationForm = singleSnpMultiSnpAssociationService.createSnpAssociationForm(
                associationToView);
        model.addAttribute("snpAssociationForm", snpAssociationForm);

        // Return study, this will be used to create link back to page containing all studies for that association
        Study study = studyRepository.findOne(associationToView.getStudy().getId());
        model.addAttribute("study", study);

        return "delete_standard_or_multisnp_association";
    }

    // Delete an association
    @RequestMapping(value = "associations/{associationId}/delete",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String deleteAssociation(Model model, @PathVariable Long associationId) {


        // Get association
        Association associationToDelete = associationRepository.findOne(associationId);

        // Get study Id for redirect
        Long studyId = associationToDelete.getStudy().getId();

        // Get all loci for association
        Collection<Locus> loci = associationToDelete.getLoci();

        // Delete each locus and risk allele, which in turn deletes link to genes via author_reported_gene table,
        // Snps are not deleted as they may be used in other associations
        for (Locus locus : loci) {
            Collection<RiskAllele> locusRiskAlleles = locus.getStrongestRiskAlleles();
            locus.setStrongestRiskAlleles(new ArrayList<>());
            for (RiskAllele riskAllele : locusRiskAlleles) {
                lociAttributesService.deleteRiskAllele(riskAllele);
            }
            locusRepository.delete(locus);
        }
        // Delete association
        associationRepository.delete(associationToDelete);

        // Get study
        Study study = studyRepository.findOne(associationToDelete.getStudy().getId());

        return "redirect:/studies/" + studyId + "/associations";
    }


    // Delete all associations linked to a study
    @RequestMapping(value = "/studies/{studyId}/associations/delete_all",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String deleteAllAssociations(Model model, @PathVariable Long studyId) {

        // Get all associations
        Collection<Association> studyAssociations = associationRepository.findByStudyId(studyId);

        // For each association get the loci
        Collection<Locus> loci = new ArrayList<Locus>();
        for (Association association : studyAssociations) {
            loci.addAll(association.getLoci());
        }

        // Delete each locus and risk allele, which in turn deletes link to genes via author_reported_gene table,
        // Snps are not deleted as they may be used in other associations
        for (Locus locus : loci) {
            Collection<RiskAllele> locusRiskAlleles = locus.getStrongestRiskAlleles();
            locus.setStrongestRiskAlleles(new ArrayList<>());
            for (RiskAllele riskAllele : locusRiskAlleles) {
                lociAttributesService.deleteRiskAllele(riskAllele);
            }
            locusRepository.delete(locus);
        }
        // Delete associations
        for (Association association : studyAssociations) {
            associationRepository.delete(association);
        }

        return "redirect:/studies/" + studyId + "/associations";
    }

    // Delete checked SNP associations
    @RequestMapping(value = "/studies/{studyId}/associations/delete_checked",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> deleteChecked(@RequestParam(value = "associationIds[]") String[] associationsIds) {

        String message = "";
        Integer count = 0;

        Collection<Locus> loci = new ArrayList<Locus>();
        Collection<Association> studyAssociations = new ArrayList<Association>();

        // For each association get the loci attached
        for (String associationId : associationsIds) {
            Association association = associationRepository.findOne(Long.valueOf(associationId));
            loci.addAll(association.getLoci());
            studyAssociations.add(association);
            count++;
        }

        // Delete each locus and risk allele, which in turn deletes link to genes via author_reported_gene table,
        // Snps are not deleted as they may be used in other associations
        for (Locus locus : loci) {
            Collection<RiskAllele> locusRiskAlleles = locus.getStrongestRiskAlleles();
            locus.setStrongestRiskAlleles(new ArrayList<>());
            for (RiskAllele riskAllele : locusRiskAlleles) {
                lociAttributesService.deleteRiskAllele(riskAllele);
            }
            locusRepository.delete(locus);
        }

        // Delete associations
        for (Association association : studyAssociations) {
            associationRepository.delete(association);
        }

        message = "Successfully deleted " + count + " associations";

        Map<String, String> result = new HashMap<>();
        result.put("message", message);
        return result;

    }

    /*  Approve snp associations */
    // Approve a SNP association
    @RequestMapping(value = "associations/{associationId}/approve",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String approveSnpAssociation(Model model, @PathVariable Long associationId) {

        Association association = associationRepository.findOne(associationId);

        // Set snpChecked attribute to true
        association.setSnpChecked(true);
        associationRepository.save(association);

        return "redirect:/studies/" + association.getStudy().getId() + "/associations";
    }


    // Approve checked SNPs
    @RequestMapping(value = "/studies/{studyId}/associations/approve_checked",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> approveChecked(@RequestParam(value = "associationIds[]") String[] associationsIds) {

        String message = "";
        Integer count = 0;

        // For each one set snpChecked attribute to true
        for (String associationId : associationsIds) {
            Association association = associationRepository.findOne(Long.valueOf(associationId));
            association.setSnpChecked(true);
            associationRepository.save(association);
            count++;
        }
        message = "Successfully updated " + count + " associations";

        Map<String, String> result = new HashMap<>();
        result.put("message", message);
        return result;

    }


    // Approve all SNPs
    @RequestMapping(value = "/studies/{studyId}/associations/approve_all",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
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


    @RequestMapping(value = "/studies/{studyId}/associations/download",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String downloadStudySnps(HttpServletResponse response, Model model, @PathVariable Long studyId)
            throws IOException {

        Collection<Association> associations = new ArrayList<>();
        associations.addAll(associationRepository.findByStudyId(studyId));
        Study study = studyRepository.findOne((studyId));

        if (associations.size() == 0) {
            model.addAttribute("study", study);
            return "no_association_download_warning";
        }
        else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String now = dateFormat.format(date);

            String fileName =
                    study.getAuthor().concat("-").concat(study.getPubmedId()).concat("-").concat(now).concat(".tsv");
            response.setContentType("text/tsv");
            response.setHeader("Content-Disposition", "attachement; filename=" + fileName);

            associationDownloadService.createDownloadFile(response.getOutputStream(), associations);

            return "redirect:/studies/" + studyId + "/associations";
        }
    }


    @RequestMapping(value = "/studies/{studyId}/associations/applyefotraits",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String applyStudyEFOtraitToSnps(Model model, @PathVariable Long studyId,
                                           @RequestParam(value = "e", required = false, defaultValue = "false") boolean existing,
                                           @RequestParam(value = "o", required = false, defaultValue = "true") boolean overwrite)
            throws IOException {

        Collection<Association> associations = new ArrayList<>();
        associations.addAll(associationRepository.findByStudyId(studyId));
        Study study = studyRepository.findOne((studyId));
        Collection<EfoTrait> efoTraits = study.getEfoTraits();


        if(associations.size() == 0 || efoTraits.size() == 0){
            model.addAttribute("study", study);
            return "no_association_efo_trait_warning";
        }
        else{
            if(!existing) {
                for (Association association : associations) {
                    if (association.getEfoTraits().size() != 0) {
                        model.addAttribute("study", study);
                        return "existing_efo_traits_warning";
                    }
                }
            }
            Collection<EfoTrait> associationTraits = new ArrayList<EfoTrait>();

            for(EfoTrait efoTrait : efoTraits){
                associationTraits.add(efoTrait);
            }

            for(Association association : associations){
                if(association.getEfoTraits().size() != 0 && !overwrite){
                   for(EfoTrait trait : associationTraits){
                       if(!association.getEfoTraits().contains(trait)) {
                           association.addEfoTrait(trait);
                       }
                   }
                }
                else {
                    association.setEfoTraits(associationTraits);
                }
                associationRepository.save(association);
            }

            return "redirect:/studies/" + studyId + "/associations";
        }
    }


   /* General purpose methods */

    // Takes information in addSNPForm and creates association

    /* Exception handling */
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
        return efoTraitRepository.findAll(sortByTraitAsc());
    }

    // Sort options
    private Sort sortByTraitAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "trait").ignoreCase());
    }

    private Sort sortByPvalueExponentAndMantissaAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "pvalueExponent"),
                        new Sort.Order(Sort.Direction.ASC, "pvalueMantissa"));
    }

    private Sort sortByPvalueExponentAndMantissaDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "pvalueExponent"),
                        new Sort.Order(Sort.Direction.DESC, "pvalueMantissa"));
    }

    private Sort sortByRsidAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "loci.strongestRiskAlleles.snp.rsId"));
    }

    private Sort sortByRsidDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "loci.strongestRiskAlleles.snp.rsId"));
    }
}

