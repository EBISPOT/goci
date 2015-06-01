package uk.ac.ebi.spot.goci.curation.controller;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationTableView;
import uk.ac.ebi.spot.goci.curation.model.SnpFormColumn;
import uk.ac.ebi.spot.goci.curation.model.SnpFormRow;
import uk.ac.ebi.spot.goci.curation.service.AssociationBatchLoaderService;
import uk.ac.ebi.spot.goci.curation.service.AssociationDownloadService;
import uk.ac.ebi.spot.goci.curation.service.AssociationViewService;
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
    private AssociationViewService associationViewService;
    private SingleSnpMultiSnpAssociationService singleSnpMultiSnpAssociationService;
    private SnpInteractionAssociationService snpInteractionAssociationService;
    private LociAttributesService lociAttributesService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public AssociationController(AssociationRepository associationRepository,
                                 StudyRepository studyRepository,
                                 EfoTraitRepository efoTraitRepository,
                                 LocusRepository locusRepository,
                                 AssociationBatchLoaderService associationBatchLoaderService,
                                 AssociationDownloadService associationDownloadService,
                                 AssociationViewService associationViewService,
                                 SingleSnpMultiSnpAssociationService singleSnpMultiSnpAssociationService,
                                 SnpInteractionAssociationService snpInteractionAssociationService,
                                 LociAttributesService lociAttributesService) {
        this.associationRepository = associationRepository;
        this.studyRepository = studyRepository;
        this.efoTraitRepository = efoTraitRepository;
        this.locusRepository = locusRepository;
        this.associationBatchLoaderService = associationBatchLoaderService;
        this.associationDownloadService = associationDownloadService;
        this.associationViewService = associationViewService;
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

        // For our associations create a table view object and return
        Collection<SnpAssociationTableView> snpAssociationTableViews = new ArrayList<SnpAssociationTableView>();
        for (Association association : associations) {
            SnpAssociationTableView snpAssociationTableView = associationViewService.createSnpAssociationTableView(association);
            snpAssociationTableViews.add(snpAssociationTableView);
        }
        model.addAttribute("snpAssociationTableViews", snpAssociationTableViews);

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
        switch (direction) {
            case "asc":
                associations.addAll(associationRepository.findByStudyId(studyId, sortByPvalueExponentAndMantissaAsc()));
                break;
            case "desc":
                associations.addAll(associationRepository.findByStudyId(studyId,
                                                                        sortByPvalueExponentAndMantissaDesc()));
                break;
            default:
                associations.addAll(associationRepository.findByStudyId(studyId));
                break;
        }

        // For our associations create a table view object and return
        Collection<SnpAssociationTableView> snpAssociationTableViews = new ArrayList<SnpAssociationTableView>();
        for (Association association : associations) {
            SnpAssociationTableView snpAssociationTableView = associationViewService.createSnpAssociationTableView(
                    association);
            snpAssociationTableViews.add(snpAssociationTableView);
        }
        model.addAttribute("snpAssociationTableViews", snpAssociationTableViews);

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

        // Sorting will not work for multi-snp haplotype or snp interactions so need to check for that
        Boolean sortValues = true;

        switch (direction) {
            case "asc":
                associations.addAll(associationRepository.findByStudyId(studyId, sortByRsidAsc()));
                break;
            case "desc":
                associations.addAll(associationRepository.findByStudyId(studyId, sortByRsidDesc()));
                break;
            default:
                associations.addAll(associationRepository.findByStudyId(studyId));
                break;
        }


        // For our associations create a table view object and return
        Collection<SnpAssociationTableView> snpAssociationTableViews = new ArrayList<SnpAssociationTableView>();
        for (Association association : associations) {
            SnpAssociationTableView snpAssociationTableView = associationViewService.createSnpAssociationTableView(
                    association);

           // Cannot sort multi field values
            if (snpAssociationTableView.getMultiSnpHaplotype() != null) {
                if (snpAssociationTableView.getMultiSnpHaplotype().equalsIgnoreCase("Yes")) {
                    sortValues = false;
                }
            }

            if (snpAssociationTableView.getSnpInteraction() != null) {
                if (snpAssociationTableView.getSnpInteraction().equalsIgnoreCase("Yes")) {
                    sortValues = false;
                }
            }

            snpAssociationTableViews.add(snpAssociationTableView);
        }

        // Only return sorted results if its not a multi-snp haplotype or snp interaction
        if (sortValues) {

            model.addAttribute("snpAssociationTableViews", snpAssociationTableViews);

            // Also passes back study object to view so we can create links back to main study page
            model.addAttribute("study", studyRepository.findOne(studyId));
            return "study_association";

        }

        else {
            return "redirect:/studies/" + studyId + "/associations";
        }

    }


    // Upload a spreadsheet of snp association information
    @RequestMapping(value = "/studies/{studyId}/associations/upload",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String uploadStudySnps(@RequestParam("file") MultipartFile file, @PathVariable Long studyId, Model model){

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
            catch (InvalidOperationException e) {
                e.printStackTrace();
                model.addAttribute("study", studyRepository.findOne(studyId));
                return "wrong_file_format_warning";
            }
            catch (InvalidFormatException e) {
                e.printStackTrace();
                model.addAttribute("study", studyRepository.findOne(studyId));
                return "wrong_file_format_warning";
            }
            catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("study", studyRepository.findOne(studyId));
                return "wrong_file_format_warning";
            }
            catch (RuntimeException e){
                e.printStackTrace();
                model.addAttribute("study", studyRepository.findOne(studyId));
                return "data_upload_problem";

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

        // Add one row by default and set description
        emptyForm.getSnpFormRows().add(new SnpFormRow());
        emptyForm.setMultiSnpHaplotypeDescr("Single variant");

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
        emptyForm.setMultiSnpHaplotype(true);

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
    public String addRows(SnpAssociationInteractionForm snpAssociationInteractionForm,
                          Model model,
                          @PathVariable Long studyId) {
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

    // Add single column to table
    @RequestMapping(value = "/studies/{studyId}/associations/add_interaction", params = {"addCol"})
    public String addCol(SnpAssociationInteractionForm snpAssociationInteractionForm,
                         Model model,
                         @PathVariable Long studyId) {
        snpAssociationInteractionForm.getSnpFormColumns().add(new SnpFormColumn());

        // Pass back updated form
        model.addAttribute("snpAssociationInteractionForm", snpAssociationInteractionForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_snp_interaction_association";
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


    // Remove column from table
    @RequestMapping(value = "/studies/{studyId}/associations/add_interaction", params = {"removeCol"})
    public String removeCol(SnpAssociationInteractionForm snpAssociationInteractionForm,
                            HttpServletRequest req,
                            Model model,
                            @PathVariable Long studyId) {

        //Index of value to remove
        final Integer colId = Integer.valueOf(req.getParameter("removeCol"));

        // Remove col
        snpAssociationInteractionForm.getSnpFormColumns().remove(colId.intValue());

        // Pass back updated form
        model.addAttribute("snpAssociationInteractionForm", snpAssociationInteractionForm);

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "add_snp_interaction_association";
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

        return "redirect:/associations/" + newAssociation.getId();
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

        return "redirect:/associations/" + newAssociation.getId();
    }

    @RequestMapping(value = "/studies/{studyId}/associations/add_interaction",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String addSnpInteraction(@ModelAttribute SnpAssociationInteractionForm snpAssociationInteractionForm,
                                    @PathVariable Long studyId) {

        // Get our study object
        Study study = studyRepository.findOne(studyId);

        // Create an association object from details in returned form
        Association newAssociation = snpInteractionAssociationService.createAssociation(snpAssociationInteractionForm);

        // Set the study ID for our association
        newAssociation.setStudy(study);

        // Save our association information
        associationRepository.save(newAssociation);

        return "redirect:/associations/" + newAssociation.getId();
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

        // Also passes back study object to view so we can create links back to main study page
        model.addAttribute("study", studyRepository.findOne(studyId));

        if (associationToView.getSnpInteraction() != null && associationToView.getSnpInteraction()) {
            SnpAssociationInteractionForm snpAssociationInteractionForm =
                    snpInteractionAssociationService.createSnpAssociationInteractionForm(associationToView);
            model.addAttribute("snpAssociationInteractionForm", snpAssociationInteractionForm);
            return "edit_snp_interaction_association";
        }

        else if (associationToView.getMultiSnpHaplotype() != null && associationToView.getMultiSnpHaplotype()) {
            // Create form and return to user
            SnpAssociationForm snpAssociationForm = singleSnpMultiSnpAssociationService.createSnpAssociationForm(
                    associationToView);
            model.addAttribute("snpAssociationForm", snpAssociationForm);
            return "edit_multi_snp_association";
        }

        // If attributes haven't been set determine based on locus count and risk allele count
        else {
            Integer locusCount = associationToView.getLoci().size();

            List<RiskAllele> riskAlleles = new ArrayList<>();
            for (Locus locus : associationToView.getLoci()) {
                for (RiskAllele riskAllele : locus.getStrongestRiskAlleles()) {
                    riskAlleles.add(riskAllele);
                }
            }

            // Case where we have SNP interaction
            if (locusCount > 1) {
                SnpAssociationInteractionForm snpAssociationInteractionForm =
                        snpInteractionAssociationService.createSnpAssociationInteractionForm(associationToView);
                model.addAttribute("snpAssociationInteractionForm", snpAssociationInteractionForm);
                return "edit_snp_interaction_association";
            }
            else {

                // Create form and return to user
                SnpAssociationForm snpAssociationForm = singleSnpMultiSnpAssociationService.createSnpAssociationForm(
                        associationToView);
                model.addAttribute("snpAssociationForm", snpAssociationForm);


                // If editing multi-snp haplotype
                if (riskAlleles.size() > 1) {
                    return "edit_multi_snp_association";
                }
                else {
                    return "edit_standard_snp_association";
                }
            }
        }
    }


    //Edit existing association
    @RequestMapping(value = "/associations/{associationId}",
                    produces = MediaType.TEXT_HTML_VALUE,
                    method = RequestMethod.POST)
    public String editAssociation(@ModelAttribute SnpAssociationForm snpAssociationForm, @ModelAttribute SnpAssociationInteractionForm snpAssociationInteractionForm,
                                  @PathVariable Long associationId, @RequestParam(value = "associationtype", required = true) String associationType) {

        //Create association
        Association editedAssociation = null;

        // Request parameter determines how to process form and also which form to process
        if (associationType.equalsIgnoreCase("interaction")){
            editedAssociation = snpInteractionAssociationService.createAssociation(snpAssociationInteractionForm);
        }

        else if (associationType.equalsIgnoreCase("standardormulti")) {
            editedAssociation = singleSnpMultiSnpAssociationService.createAssociation(snpAssociationForm);
        }

        // default to standard view
        else {
            editedAssociation = singleSnpMultiSnpAssociationService.createAssociation(snpAssociationForm);
        }

        // Set ID of new  association to the ID of the association we're currently editing
        editedAssociation.setId(associationId);

        // Set study to one currently linked to association
        Association currentAssociation = associationRepository.findOne(associationId);
        Study associationStudy = currentAssociation.getStudy();
        editedAssociation.setStudy(associationStudy);

        // Save our association information
        associationRepository.save(editedAssociation);

        return "redirect:/associations/" + associationId;
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

    // Add single column to table
    @RequestMapping(value = "/associations/{associationId}", params = {"addCol"})
    public String addColEditMode(SnpAssociationInteractionForm snpAssociationInteractionForm,
                                 Model model, @PathVariable Long associationId) {

        snpAssociationInteractionForm.getSnpFormColumns().add(new SnpFormColumn());

        // Pass back updated form
        model.addAttribute("snpAssociationInteractionForm", snpAssociationInteractionForm);

        // Also passes back study object to view so we can create links back to main study page
        Association currentAssociation = associationRepository.findOne(associationId);
        Study associationStudy = currentAssociation.getStudy();
        Long studyId = associationStudy.getId();
        model.addAttribute("study", studyRepository.findOne(studyId));

        return "edit_snp_interaction_association";
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
        model.addAttribute("snpAssociationInteractionForm", snpAssociationInteractionForm);

        // Also passes back study object to view so we can create links back to main study page
        Association currentAssociation = associationRepository.findOne(associationId);
        Study associationStudy = currentAssociation.getStudy();
        Long studyId = associationStudy.getId();
        model.addAttribute("study", studyRepository.findOne(studyId));

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
                associationRepository.save(association);
            }

            return "redirect:/studies/" + studyId + "/associations";
        }
    }

    /* Exception handling */
    @ExceptionHandler(DataIntegrityException.class)
    public String handleDataIntegrityException(DataIntegrityException dataIntegrityException, Model model) {
        return dataIntegrityException.getMessage();
    }

//    @ExceptionHandler(InvalidFormatException.class)
//    public String handleInvalidFormatException(InvalidFormatException invalidFormatException, Model model, Study study){
//        getLog().error("Invalid format exception", invalidFormatException);
//        model.addAttribute("study", study);
//        return "wrong_file_format_warning";
//
//    }
//
//    @ExceptionHandler(InvalidOperationException.class)
//    public String handleInvalidOperationException(InvalidOperationException invalidOperationException){
//        getLog().error("Invalid operation exception", invalidOperationException);
////        model.addAttribute("study", study);
//        System.out.println("Caught the exception but couldn't quite handle it");
//        return "wrong_file_format_warning";
//
//    }

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

