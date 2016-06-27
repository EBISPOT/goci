package uk.ac.ebi.spot.goci.curation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.spot.goci.curation.exception.DataIntegrityException;
import uk.ac.ebi.spot.goci.curation.exception.FileUploadException;
import uk.ac.ebi.spot.goci.curation.model.AssociationUploadErrorView;
import uk.ac.ebi.spot.goci.curation.model.LastViewedAssociation;
import uk.ac.ebi.spot.goci.curation.model.MappingDetails;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationForm;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationInteractionForm;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationStandardMultiForm;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationTableView;
import uk.ac.ebi.spot.goci.curation.model.SnpFormColumn;
import uk.ac.ebi.spot.goci.curation.model.SnpFormRow;
import uk.ac.ebi.spot.goci.curation.service.AssociationDownloadService;
import uk.ac.ebi.spot.goci.curation.service.AssociationOperationsService;
import uk.ac.ebi.spot.goci.curation.service.AssociationUploadService;
import uk.ac.ebi.spot.goci.curation.service.AssociationViewService;
import uk.ac.ebi.spot.goci.curation.service.CheckEfoTermAssignmentService;
import uk.ac.ebi.spot.goci.curation.service.CurrentUserDetailsService;
import uk.ac.ebi.spot.goci.curation.service.LociAttributesService;
import uk.ac.ebi.spot.goci.curation.service.SingleSnpMultiSnpAssociationService;
import uk.ac.ebi.spot.goci.curation.service.SnpInteractionAssociationService;
import uk.ac.ebi.spot.goci.exception.EnsemblMappingException;
import uk.ac.ebi.spot.goci.exception.SheetProcessingException;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.MappingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
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
    private AssociationDownloadService associationDownloadService;
    private AssociationViewService associationViewService;
    private SingleSnpMultiSnpAssociationService singleSnpMultiSnpAssociationService;
    private SnpInteractionAssociationService snpInteractionAssociationService;
    private LociAttributesService lociAttributesService;
    private CheckEfoTermAssignmentService checkEfoTermAssignmentService;
    private AssociationOperationsService associationOperationsService;
    private MappingService mappingService;
    private AssociationUploadService associationUploadService;
    private CurrentUserDetailsService currentUserDetailsService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public AssociationController(AssociationRepository associationRepository,
                                 StudyRepository studyRepository,
                                 EfoTraitRepository efoTraitRepository,
                                 LocusRepository locusRepository,
                                 AssociationDownloadService associationDownloadService,
                                 AssociationViewService associationViewService,
                                 SingleSnpMultiSnpAssociationService singleSnpMultiSnpAssociationService,
                                 SnpInteractionAssociationService snpInteractionAssociationService,
                                 LociAttributesService lociAttributesService,
                                 CheckEfoTermAssignmentService checkEfoTermAssignmentService,
                                 AssociationOperationsService associationOperationsService,
                                 MappingService mappingService,
                                 AssociationUploadService associationUploadService,
                                 CurrentUserDetailsService currentUserDetailsService) {
        this.associationRepository = associationRepository;
        this.studyRepository = studyRepository;
        this.efoTraitRepository = efoTraitRepository;
        this.locusRepository = locusRepository;
        this.associationDownloadService = associationDownloadService;
        this.associationViewService = associationViewService;
        this.singleSnpMultiSnpAssociationService = singleSnpMultiSnpAssociationService;
        this.snpInteractionAssociationService = snpInteractionAssociationService;
        this.lociAttributesService = lociAttributesService;
        this.checkEfoTermAssignmentService = checkEfoTermAssignmentService;
        this.associationOperationsService = associationOperationsService;
        this.mappingService = mappingService;
        this.associationUploadService = associationUploadService;
        this.currentUserDetailsService = currentUserDetailsService;
    }
    
    /*  Study SNP/Associations */

    // Generate list of SNP associations linked to a study
    @RequestMapping(value = "/studies/{studyId}/associations",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String viewStudySnps(Model model,
                                @PathVariable Long studyId,
                                @RequestParam(required = false) Long associationId) {

        // Get all associations for a study
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

        // Determine last viewed association
        LastViewedAssociation lastViewedAssociation =
                associationOperationsService.getLastViewedAssociation(associationId);
        model.addAttribute("lastViewedAssociation", lastViewedAssociation);

        // Pass back count of associations
        Integer totalAssociations = associations.size();
        model.addAttribute("totalAssociations", totalAssociations);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "study_association";
    }


    // Upload a spreadsheet of snp association information
    @RequestMapping(value = "/studies/{studyId}/associations/upload",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String uploadStudySnps(@RequestParam("file") MultipartFile file,
                                  @PathVariable Long studyId,
                                  Model model,
                                  HttpServletRequest request) throws IOException {

        // Establish our study object and upload file into study dir
        Study study = studyRepository.findOne(studyId);
        model.addAttribute("study", studyRepository.findOne(studyId));

        List<AssociationUploadErrorView> fileErrors = null;
        try {
            fileErrors =
                    associationUploadService.upload(file, study, currentUserDetailsService.getUserFromRequest(request));
        }
        catch (EnsemblMappingException e) {
            return "ensembl_mapping_failure";
        }

        if (fileErrors != null && !fileErrors.isEmpty()) {
            getLog().error("Errors found in file: " + file.getOriginalFilename());
            model.addAttribute("fileName", file.getOriginalFilename());
            model.addAttribute("fileErrors", fileErrors);

            // Determine if we have any errors rather than warnings
            long errors = fileErrors.stream()
                    .filter(associationUploadErrorView -> !associationUploadErrorView.getWarning())
                    .count();

            if (errors > 0) {
                model.addAttribute("errorsFound", true);
            }

            return "error_pages/association_file_upload_error";
        }
        else {
            return "redirect:/studies/" + studyId + "/associations";
        }
    }

    // Generate a empty form page to add standard snp
    @RequestMapping(value = "/studies/{studyId}/associations/add_standard",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String addStandardSnps(Model model,
                                  @PathVariable Long studyId,
                                  @RequestParam(required = true) String measurementType) {

        // Return form object
        SnpAssociationStandardMultiForm emptyForm = new SnpAssociationStandardMultiForm();

        // Add one row by default and set description
        emptyForm.getSnpFormRows().add(new SnpFormRow());

        // Measurement type determines whether we render a OR/Beta form
        model.addAttribute("form", emptyForm);
        model.addAttribute("measurementType", measurementType);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "add_standard_snp_association";
    }


    // Generate a empty form page to add multi-snp haplotype
    @RequestMapping(value = "/studies/{studyId}/associations/add_multi",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String addMultiSnps(Model model,
                               @PathVariable Long studyId,
                               @RequestParam(required = true) String measurementType) {

        // Return form object
        SnpAssociationStandardMultiForm emptyForm = new SnpAssociationStandardMultiForm();
        emptyForm.setMultiSnpHaplotype(true);

        // Measurement type determines whether we render a OR/Beta form
        model.addAttribute("form", emptyForm);
        model.addAttribute("measurementType", measurementType);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "add_multi_snp_association";
    }

    // Generate a empty form page to add a interaction association
    @RequestMapping(value = "/studies/{studyId}/associations/add_interaction",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String addSnpInteraction(Model model,
                                    @PathVariable Long studyId,
                                    @RequestParam(required = true) String measurementType) {

        // Return form object
        SnpAssociationInteractionForm emptyForm = new SnpAssociationInteractionForm();

        // Measurement type determines whether we render a OR/Beta form
        model.addAttribute("measurementType", measurementType);
        model.addAttribute("form", emptyForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "add_snp_interaction_association";
    }

    // Add multiple rows to table
    @RequestMapping(value = "/studies/{studyId}/associations/add_multi", params = {"addRows"})
    public String addRows(SnpAssociationStandardMultiForm snpAssociationStandardMultiForm,
                          Model model,
                          @PathVariable Long studyId, @RequestParam(required = true) String measurementType) {
        Integer numberOfRows = snpAssociationStandardMultiForm.getMultiSnpHaplotypeNum();

        // Add number of rows curator selected
        while (numberOfRows != 0) {
            snpAssociationStandardMultiForm.getSnpFormRows().add(new SnpFormRow());
            numberOfRows--;
        }
        // Pass back required attributes
        model.addAttribute("measurementType", measurementType);
        model.addAttribute("form", snpAssociationStandardMultiForm);
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_multi_snp_association";
    }

    // Add multiple rows to table
    @RequestMapping(value = "/studies/{studyId}/associations/add_interaction", params = {"addCols"})
    public String addRows(SnpAssociationInteractionForm snpAssociationInteractionForm,
                          Model model,
                          @PathVariable Long studyId, @RequestParam(required = true) String measurementType) {
        Integer numberOfCols = snpAssociationInteractionForm.getNumOfInteractions();

        // Add number of cols curator selected
        while (numberOfCols != 0) {
            snpAssociationInteractionForm.getSnpFormColumns().add(new SnpFormColumn());
            numberOfCols--;
        }

        // Pass back required attributes
        model.addAttribute("form", snpAssociationInteractionForm);
        model.addAttribute("measurementType", measurementType);
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_snp_interaction_association";
    }

    // Add single row to table
    @RequestMapping(value = "/studies/{studyId}/associations/add_multi", params = {"addRow"})
    public String addRow(SnpAssociationStandardMultiForm snpAssociationStandardMultiForm,
                         Model model,
                         @PathVariable Long studyId, @RequestParam(required = true) String measurementType) {

        snpAssociationStandardMultiForm.getSnpFormRows().add(new SnpFormRow());

        // Pass back required attributes
        model.addAttribute("form", snpAssociationStandardMultiForm);
        model.addAttribute("measurementType", measurementType);
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_multi_snp_association";
    }

    // Add single column to table
    @RequestMapping(value = "/studies/{studyId}/associations/add_interaction", params = {"addCol"})
    public String addCol(SnpAssociationInteractionForm snpAssociationInteractionForm,
                         Model model,
                         @PathVariable Long studyId, @RequestParam(required = true) String measurementType) {

        snpAssociationInteractionForm.getSnpFormColumns().add(new SnpFormColumn());

        // Pass back required attributes
        model.addAttribute("form", snpAssociationInteractionForm);
        model.addAttribute("measurementType", measurementType);
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_snp_interaction_association";
    }

    // Remove row from table
    @RequestMapping(value = "/studies/{studyId}/associations/add_multi", params = {"removeRow"})
    public String removeRow(SnpAssociationStandardMultiForm snpAssociationStandardMultiForm,
                            HttpServletRequest req,
                            Model model,
                            @PathVariable Long studyId, @RequestParam(required = true) String measurementType) {

        //Index of value to remove
        final Integer rowId = Integer.valueOf(req.getParameter("removeRow"));

        // Remove row
        snpAssociationStandardMultiForm.getSnpFormRows().remove(rowId.intValue());

        // Pass back required attributes
        model.addAttribute("form", snpAssociationStandardMultiForm);
        model.addAttribute("measurementType", measurementType);
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_multi_snp_association";
    }

    // Remove column from table
    @RequestMapping(value = "/studies/{studyId}/associations/add_interaction", params = {"removeCol"})
    public String removeCol(SnpAssociationInteractionForm snpAssociationInteractionForm,
                            HttpServletRequest req,
                            Model model,
                            @PathVariable Long studyId, @RequestParam(required = true) String measurementType) {

        //Index of value to remove
        final Integer colId = Integer.valueOf(req.getParameter("removeCol"));

        // Remove col
        snpAssociationInteractionForm.getSnpFormColumns().remove(colId.intValue());

        // Pass back required attributes
        model.addAttribute("form", snpAssociationInteractionForm);
        model.addAttribute("measurementType", measurementType);
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_snp_interaction_association";
    }

    // Add new standard association/snp information to a study
    @RequestMapping(value = "/studies/{studyId}/associations/add_standard",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String addStandardSnps(@ModelAttribute SnpAssociationStandardMultiForm snpAssociationStandardMultiForm,
                                  @PathVariable Long studyId,
                                  BindingResult result,
                                  Model model) throws EnsemblMappingException {

        // Check for errors in form
        Boolean hasErrors =
                associationOperationsService.checkSnpAssociationFormErrors(result, snpAssociationStandardMultiForm);

        if (hasErrors) {
            model.addAttribute("form", snpAssociationStandardMultiForm);

            // Also passes back study object to view so we can create links back to main study page
            model.addAttribute("study", studyRepository.findOne(studyId));
            return "add_standard_snp_association";
        }
        else {
            // Get our study object
            Study study = studyRepository.findOne(studyId);

            // Create an association object from details in returned form
            Association newAssociation =
                    singleSnpMultiSnpAssociationService.createAssociation(snpAssociationStandardMultiForm);

            // Set the study ID for our association
            newAssociation.setStudy(study);

            // Save our association information
            newAssociation.setLastUpdateDate(new Date());
            associationRepository.save(newAssociation);

            // Map RS_ID in association
            Collection<Association> associationsToMap = new ArrayList<>();
            associationsToMap.add(newAssociation);
            Curator curator = study.getHousekeeping().getCurator();
            String mappedBy = curator.getLastName();
            try {
                mappingService.validateAndMapAssociations(associationsToMap, mappedBy);
            }
            catch (EnsemblMappingException e) {
                model.addAttribute("study", study);
                return "ensembl_mapping_failure";
            }

            return "redirect:/associations/" + newAssociation.getId();
        }
    }

    @RequestMapping(value = "/studies/{studyId}/associations/add_multi",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String addMultiSnps(@ModelAttribute SnpAssociationStandardMultiForm snpAssociationStandardMultiForm,
                               @PathVariable Long studyId,
                               BindingResult result,
                               Model model) throws EnsemblMappingException {

        // Check for errors in form
        Boolean hasErrors =
                associationOperationsService.checkSnpAssociationFormErrors(result, snpAssociationStandardMultiForm);

        if (hasErrors) {
            model.addAttribute("form", snpAssociationStandardMultiForm);

            // Also passes back study object to view so we can create links back to main study page
            model.addAttribute("study", studyRepository.findOne(studyId));
            return "add_multi_snp_association";
        }
        else {

            // Get our study object
            Study study = studyRepository.findOne(studyId);

            // Create an association object from details in returned form
            Association newAssociation =
                    singleSnpMultiSnpAssociationService.createAssociation(snpAssociationStandardMultiForm);

            // Set the study ID for our association
            newAssociation.setStudy(study);

            // Save our association information
            newAssociation.setLastUpdateDate(new Date());
            associationRepository.save(newAssociation);

            // Map RS_ID in association
            Collection<Association> associationsToMap = new ArrayList<>();
            associationsToMap.add(newAssociation);
            Curator curator = study.getHousekeeping().getCurator();
            String mappedBy = curator.getLastName();
            try {
                mappingService.validateAndMapAssociations(associationsToMap, mappedBy);
            }
            catch (EnsemblMappingException e) {
                model.addAttribute("study", study);
                return "ensembl_mapping_failure";
            }

            return "redirect:/associations/" + newAssociation.getId();
        }
    }

    @RequestMapping(value = "/studies/{studyId}/associations/add_interaction",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String addSnpInteraction(@ModelAttribute SnpAssociationInteractionForm snpAssociationInteractionForm,
                                    @PathVariable Long studyId, BindingResult result, Model model)
            throws EnsemblMappingException {

        // Check for errors in form
        Boolean hasErrors = associationOperationsService.checkSnpAssociationInteractionFormErrors(result,
                                                                                                  snpAssociationInteractionForm);

        if (hasErrors) {
            model.addAttribute("form", snpAssociationInteractionForm);

            // Also passes back study object to view so we can create links back to main study page
            model.addAttribute("study", studyRepository.findOne(studyId));
            return "add_snp_interaction_association";
        }
        else {
            // Get our study object
            Study study = studyRepository.findOne(studyId);

            // Create an association object from details in returned form
            Association newAssociation =
                    snpInteractionAssociationService.createAssociation(snpAssociationInteractionForm);

            // Set the study ID for our association
            newAssociation.setStudy(study);

            // Save our association information
            newAssociation.setLastUpdateDate(new Date());
            associationRepository.save(newAssociation);

            // Map RS_ID in association
            Collection<Association> associationsToMap = new ArrayList<>();
            associationsToMap.add(newAssociation);
            Curator curator = study.getHousekeeping().getCurator();
            String mappedBy = curator.getLastName();
            try {
                mappingService.validateAndMapAssociations(associationsToMap, mappedBy);
            }
            catch (EnsemblMappingException e) {
                model.addAttribute("study", study);
                return "ensembl_mapping_failure";
            }

            return "redirect:/associations/" + newAssociation.getId();
        }
    }

     /* Existing association information */

    // View association information
    @RequestMapping(value = "/associations/{associationId}",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String viewAssociation(Model model, @PathVariable Long associationId) {

        // Return association with that ID
        Association associationToView = associationRepository.findOne(associationId);

        // Get mapping details
        MappingDetails mappingDetails = associationOperationsService.createMappingDetails(associationToView);
        model.addAttribute("mappingDetails", mappingDetails);

        // Return any association errors
        model.addAttribute("errors", associationOperationsService.getAssociationWarnings(associationId));

        // Establish study
        Long studyId = associationToView.getStudy().getId();

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));

        // Determine if association is an OR or BETA type
        String measurementType = associationOperationsService.determineIfAssociationIsOrType(associationToView);
        model.addAttribute("measurementType", measurementType);

        // Determine form to return
        SnpAssociationForm form = associationOperationsService.generateForm(associationToView);
        model.addAttribute("form", form);

        // Determine page to return
        if (associationToView.getSnpInteraction() != null && associationToView.getSnpInteraction()) {
            return "edit_snp_interaction_association";
        }

        else if (associationToView.getMultiSnpHaplotype() != null && associationToView.getMultiSnpHaplotype()) {
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
    // TODO COULD REFACTOR TO JUST USE SUPERCLASS AS METHOD PARAMETER
    public String editAssociation(@ModelAttribute SnpAssociationStandardMultiForm snpAssociationStandardMultiForm,
                                  BindingResult snpAssociationFormBindingResult,
                                  @ModelAttribute SnpAssociationInteractionForm snpAssociationInteractionForm,
                                  BindingResult snpAssociationInteractionFormBindingResult,
                                  @PathVariable Long associationId,
                                  @RequestParam(value = "associationtype", required = true) String associationType,
                                  Model model) throws EnsemblMappingException {


        // Validate returned form depending on association type
        Boolean hasErrors;
        if (associationType.equalsIgnoreCase("interaction")) {
            hasErrors = associationOperationsService.checkSnpAssociationInteractionFormErrors(
                    snpAssociationInteractionFormBindingResult,
                    snpAssociationInteractionForm);
        }
        else {
            hasErrors = associationOperationsService.checkSnpAssociationFormErrors(snpAssociationFormBindingResult,
                                                                                   snpAssociationStandardMultiForm);
        }

        // If errors found then return the edit form with all information entered by curator preserved
        if (hasErrors) {

            // Return association with that ID
            Association associationToEdit = associationRepository.findOne(associationId);

            // Get mapping details
            MappingDetails mappingDetails = associationOperationsService.createMappingDetails(associationToEdit);
            model.addAttribute("mappingDetails", mappingDetails);

            // Return any association errors
            model.addAttribute("errors", associationOperationsService.getAssociationWarnings(associationId));

            // Establish study
            Long studyId = associationToEdit.getStudy().getId();

            // Also passes back study object to view so we can create links back to main study page
            model.addAttribute("study", studyRepository.findOne(studyId));

            if (associationType.equalsIgnoreCase("interaction")) {
                model.addAttribute("form", snpAssociationInteractionForm);
                return "edit_snp_interaction_association";
            }
            else {
                model.addAttribute("form", snpAssociationStandardMultiForm);

                // Determine view
                if (associationToEdit.getMultiSnpHaplotype()) {
                    return "edit_multi_snp_association";
                }
                else {
                    return "edit_standard_snp_association";
                }
            }
        }
        else {
            //Create association
            Association editedAssociation;

            // Request parameter determines how to process form and also which form to process
            if (associationType.equalsIgnoreCase("interaction")) {
                editedAssociation =
                        snpInteractionAssociationService.createAssociation(snpAssociationInteractionForm);
            }
            else {
                editedAssociation =
                        singleSnpMultiSnpAssociationService.createAssociation(snpAssociationStandardMultiForm);
            }

            // Set ID of new  association to the ID of the association we're currently editing
            editedAssociation.setId(associationId);

            // Set study to one currently linked to association
            Association currentAssociation = associationRepository.findOne(associationId);
            Study associationStudy = currentAssociation.getStudy();
            editedAssociation.setStudy(associationStudy);

            // Save our association information
            editedAssociation.setLastUpdateDate(new Date());
            associationRepository.save(editedAssociation);

            // Map RS_ID in association
            Collection<Association> associationsToMap = new ArrayList<>();
            associationsToMap.add(editedAssociation);
            Curator curator = associationStudy.getHousekeeping().getCurator();
            String mappedBy = curator.getLastName();
            try {
                mappingService.validateAndMapAssociations(associationsToMap, mappedBy);
            }
            catch (EnsemblMappingException e) {
                model.addAttribute("study", associationStudy);
                return "ensembl_mapping_failure";
            }

            return "redirect:/associations/" + associationId;
        }
    }

    // Add single row to table
    @RequestMapping(value = "/associations/{associationId}", params = {"addRow"})
    public String addRowEditMode(SnpAssociationStandardMultiForm snpAssociationStandardMultiForm,
                                 Model model,
                                 @PathVariable Long associationId) {

        snpAssociationStandardMultiForm.getSnpFormRows().add(new SnpFormRow());

        // Pass back updated form
        model.addAttribute("form", snpAssociationStandardMultiForm);

        // Also passes back study object to view so we can create links back to main study page
        Association currentAssociation = associationRepository.findOne(associationId);
        Study associationStudy = currentAssociation.getStudy();
        Long studyId = associationStudy.getId();
        model.addAttribute("study", studyRepository.findOne(studyId));

        // Determine if association is an OR or BETA type
        String measurementType = associationOperationsService.determineIfAssociationIsOrType(currentAssociation);
        model.addAttribute("measurementType", measurementType);

        // Get mapping details
        MappingDetails mappingDetails = associationOperationsService.createMappingDetails(currentAssociation);
        model.addAttribute("mappingDetails", mappingDetails);

        // Return any association errors
        model.addAttribute("errors", associationOperationsService.getAssociationWarnings(associationId));

        return "edit_multi_snp_association";
    }

    // Add single column to table
    @RequestMapping(value = "/associations/{associationId}", params = {"addCol"})
    public String addColEditMode(SnpAssociationInteractionForm snpAssociationInteractionForm,
                                 Model model,
                                 @PathVariable Long associationId) {

        snpAssociationInteractionForm.getSnpFormColumns().add(new SnpFormColumn());

        // Pass back updated form
        model.addAttribute("form", snpAssociationInteractionForm);

        // Also passes back study object to view so we can create links back to main study page
        Association currentAssociation = associationRepository.findOne(associationId);
        Study associationStudy = currentAssociation.getStudy();
        Long studyId = associationStudy.getId();
        model.addAttribute("study", studyRepository.findOne(studyId));

        // Determine if association is an OR or BETA type
        String measurementType = associationOperationsService.determineIfAssociationIsOrType(currentAssociation);
        model.addAttribute("measurementType", measurementType);

        // Get mapping details
        MappingDetails mappingDetails = associationOperationsService.createMappingDetails(currentAssociation);
        model.addAttribute("mappingDetails", mappingDetails);

        // Return any association errors
        model.addAttribute("errors", associationOperationsService.getAssociationWarnings(associationId));

        return "edit_snp_interaction_association";
    }

    // Remove row from table
    @RequestMapping(value = "/associations/{associationId}", params = {"removeRow"})
    public String removeRowEditMode(SnpAssociationStandardMultiForm snpAssociationStandardMultiForm,
                                    HttpServletRequest req,
                                    Model model,
                                    @PathVariable Long associationId) {

        //Index of value to remove
        final Integer rowId = Integer.valueOf(req.getParameter("removeRow"));

        // Remove row
        snpAssociationStandardMultiForm.getSnpFormRows().remove(rowId.intValue());

        // Pass back updated form
        model.addAttribute("form", snpAssociationStandardMultiForm);

        // Also passes back study object to view so we can create links back to main study page
        Association currentAssociation = associationRepository.findOne(associationId);
        Study associationStudy = currentAssociation.getStudy();
        Long studyId = associationStudy.getId();
        model.addAttribute("study", studyRepository.findOne(studyId));

        // Determine if association is an OR or BETA type
        String measurementType = associationOperationsService.determineIfAssociationIsOrType(currentAssociation);
        model.addAttribute("measurementType", measurementType);

        // Get mapping details
        MappingDetails mappingDetails = associationOperationsService.createMappingDetails(currentAssociation);
        model.addAttribute("mappingDetails", mappingDetails);

        // Return any association errors
        model.addAttribute("errors", associationOperationsService.getAssociationWarnings(associationId));

        return "edit_multi_snp_association";
    }

    // Remove column from table
    @RequestMapping(value = "/associations/{associationId}", params = {"removeCol"})
    public String removeColEditMode(SnpAssociationInteractionForm snpAssociationInteractionForm,
                                    HttpServletRequest req,
                                    Model model,
                                    @PathVariable Long associationId) {

        //Index of value to remove
        final Integer colId = Integer.valueOf(req.getParameter("removeCol"));

        // Remove col
        snpAssociationInteractionForm.getSnpFormColumns().remove(colId.intValue());

        // Pass back updated form
        model.addAttribute("form", snpAssociationInteractionForm);

        // Also passes back study object to view so we can create links back to main study page
        Association currentAssociation = associationRepository.findOne(associationId);
        Study associationStudy = currentAssociation.getStudy();
        Long studyId = associationStudy.getId();
        model.addAttribute("study", studyRepository.findOne(studyId));

        // Determine if association is an OR or BETA type
        String measurementType = associationOperationsService.determineIfAssociationIsOrType(currentAssociation);
        model.addAttribute("measurementType", measurementType);

        // Get mapping details
        MappingDetails mappingDetails = associationOperationsService.createMappingDetails(currentAssociation);
        model.addAttribute("mappingDetails", mappingDetails);

        // Return any association errors
        model.addAttribute("errors", associationOperationsService.getAssociationWarnings(associationId));

        return "edit_snp_interaction_association";
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


    // Approve a single SNP association
    @RequestMapping(value = "associations/{associationId}/approve",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String approveSnpAssociation(@PathVariable Long associationId, RedirectAttributes redirectAttributes) {

        Association association = associationRepository.findOne(associationId);

        // Check if association has an EFO trait
        Boolean associationEfoTermsAssigned =
                checkEfoTermAssignmentService.checkAssociationEfoAssignment(association);

        if (!associationEfoTermsAssigned) {
            String message = "Cannot approve association as no EFO trait assigned";
            redirectAttributes.addFlashAttribute("efoMessage", message);
        }
        else {
            // Mark errors as checked
            associationOperationsService.associationErrorsChecked(association);

            // Set snpChecked attribute to true
            association.setSnpApproved(true);
            association.setLastUpdateDate(new Date());
            associationRepository.save(association);
        }

        return "redirect:/studies/" + association.getStudy().getId() + "/associations";
    }


    // Un-approve a single SNP association
    @RequestMapping(value = "associations/{associationId}/unapprove",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String unapproveSnpAssociation(@PathVariable Long associationId) {

        Association association = associationRepository.findOne(associationId);

        // Mark errors as unchecked
        associationOperationsService.associationErrorsUnchecked(association);

        // Set snpChecked attribute to true
        association.setSnpApproved(false);
        association.setLastUpdateDate(new Date());
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

        // Create a collection of all association objects and check EFO term assignment
        Collection<Association> allAssociations = new ArrayList<>();
        for (String associationId : associationsIds) {
            Association association = associationRepository.findOne(Long.valueOf(associationId));
            allAssociations.add(association);
        }
        Boolean associationsEfoTermsAssigned =
                checkEfoTermAssignmentService.checkAssociationsEfoAssignment(allAssociations);

        if (!associationsEfoTermsAssigned) {
            message = "Cannot approve association(s) as no EFO trait assigned";
        }

        else {
            // For each one set snpChecked attribute to true
            for (String associationId : associationsIds) {
                Association association = associationRepository.findOne(Long.valueOf(associationId));

                // Mark errors as checked
                associationOperationsService.associationErrorsChecked(association);

                association.setSnpApproved(true);
                association.setLastUpdateDate(new Date());
                associationRepository.save(association);
                count++;
            }
            message = "Successfully updated " + count + " associations";
        }
        Map<String, String> result = new HashMap<>();
        result.put("message", message);
        return result;
    }

    // Un-approve checked SNPs
    @RequestMapping(value = "/studies/{studyId}/associations/unapprove_checked",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> unapproveChecked(@RequestParam(value = "associationIds[]") String[] associationsIds) {

        String message = "";
        Integer count = 0;

        // For each one set snpChecked attribute to true
        for (String associationId : associationsIds) {
            Association association = associationRepository.findOne(Long.valueOf(associationId));

            // Mark errors as checked
            associationOperationsService.associationErrorsUnchecked(association);

            association.setSnpApproved(false);
            association.setLastUpdateDate(new Date());
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
    public String approveAll(@PathVariable Long studyId, RedirectAttributes redirectAttributes) {

        // Get all associations
        Collection<Association> studyAssociations = associationRepository.findByStudyId(studyId);
        Boolean associationEfoTermsAssigned =
                checkEfoTermAssignmentService.checkAssociationsEfoAssignment(studyAssociations);

        if (!associationEfoTermsAssigned) {
            String message = "Cannot approve all associations as no EFO trait assigned";
            redirectAttributes.addFlashAttribute("efoMessage", message);
        }

        else {
            // For each one set snpChecked attribute to true
            for (Association association : studyAssociations) {
                // Mark errors as checked
                associationOperationsService.associationErrorsChecked(association);

                association.setSnpApproved(true);
                association.setLastUpdateDate(new Date());
                associationRepository.save(association);
            }
        }
        return "redirect:/studies/" + studyId + "/associations";
    }

    /**
     * Run mapping pipeline on all SNPs in a study
     *
     * @param studyId            Study ID in database
     * @param redirectAttributes attributes for a redirect scenario
     */
    @RequestMapping(value = "/studies/{studyId}/associations/validate_all",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String validateAll(@PathVariable Long studyId, RedirectAttributes redirectAttributes, Model model)
            throws EnsemblMappingException {

        // For the study get all associations
        Collection<Association> studyAssociations = associationRepository.findByStudyId(studyId);

        Study study = studyRepository.findOne(studyId);
        Curator curator = study.getHousekeeping().getCurator();
        String mappedBy = curator.getLastName();
        try {
            mappingService.validateAndMapAssociations(studyAssociations, mappedBy);
        }
        catch (EnsemblMappingException e) {
            model.addAttribute("study", study);
            return "ensembl_mapping_failure";
        }

        String message = "Mapping complete, please check for any errors displayed in the 'Errors' column";
        redirectAttributes.addFlashAttribute("mappingComplete", message);
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
                    study.getAuthor()
                            .concat("-")
                            .concat(study.getPubmedId())
                            .concat("-")
                            .concat(now)
                            .concat(".tsv");
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
                                           @RequestParam(value = "e",
                                                         required = false,
                                                         defaultValue = "false") boolean existing,
                                           @RequestParam(value = "o",
                                                         required = false,
                                                         defaultValue = "true") boolean overwrite)
            throws IOException {

        Collection<Association> associations = new ArrayList<>();
        associations.addAll(associationRepository.findByStudyId(studyId));
        Study study = studyRepository.findOne((studyId));
        Collection<EfoTrait> efoTraits = study.getEfoTraits();


        if (associations.size() == 0 || efoTraits.size() == 0) {
            model.addAttribute("study", study);
            return "no_association_efo_trait_warning";
        }
        else {
            if (!existing) {
                for (Association association : associations) {
                    if (association.getEfoTraits().size() != 0) {
                        model.addAttribute("study", study);
                        return "existing_efo_traits_warning";
                    }
                }
            }
            Collection<EfoTrait> associationTraits = new ArrayList<EfoTrait>();

            for (EfoTrait efoTrait : efoTraits) {
                associationTraits.add(efoTrait);
            }

            for (Association association : associations) {
                if (association.getEfoTraits().size() != 0 && !overwrite) {
                    for (EfoTrait trait : associationTraits) {
                        if (!association.getEfoTraits().contains(trait)) {
                            association.addEfoTrait(trait);
                        }
                    }
                }
                else {
                    association.setEfoTraits(associationTraits);
                }
                association.setLastUpdateDate(new Date());
                associationRepository.save(association);
            }

            return "redirect:/studies/" + studyId + "/associations";
        }
    }

    /* Exception handling */
    @ExceptionHandler(DataIntegrityException.class)
    public String handleDataIntegrityException(DataIntegrityException dataIntegrityException) {
        return dataIntegrityException.getMessage();
    }

    @ExceptionHandler({SheetProcessingException.class})
    public String handleInvalidFormatExceptionAndInvalidOperationException() {
        return "error_pages/wrong_file_format_warning";
    }

    @ExceptionHandler({IOException.class})
    public String handleIOException() {
        return "error_pages/data_upload_problem";
    }

    @ExceptionHandler({FileUploadException.class})
    public String handleFileUploadException() {
        return "error_pages/empty_snpfile_upload_warning";
    }

    @ExceptionHandler({FileNotFoundException.class})
    public String handleFileNotFound() {
        return "error_pages/file_not_found";
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