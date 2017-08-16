package uk.ac.ebi.spot.goci.curation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
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
import uk.ac.ebi.spot.goci.curation.model.AssociationValidationView;
import uk.ac.ebi.spot.goci.curation.model.LastViewedAssociation;
import uk.ac.ebi.spot.goci.curation.model.MappingDetails;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationForm;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationInteractionForm;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationStandardMultiForm;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationTableView;
import uk.ac.ebi.spot.goci.curation.model.SnpFormColumn;
import uk.ac.ebi.spot.goci.curation.model.SnpFormRow;
import uk.ac.ebi.spot.goci.curation.service.AssociationDeletionService;
import uk.ac.ebi.spot.goci.curation.service.AssociationDownloadService;
import uk.ac.ebi.spot.goci.curation.service.AssociationOperationsService;
import uk.ac.ebi.spot.goci.curation.service.AssociationUploadService;
import uk.ac.ebi.spot.goci.curation.service.AssociationValidationReportService;
import uk.ac.ebi.spot.goci.curation.service.CheckEfoTermAssignmentService;
import uk.ac.ebi.spot.goci.curation.service.CheckMappingService;
import uk.ac.ebi.spot.goci.curation.service.CurrentUserDetailsService;
import uk.ac.ebi.spot.goci.curation.service.EventsViewService;
import uk.ac.ebi.spot.goci.curation.service.SingleSnpMultiSnpAssociationService;
import uk.ac.ebi.spot.goci.curation.service.SnpAssociationTableViewService;
import uk.ac.ebi.spot.goci.curation.service.SnpInteractionAssociationService;
import uk.ac.ebi.spot.goci.curation.service.StudyAssociationBatchDeletionEventService;
import uk.ac.ebi.spot.goci.exception.EnsemblMappingException;
import uk.ac.ebi.spot.goci.exception.SheetProcessingException;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationReport;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.AssociationService;
import uk.ac.ebi.spot.goci.service.EnsemblRestTemplateService;
import uk.ac.ebi.spot.goci.service.MapCatalogService;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

    // Services
    private AssociationDownloadService associationDownloadService;
    private SnpAssociationTableViewService snpAssociationTableViewService;
    private SingleSnpMultiSnpAssociationService singleSnpMultiSnpAssociationService;
    private SnpInteractionAssociationService snpInteractionAssociationService;
    private CheckEfoTermAssignmentService checkEfoTermAssignmentService;
    private AssociationOperationsService associationOperationsService;
    private AssociationUploadService associationUploadService;
    private CurrentUserDetailsService currentUserDetailsService;
    private AssociationValidationReportService associationValidationReportService;
    private AssociationDeletionService associationDeletionService;
    private EventsViewService eventsViewService;
    private StudyAssociationBatchDeletionEventService studyAssociationBatchDeletionEventService;
    private AssociationService associationService;
    private EnsemblRestTemplateService ensemblRestTemplateService;
    private CheckMappingService checkMappingService;
    private MapCatalogService mapCatalogService;


    @Value("${collection.sizelimit}")
    private int collectionLimit;

    private final ExecutorService executorService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @InitBinder(value={"snpAssociationStandardMultiForm", "snpAssociationInteractionForm"})
    public void initBinder(WebDataBinder dataBinder) {
        //System.out.println("A binder for object: " + dataBinder.getObjectName());
        dataBinder.setAutoGrowCollectionLimit(collectionLimit);
    }

    @Autowired
    public AssociationController(AssociationRepository associationRepository,
                                 StudyRepository studyRepository,
                                 EfoTraitRepository efoTraitRepository,
                                 AssociationDownloadService associationDownloadService,
                                 SnpAssociationTableViewService snpAssociationTableViewService,
                                 SingleSnpMultiSnpAssociationService singleSnpMultiSnpAssociationService,
                                 SnpInteractionAssociationService snpInteractionAssociationService,
                                 CheckEfoTermAssignmentService checkEfoTermAssignmentService,
                                 AssociationOperationsService associationOperationsService,
                                 AssociationUploadService associationUploadService,
                                 CurrentUserDetailsService currentUserDetailsService,
                                 AssociationValidationReportService associationValidationReportService,
                                 AssociationDeletionService associationDeletionService,
                                 @Qualifier("associationEventsViewService") EventsViewService eventsViewService,
                                 StudyAssociationBatchDeletionEventService studyAssociationBatchDeletionEventService,
                                 EnsemblRestTemplateService ensemblRestTemplateService,
                                 CheckMappingService checkMappingService,
                                 MapCatalogService mapCatalogService,
                                 AssociationService associationService) {
        this.associationRepository = associationRepository;
        this.studyRepository = studyRepository;
        this.efoTraitRepository = efoTraitRepository;
        this.associationDownloadService = associationDownloadService;
        this.snpAssociationTableViewService = snpAssociationTableViewService;
        this.singleSnpMultiSnpAssociationService = singleSnpMultiSnpAssociationService;
        this.snpInteractionAssociationService = snpInteractionAssociationService;
        this.checkEfoTermAssignmentService = checkEfoTermAssignmentService;
        this.associationOperationsService = associationOperationsService;
        this.associationUploadService = associationUploadService;
        this.currentUserDetailsService = currentUserDetailsService;
        this.associationValidationReportService = associationValidationReportService;
        this.associationDeletionService = associationDeletionService;
        this.eventsViewService = eventsViewService;
        this.studyAssociationBatchDeletionEventService = studyAssociationBatchDeletionEventService;
        this.associationService = associationService;
        this.ensemblRestTemplateService = ensemblRestTemplateService;
        this.checkMappingService = checkMappingService;
        this.mapCatalogService = mapCatalogService;

        this.executorService = Executors.newFixedThreadPool(4);

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
        Collection<Association> associations = associationRepository.findByStudyId(studyId);

        // For our associations create a table view object and return
        Collection<SnpAssociationTableView> snpAssociationTableViews = new ArrayList<SnpAssociationTableView>();
        for (Association association : associations) {
            SnpAssociationTableView snpAssociationTableView =
                    snpAssociationTableViewService.createSnpAssociationTableView(association);
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


    @RequestMapping(value = "studies/{studyId}/association_tracking",
            produces = MediaType.TEXT_HTML_VALUE,
            method = RequestMethod.GET)
    public String getAssociationEvents(Model model, @PathVariable Long studyId) {
        model.addAttribute("events", eventsViewService.createViews(studyId));
        model.addAttribute("study", studyRepository.findOne(studyId));
        return "association_events";
    }


    // Upload a spreadsheet of snp association information
    @RequestMapping(value = "/studies/{studyId}/associations/upload",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public Callable<String> uploadStudySnps(@RequestParam("file") MultipartFile file,
                                    @PathVariable Long studyId,
                                    Model model,
                                    HttpServletRequest request,
                                    HttpSession session)
            throws IOException, ExecutionException, InterruptedException {

        Enumeration<String> sessionAttr = session.getAttributeNames();

        while(sessionAttr.hasMoreElements()){
            String attr = sessionAttr.nextElement();
            if(!attr.equals("SPRING_SECURITY_CONTEXT")){
                session.removeAttribute(attr);
            }
        }

        // Establish our study object and upload file into study dir
        Study study = studyRepository.findOne(studyId);
        model.addAttribute("study", study);

        session.setAttribute("done", false);

        SecureUser user =  currentUserDetailsService.getUserFromRequest(request);

        // Return holding screen or error message
        return () -> {
                model.addAttribute("status", "201");
                model.addAttribute("uploadProgress", "true");
                model.addAttribute("processType", "upload");

                performUpload(model, session, file, user, study);

                return "association_upload_progress";
        };
    }

    // Generate a empty form page to add standard snp
    @RequestMapping(value = "/studies/{studyId}/associations/add_standard",
            produces = MediaType.TEXT_HTML_VALUE,
            method = RequestMethod.GET)
    public String addStandardSnpsView(Model model,
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
    public String addMultiSnpsView(Model model,
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
    public String addSnpInteractionView(Model model,
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
    public String addStandardSnps(@ModelAttribute("form") @Valid SnpAssociationStandardMultiForm snpAssociationStandardMultiForm,
                                  BindingResult bindingResult,
                                  @PathVariable Long studyId,
                                  Model model,
                                  @RequestParam(required = true) String measurementType,
                                  HttpServletRequest request)
            throws EnsemblMappingException {

        Study study = studyRepository.findOne(studyId);
        model.addAttribute("study", study);
        model.addAttribute("measurementType", measurementType);


        // Binding vs Validator issue. File: messages.properties
        if (bindingResult.hasErrors()) {
            model.addAttribute("form", snpAssociationStandardMultiForm);
            return "add_standard_snp_association";
        }


        // Check for errors in form that would prevent saving an association
        List<AssociationValidationView> rowErrors =
                associationOperationsService.checkSnpAssociationFormErrors(snpAssociationStandardMultiForm,
                        measurementType);

        if (!rowErrors.isEmpty()) {
            model.addAttribute("errors", rowErrors);
            model.addAttribute("form", snpAssociationStandardMultiForm);
            model.addAttribute("criticalErrorsFound", true);
            return "add_standard_snp_association";
        }
        else {
            // Create an association object from details in returned form
            Association newAssociation =
                    singleSnpMultiSnpAssociationService.createAssociation(snpAssociationStandardMultiForm);

            // Save and validate form
            String eRelease = ensemblRestTemplateService.getRelease();
            Collection<AssociationValidationView> errors = null;
            try {
                errors = associationOperationsService.saveAssociationCreatedFromForm(study,
                        newAssociation,
                        currentUserDetailsService.getUserFromRequest(
                                request), eRelease);
            }
            catch (EnsemblMappingException e) {
                return "ensembl_mapping_failure";
            }

            // Determine if we have any errors rather than warnings
            long errorCount = errors.stream()
                    .filter(validationError -> !validationError.getWarning())
                    .count();

            if (errorCount > 0) {
                model.addAttribute("errors", errors);
                model.addAttribute("form", snpAssociationStandardMultiForm);
                model.addAttribute("criticalErrorsFound", true);
                return "add_standard_snp_association";
            }
            else {
                return "redirect:/associations/" + newAssociation.getId();
            }
        }
    }

    @RequestMapping(value = "/studies/{studyId}/associations/add_multi",
            produces = MediaType.TEXT_HTML_VALUE,
            method = RequestMethod.POST)
    public String addMultiSnps(@ModelAttribute("form") @Valid SnpAssociationStandardMultiForm snpAssociationStandardMultiForm,
                               BindingResult bindingResult,
                               @PathVariable Long studyId,
                               Model model,
                               @RequestParam(required = true) String measurementType,
                               HttpServletRequest request)
            throws EnsemblMappingException {

        Study study = studyRepository.findOne(studyId);
        model.addAttribute("study", study);
        model.addAttribute("measurementType", measurementType);

        // Binding vs Validator issue. File: messages.properties
        if (bindingResult.hasErrors()) {
            model.addAttribute("form", snpAssociationStandardMultiForm);
            return "add_multi_snp_association";
        }


        // Check for errors in form that would prevent saving an association
        List<AssociationValidationView> rowErrors =
                associationOperationsService.checkSnpAssociationFormErrors(snpAssociationStandardMultiForm,
                        measurementType);

        if (!rowErrors.isEmpty()) {
            model.addAttribute("errors", rowErrors);
            model.addAttribute("form", snpAssociationStandardMultiForm);
            model.addAttribute("criticalErrorsFound", true);
            return "add_multi_snp_association";
        }
        else {

            // Create an association object from details in returned form
            Association newAssociation =
                    singleSnpMultiSnpAssociationService.createAssociation(snpAssociationStandardMultiForm);

            // Save and validate form
            String eRelease = ensemblRestTemplateService.getRelease();
            Collection<AssociationValidationView> errors = null;
            try {
                errors = associationOperationsService.saveAssociationCreatedFromForm(study, newAssociation,
                        currentUserDetailsService.getUserFromRequest(
                                request), eRelease);
            }
            catch (EnsemblMappingException e) {
                return "ensembl_mapping_failure";
            }

            // Determine if we have any errors rather than warnings
            long errorCount = errors.stream()
                    .filter(validationError -> !validationError.getWarning())
                    .count();

            if (errorCount > 0) {
                model.addAttribute("errors", errors);
                model.addAttribute("form", snpAssociationStandardMultiForm);
                model.addAttribute("criticalErrorsFound", true);
                return "add_multi_snp_association";
            }
            else {
                return "redirect:/associations/" + newAssociation.getId();
            }
        }
    }

    @RequestMapping(value = "/studies/{studyId}/associations/add_interaction",
            produces = MediaType.TEXT_HTML_VALUE,
            method = RequestMethod.POST)
    public String addSnpInteraction(@ModelAttribute("form") @Valid SnpAssociationInteractionForm snpAssociationInteractionForm,
                                    BindingResult bindingResult,
                                    @PathVariable Long studyId,
                                    Model model,
                                    @RequestParam(required = true) String measurementType, HttpServletRequest request)
            throws EnsemblMappingException {

        Study study = studyRepository.findOne(studyId);
        model.addAttribute("study", study);
        model.addAttribute("measurementType", measurementType);

        // Binding vs Validator issue. File: messages.properties
        if (bindingResult.hasErrors()) {
            model.addAttribute("form", snpAssociationInteractionForm);
            return "add_snp_interaction_association";
        }


        // Check for errors in form that would prevent saving an association
        List<AssociationValidationView> colErrors =
                associationOperationsService.checkSnpAssociationInteractionFormErrors(snpAssociationInteractionForm,
                        measurementType);

        if (!colErrors.isEmpty()) {
            model.addAttribute("errors", colErrors);
            model.addAttribute("form", snpAssociationInteractionForm);
            model.addAttribute("criticalErrorsFound", true);
            return "add_snp_interaction_association";
        }
        else {
            // Create an association object from details in returned form
            Association newAssociation =
                    snpInteractionAssociationService.createAssociation(snpAssociationInteractionForm);

            // Save and validate form
            Collection<AssociationValidationView> errors = null;
            String eRelease = ensemblRestTemplateService.getRelease();
            try {
                errors = associationOperationsService.saveAssociationCreatedFromForm(study, newAssociation,
                        currentUserDetailsService.getUserFromRequest(
                                request), eRelease);
            }
            catch (EnsemblMappingException e) {
                return "ensembl_mapping_failure";
            }

            // Determine if we have any errors rather than warnings
            long errorCount = errors.stream()
                    .filter(validationError -> !validationError.getWarning())
                    .count();

            if (errorCount > 0) {
                model.addAttribute("errors", errors);
                model.addAttribute("form", snpAssociationInteractionForm);
                model.addAttribute("criticalErrorsFound", true);
                return "add_snp_interaction_association";
            }
            else {
                return "redirect:/associations/" + newAssociation.getId();
            }
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
        model.addAttribute("errors",
                associationValidationReportService.generateAssociationWarningsListView(associationId));

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
    // We tried to remap if the snp or genes changed.
    // TODO : implement something for SNP:SNP iteration. Actually we remap.
    @RequestMapping(value = "/associations/{associationId}",
            produces = MediaType.TEXT_HTML_VALUE,
            method = RequestMethod.POST)
    // TODO COULD REFACTOR TO JUST USE SUPERCLASS AS METHOD PARAMETER
    public String editAssociation(@ModelAttribute SnpAssociationStandardMultiForm snpAssociationStandardMultiForm,
                                  @ModelAttribute SnpAssociationInteractionForm snpAssociationInteractionForm,
                                  @PathVariable Long associationId,
                                  @RequestParam(value = "associationtype", required = true) String associationType,
                                  Model model, HttpServletRequest request,
                                  RedirectAttributes redirectAttributes) throws EnsemblMappingException {


        // Establish study and association we are editing
        Collection<String> previousAuthorReportedGenes = new HashSet<>();
        Collection<String> authorReportedGenes = new HashSet<>();
        Collection<String> previousSnps = new HashSet<>();
        Collection<String> snps = new HashSet<>();
        String isToRemapping = "yes";

        Association associationToEdit = associationRepository.findOne(associationId);
        Long studyId = associationToEdit.getStudy().getId();
        Study study = studyRepository.findOne(studyId);
        model.addAttribute("study", study);

        AssociationReport oldAssociationReport = associationToEdit.getAssociationReport();
        previousAuthorReportedGenes= associationOperationsService.getGenesIds(associationToEdit.getLoci());
        previousSnps = associationOperationsService.getSpnsName(associationToEdit.getSnps());

        // Determine if association is an OR or BETA type
        String measurementType = associationOperationsService.determineIfAssociationIsOrType(associationToEdit);
        model.addAttribute("measurementType", measurementType);

        // Validate returned form depending on association type
        List<AssociationValidationView> criticalErrors = new ArrayList<>();
        if (associationType.equalsIgnoreCase("interaction")) {
            criticalErrors =
                    associationOperationsService.checkSnpAssociationInteractionFormErrors(snpAssociationInteractionForm,
                            measurementType);
        }
        else {
            criticalErrors =
                    associationOperationsService.checkSnpAssociationFormErrors(snpAssociationStandardMultiForm,
                            measurementType);
        }

        // If errors found then return the edit form with all information entered by curator preserved
        if (!criticalErrors.isEmpty()) {

            // Get mapping details
            model.addAttribute("mappingDetails", associationOperationsService.createMappingDetails(associationToEdit));

            // Return any association errors
            model.addAttribute("errors", criticalErrors);
            model.addAttribute("criticalErrorsFound", true);

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

                // New snps to compare with the previousSnps.
                Collection<SnpFormRow> newSnpsList = snpAssociationStandardMultiForm.getSnpFormRows();
                if (newSnpsList != null && !newSnpsList.isEmpty()) {
                    for (SnpFormRow snp : newSnpsList) {
                        snps.add(snp.getSnp());
                    }
                }
            }


            authorReportedGenes = associationOperationsService.getGenesIds(editedAssociation.getLoci());


            if (oldAssociationReport != null) {
                if ( (previousAuthorReportedGenes.size() == authorReportedGenes.size())
                        && (snps.size() == snps.size()))
                {
                    //check the values
                    if ((authorReportedGenes.equals(previousAuthorReportedGenes))
                            && (snps.equals(previousSnps))    )
                    {
                        editedAssociation.setLastMappingDate(associationToEdit.getLastMappingDate());
                        editedAssociation.setLastMappingPerformedBy(associationToEdit.getLastMappingPerformedBy());
                        editedAssociation.setAssociationReport(oldAssociationReport);
                        isToRemapping = "no";
                    }
                }
            }

            if ((oldAssociationReport != null) && (isToRemapping.compareTo("yes") == 0)) {
                associationOperationsService.deleteAssocationReport(associationToEdit.getAssociationReport().getId());
            }


            // Save and validate form
            String eRelease = ensemblRestTemplateService.getRelease();
            Collection<AssociationValidationView> errors =
                    associationOperationsService.saveEditedAssociationFromForm(study,
                            editedAssociation,
                            associationId,
                            currentUserDetailsService.getUserFromRequest(
                                    request), eRelease);


            // Determine if we have any errors rather than warnings
            long errorCount = errors.stream()
                    .filter(validationError -> !validationError.getWarning())
                    .count();

            if (errorCount > 0) {

                // Get mapping details for association we're editing
                model.addAttribute("mappingDetails",
                        associationOperationsService.createMappingDetails(associationToEdit));
                model.addAttribute("errors", errors);
                model.addAttribute("criticalErrorsFound", true);

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
                redirectAttributes.addFlashAttribute("isToRemapping", isToRemapping);
                return "redirect:/associations/" + associationId;
            }
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
        model.addAttribute("errors",
                associationValidationReportService.generateAssociationWarningsListView(associationId));

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
        model.addAttribute("errors",
                associationValidationReportService.generateAssociationWarningsListView(associationId));

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
        model.addAttribute("errors",
                associationValidationReportService.generateAssociationWarningsListView(associationId));

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
        model.addAttribute("errors",
                associationValidationReportService.generateAssociationWarningsListView(associationId));

        return "edit_snp_interaction_association";
    }

    // Delete all associations linked to a study
    @RequestMapping(value = "/studies/{studyId}/associations/delete_all",
            produces = MediaType.TEXT_HTML_VALUE,
            method = RequestMethod.GET)
    public String deleteAllAssociations(@PathVariable Long studyId, HttpServletRequest request) {

        // Get all associations and delete
        Collection<Association> studyAssociations = associationRepository.findByStudyId(studyId);
        if (studyAssociations.size() > 0) {
            getLog().info("Deleting all associations for study: " + studyId);
            SecureUser user = currentUserDetailsService.getUserFromRequest(request);
            studyAssociationBatchDeletionEventService.createBatchUploadEvent(studyId, studyAssociations.size(), user);
            studyAssociations.forEach(association -> associationDeletionService.deleteAssociation(association, user));
        }
        return "redirect:/studies/" + studyId + "/associations";
    }

    // Delete checked SNP associations
    @RequestMapping(value = "/studies/{studyId}/associations/delete_checked",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> deleteChecked(@RequestParam(value = "associationIds[]") String[] associationsIds,
                                      HttpServletRequest request) {

        String message = "";
        Integer count = 0;

        // Get all associations and delete
        for (String associationId : associationsIds) {
            Association association = associationRepository.findOne(Long.valueOf(associationId));
            associationDeletionService.deleteAssociation(association,
                    currentUserDetailsService.getUserFromRequest(request));
            count++;
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
    public String approveSnpAssociation(@PathVariable Long associationId,
                                        RedirectAttributes redirectAttributes,
                                        HttpServletRequest request) {

        Association association = associationRepository.findOne(associationId);

        // Check if association has an EFO trait
        Boolean associationEfoTermsAssigned =
                checkEfoTermAssignmentService.checkAssociationEfoAssignment(association);


        //Boolean associationMappingAssigned =

        if (!associationEfoTermsAssigned) {
            String message = "Cannot approve association as no EFO trait assigned";
            redirectAttributes.addFlashAttribute("efoMessage", message);
        } else {
            Boolean associationMappingAssigned = checkMappingService.checkAssociationMappingAssignment(association);
            if (!associationMappingAssigned) {
                String message = "Cannot approve association as no Mapping assigned";
                redirectAttributes.addFlashAttribute("mappingMessage", message);
            }
            else {
                associationOperationsService.approveAssociation(association, currentUserDetailsService.getUserFromRequest(
                        request));
            }
        }

        return "redirect:/studies/" + association.getStudy().getId() + "/associations";
    }


    // Un-approve a single SNP association
    @RequestMapping(value = "associations/{associationId}/unapprove",
            produces = MediaType.TEXT_HTML_VALUE,
            method = RequestMethod.GET)
    public String unapproveSnpAssociation(@PathVariable Long associationId, HttpServletRequest request) {

        Association association = associationRepository.findOne(associationId);
        associationOperationsService.unapproveAssociation(association,
                currentUserDetailsService.getUserFromRequest(request));
        return "redirect:/studies/" + association.getStudy().getId() + "/associations";
    }

    // Approve checked SNPs
    @RequestMapping(value = "/studies/{studyId}/associations/approve_checked",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> approveChecked(@RequestParam(value = "associationIds[]") String[] associationsIds,
                                       HttpServletRequest request) {

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
            Boolean associationMappingAssigned = checkMappingService.checkAssociationsMappingAssignment(allAssociations);
            if (!associationMappingAssigned) {
                message = "Cannot approve association(s) as no Mapping assigned";
            }
            else {
                for (String associationId : associationsIds) {
                    Association association = associationRepository.findOne(Long.valueOf(associationId));
                    associationOperationsService.approveAssociation(association,
                            currentUserDetailsService.getUserFromRequest(request));
                    count++;
                }
                message = "Successfully updated " + count + " associations";
            }
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
    Map<String, String> unapproveChecked(@RequestParam(value = "associationIds[]") String[] associationsIds,
                                         HttpServletRequest request) {

        String message = "";
        Integer count = 0;

        // For each one set snpChecked attribute to true
        for (String associationId : associationsIds) {
            Association association = associationRepository.findOne(Long.valueOf(associationId));
            associationOperationsService.unapproveAssociation(association,
                    currentUserDetailsService.getUserFromRequest(request));
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
    public String approveAll(@PathVariable Long studyId,
                             RedirectAttributes redirectAttributes,
                             HttpServletRequest request) {

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
            Boolean associationMappingAssigned = checkMappingService.checkAssociationsMappingAssignment(studyAssociations);
            if (!associationMappingAssigned) {
                String message = "Cannot approve all associations as no Mapping assigned";
                redirectAttributes.addFlashAttribute("mappingMessage", message);
            }
            else {
                for (Association association : studyAssociations) {
                    associationOperationsService.approveAssociation(association,
                            currentUserDetailsService.getUserFromRequest(request));
                }
            }
        }
        return "redirect:/studies/" + studyId + "/associations";
    }

    /**
     -     * Run mapping pipeline on all SNPs in a study
     -     *
     -     * @param studyId            Study ID in database
     -     * @param redirectAttributes attributes for a redirect scenario
     -     */
      @RequestMapping(value = "/studies/{studyId}/associations/validate_unapproved",
                      produces = MediaType.TEXT_HTML_VALUE,
                      method = RequestMethod.GET)
        public Callable<String>  validateUnapproved(@PathVariable Long studyId,
                                         RedirectAttributes redirectAttributes,
                                         Model model,
                                         HttpServletRequest request,
                                         HttpSession session)
              throws IOException, ExecutionException, InterruptedException {

          Enumeration<String> sessionAttr = session.getAttributeNames();

          while(sessionAttr.hasMoreElements()){
              String attr = sessionAttr.nextElement();
              if(!attr.equals("SPRING_SECURITY_CONTEXT")){
                  session.removeAttribute(attr);
              }
          }

          // Establish our study object and upload file into study dir
          Study study = studyRepository.findOne(studyId);
          // For the study get all associations

          model.addAttribute("study", study);

          session.setAttribute("study", study);
          session.setAttribute("done", false);

          session.setAttribute("redirectAttributes", redirectAttributes);

          SecureUser user =  currentUserDetailsService.getUserFromRequest(request);

          // Return holding screen or error message
          return () -> {
              model.addAttribute("status", "201");
              model.addAttribute("uploadProgress", "true");
              model.addAttribute("processType", "validation");
              performValidation(model, session, study, user);

              return "association_upload_progress";
          };

    }

    @RequestMapping(value = "/studies/{studyId}/associations/download",
            produces = MediaType.TEXT_HTML_VALUE,
            method = RequestMethod.GET)
    public void downloadStudySnps(HttpServletResponse response, @PathVariable Long studyId)
            throws IOException {

        Collection<Association> associations = associationRepository.findByStudyId(studyId);
        Study study = studyRepository.findOne((studyId));

        if (associations.size() > 0) {
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



    // Approve all SNPs
    @RequestMapping(value = "/associations/{associationId}/force_mapping",
            produces = MediaType.TEXT_HTML_VALUE,
            method = RequestMethod.GET)
    public String forceMapping(@PathVariable Long associationId, RedirectAttributes redirectAttributes,
                               HttpServletRequest request) {
        Collection<Association> allAssociations = new ArrayList<>();

        Association association = associationRepository.findOne(associationId);
        allAssociations.add(association);
        try {
            SecureUser user = currentUserDetailsService.getUserFromRequest(request);
            mapCatalogService.mapCatalogContentsByAssociations(user.getEmail(), allAssociations);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("isToRemapping", "failed");
        }

        return "redirect:/associations/" + associationId;
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

    @Async
    private void performUpload(Model model, HttpSession session, MultipartFile file, SecureUser user, Study study)
            throws ExecutionException, InterruptedException, SheetProcessingException, FileUploadException, IOException {

        getLog().debug("Upload request received");

        String fileName = file.getOriginalFilename();

        Future<Boolean> future = executorService.submit(() -> {
            List<AssociationUploadErrorView> fileErrors = null;
            List<AssociationUploadErrorView> xlsErrors = null;

            try {
                fileErrors = associationUploadService.upload(file, study, user);
            }
            catch (EnsemblMappingException e) {
                session.setAttribute("ensemblMappingFailure", true);
            }

            if (fileErrors != null && !fileErrors.isEmpty()) {
                // Split
                getLog().error("Errors found in file: " + fileName);

                // Split the general collection of errors in two different structures. For view purpose.
                xlsErrors = AssociationUploadService.splitByXLSError(fileErrors);

                session.setAttribute("fileName", fileName);
                session.setAttribute("fileErrors", fileErrors);
                session.setAttribute("xlsErrors", xlsErrors);
            }
            session.setAttribute("done", true);
            return true;
        });

        session.setAttribute("future", future);
    }

    @Async
    private void performValidation(Model model, HttpSession session, Study study, SecureUser user){


        Future<Boolean> future =
            executorService.submit(() -> {
                Collection<Association> studyAssociations = associationService.findAllByStudyId(study.getId());

                for (Association associationToValidate : studyAssociations) {
                    if (!associationToValidate.getSnpApproved()) {
                        String measurementType =
                                associationOperationsService.determineIfAssociationIsOrType(associationToValidate);
                        List<AssociationValidationView> criticalErrors = new ArrayList<>();
                        if (associationToValidate.getSnpInteraction()) {
                            criticalErrors =
                                    associationOperationsService.checkSnpAssociationInteractionFormErrors((SnpAssociationInteractionForm) associationOperationsService
                                                                                                                  .generateForm(associationToValidate),
                                                                                                          measurementType);
                        }
                        else {
                            criticalErrors =
                                    associationOperationsService.checkSnpAssociationFormErrors((SnpAssociationStandardMultiForm) associationOperationsService
                                                                                                       .generateForm(associationToValidate),
                                                                                               measurementType);
                        }

                        //if an association has critical errors, go straight to that association
                        if (!criticalErrors.isEmpty()) {
                            session.setAttribute("measurementType", measurementType);

                            // Get mapping details
                            session.setAttribute("mappingDetails",
                                               associationOperationsService.createMappingDetails(associationToValidate));

                            // Return any association errors
                            session.setAttribute("errors", criticalErrors);
                            session.setAttribute("criticalErrorsFound", true);
                            session.setAttribute("associationId", associationToValidate.getId());

                            break;

                        }

    //     if there are no criticial errors, save the validation and go to the next association
                        else {
                            // Save and validate form
                            String eRelease = ensemblRestTemplateService.getRelease();

                            Collection<AssociationValidationView> errors =
                                    associationOperationsService.validateAndSaveAssociation(study,
                                                                                            associationToValidate,
                                                                                            user,
                                                                                            eRelease);

                            // Determine if we have any errors rather than warnings
                            long errorCount = errors.stream()
                                    .filter(validationError -> !validationError.getWarning())
                                    .count();
                            //if there are errors rather than warnings, go straight to the page to edit
                            if (errorCount > 0) {

                                session.setAttribute("study", study);
                                session.setAttribute("measurementType", measurementType);
                                // Get mapping details for association we're editing
                                session.setAttribute("mappingDetails",
                                                   associationOperationsService.createMappingDetails(associationToValidate));
                                session.setAttribute("errors", errors);
                                session.setAttribute("criticalErrorsFound", true);
                                session.setAttribute("associationId", associationToValidate.getId());

                                break;
                            }
                        }
                }
            }
           session.setAttribute("done", true);
           return true;
        });

        session.setAttribute("future", future);
    }

    @PreDestroy
    public void destroy() {
        // and cleanup
        getLog().debug("Shutting down executor service...");
        executorService.shutdown();
        try {
            if (executorService.awaitTermination(2, TimeUnit.MINUTES)) {
                getLog().debug("Executor service shutdown gracefully.");
            }
            else {
                int abortedTasks = executorService.shutdownNow().size();
                getLog().warn("Executor service forcibly shutdown. " + abortedTasks + " tasks were aborted");
            }
        }
        catch (InterruptedException e) {
            getLog().error("Executor service failed to shutdown cleanly", e);
            throw new RuntimeException("Unable to cleanly shutdown ZOOMA.", e);
        }
    }


    @RequestMapping(value = "/studies/{studyId}/associations/status", method = RequestMethod.GET)
    public @ResponseBody boolean checkUploadStatus(HttpSession session) {
        boolean done;
        if (session.getAttribute("done") != null) {
            done = (boolean) session.getAttribute("done");
        }
        else {
            done = false;
        }

        try {
            Future<Boolean> f = (Future<Boolean>) session.getAttribute("future");
             if(f.isDone()) {
                f.get();
            }
        }
        catch (Exception e){
            session.setAttribute("done", true);
            session.setAttribute("exception", e);
        }
        getLog().debug("Process done? = " + done);
        return done;
    }

    @RequestMapping(value = "/studies/{studyId}/associations/getUploadResults",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String getUploadResult(@PathVariable Long studyId, HttpSession session, Model model)
            throws ExecutionException, InterruptedException, SheetProcessingException, FileUploadException, IOException {


        Study study = studyRepository.findOne(studyId);
        model.addAttribute("study", study);
        Future<Boolean> f = (Future<Boolean>) session.getAttribute("future");
        f.get();

        Exception exception = (Exception) session.getAttribute("exception");
        if (exception == null) {
            model.addAttribute("status", "OK");
        }
        else {
            model.addAttribute("status", exception.getMessage());
        }



        if (session.getAttribute("ensemblMappingFailure") != null) {
            return "ensembl_mapping_failure";
        }

        List<AssociationUploadErrorView> fileErrors =
                (List<AssociationUploadErrorView>) session.getAttribute("fileErrors");

        List<AssociationUploadErrorView> xlsErrors =
                 (List<AssociationUploadErrorView>) session.getAttribute("xlsErrors");

        if ((session.getAttribute("fileErrors") != null && !fileErrors.isEmpty()) ||
                ((session.getAttribute("xlsErrors") != null && !xlsErrors.isEmpty())) )
        {
            getLog().debug("Shutting down executor service...");

            model.addAttribute("fileName", session.getAttribute("fileName"));
            model.addAttribute("fileErrors", fileErrors);
            model.addAttribute("xlsErrors", xlsErrors);

            return "error_pages/association_file_upload_error";

        }
        else {
            return "redirect:/studies/" + studyId + "/associations";
        }
    }


    @RequestMapping(value = "/studies/{studyId}/associations/getValidationResults",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.GET)
    public String getValidationResult(@PathVariable Long studyId, HttpSession session, Model model, RedirectAttributes redirectAttributes)
            throws ExecutionException, InterruptedException, SheetProcessingException, FileUploadException, IOException {


        Study study = studyRepository.findOne(studyId);
        model.addAttribute("study", study);
        Future<Boolean> f = (Future<Boolean>) session.getAttribute("future");
        f.get();

        Exception exception = (Exception) session.getAttribute("exception");
        if (exception == null) {
            model.addAttribute("status", "OK");
        }
        else {
            model.addAttribute("status", exception.getMessage());
        }

        //if an association has critical errors, go straight to that association
        if (session.getAttribute("criticalErrorsFound") != null) {
            Association association = associationRepository.getOne((Long) session.getAttribute("associationId"));
            model.addAttribute("study", study);
            model.addAttribute("measurementType", session.getAttribute("measurementType"));

            // Get mapping details
            model.addAttribute("mappingDetails",
                               associationOperationsService.createMappingDetails(association));

            // Return any association errors
            model.addAttribute("errors", session.getAttribute("errors"));
            model.addAttribute("criticalErrorsFound", true);

            if (association.getSnpInteraction()) {
                model.addAttribute("form", associationOperationsService
                        .generateForm(association));
                return "redirect:/associations/" + association.getId();
            }
            else {
                model.addAttribute("form", associationOperationsService
                        .generateForm(association));

                // Determine view
                if (association.getMultiSnpHaplotype()) {
                    return "redirect:/associations/" + association.getId();
                }
                else {
                    return "redirect:/associations/" + association.getId();

                }
            }
        }
       else {
            String message = "Mapping complete, please check for any errors displayed in the 'Errors' column";
            redirectAttributes.addFlashAttribute("mappingComplete", message);
            return "redirect:/studies/" + studyId + "/associations";
        }
    }


}