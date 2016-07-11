package uk.ac.ebi.spot.goci.curation.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.ebi.spot.goci.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.builder.SecureUserBuilder;
import uk.ac.ebi.spot.goci.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.curation.model.AssociationUploadErrorView;
import uk.ac.ebi.spot.goci.curation.model.AssociationValidationView;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationStandardMultiForm;
import uk.ac.ebi.spot.goci.curation.service.AssociationDeletionService;
import uk.ac.ebi.spot.goci.curation.service.AssociationDownloadService;
import uk.ac.ebi.spot.goci.curation.service.AssociationOperationsService;
import uk.ac.ebi.spot.goci.curation.service.AssociationUploadService;
import uk.ac.ebi.spot.goci.curation.service.AssociationValidationReportService;
import uk.ac.ebi.spot.goci.curation.service.AssociationViewService;
import uk.ac.ebi.spot.goci.curation.service.CheckEfoTermAssignmentService;
import uk.ac.ebi.spot.goci.curation.service.CurrentUserDetailsService;
import uk.ac.ebi.spot.goci.curation.service.SingleSnpMultiSnpAssociationService;
import uk.ac.ebi.spot.goci.curation.service.SnpInteractionAssociationService;
import uk.ac.ebi.spot.goci.exception.EnsemblMappingException;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.MappingService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Created by emma on 11/07/2016.
 *
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class AssociationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AssociationRepository associationRepository;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private EfoTraitRepository efoTraitRepository;

    @Mock
    private AssociationDownloadService associationDownloadService;

    @Mock
    private AssociationViewService associationViewService;

    @Mock
    private SingleSnpMultiSnpAssociationService singleSnpMultiSnpAssociationService;

    @Mock
    private SnpInteractionAssociationService snpInteractionAssociationService;

    @Mock
    private CheckEfoTermAssignmentService checkEfoTermAssignmentService;

    @Mock
    private AssociationOperationsService associationOperationsService;

    @Mock
    private MappingService mappingService;

    @Mock
    private AssociationUploadService associationUploadService;

    @Mock
    private CurrentUserDetailsService currentUserDetailsService;

    @Mock
    private AssociationValidationReportService associationValidationReportService;

    @Mock
    private AssociationDeletionService associationDeletionService;

    private static final SecureUser SECURE_USER =
            new SecureUserBuilder().setId(564L).setEmail("test@test.com").setPasswordHash("738274$$").build();

    private static final Study STUDY = new StudyBuilder().setId(1234L).build();

    private static final Association ASSOCIATION =
            new AssociationBuilder().setId(100L)
                    .setOrPerCopyNum((float) 1.22)
                    .setRange("[0.82-0.92]")
                    .setStandardError(
                            (float) 0.6)
                    .setPvalueMantissa(2)
                    .setPvalueExponent(-7)
                    .build();

    @Before
    public void setUp() throws Exception {
        AssociationController associationController = new AssociationController(associationRepository,
                                                                                studyRepository,
                                                                                efoTraitRepository,
                                                                                associationDownloadService,
                                                                                associationViewService,
                                                                                singleSnpMultiSnpAssociationService,
                                                                                snpInteractionAssociationService,
                                                                                checkEfoTermAssignmentService,
                                                                                associationOperationsService,
                                                                                mappingService,
                                                                                associationUploadService,
                                                                                currentUserDetailsService,
                                                                                associationValidationReportService,
                                                                                associationDeletionService);
        mockMvc = MockMvcBuilders.standaloneSetup(associationController).build();
    }

    @Test
    public void viewStudySnps() throws Exception {
        mockMvc.perform(get("/studies/1234/associations").accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk()).andExpect(view().name("study_association"));
    }

    @Test
    public void uploadStudySnpsFileWithError() throws Exception {

        // Create objects required for testing
        MockMultipartFile file =
                new MockMultipartFile("file", "filename.txt", "text/plain", "TEST".getBytes());
        AssociationUploadErrorView associationUploadErrorView1 =
                new AssociationUploadErrorView(1, "OR", "Value is not empty", false);
        List<AssociationUploadErrorView> uploadErrorViews = Collections.singletonList(associationUploadErrorView1);

        // Stubbing
        when(studyRepository.findOne(Matchers.anyLong())).thenReturn(STUDY);
        when(currentUserDetailsService.getUserFromRequest(Matchers.any(HttpServletRequest.class))).thenReturn(
                SECURE_USER);
        when(associationUploadService.upload(file, STUDY, SECURE_USER)).thenReturn(uploadErrorViews);

        mockMvc.perform(fileUpload("/studies/1234/associations/upload").file(file).param("studyId", "1234"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("fileName", file.getOriginalFilename()))
                .andExpect(model().attribute("fileErrors", instanceOf(List.class)))
                .andExpect(model().attribute("fileErrors", hasSize(1)))
                .andExpect(model().attributeExists("study"))
                .andExpect(view().name("error_pages/association_file_upload_error"));
        verify(studyRepository, times(1)).findOne(Matchers.anyLong());
    }

    @Test
    public void uploadStudySnpsFileWithNoError() throws Exception {

        // Create objects required for testing
        MockMultipartFile file =
                new MockMultipartFile("file", "filename.txt", "text/plain", "TEST".getBytes());

        // Stubbing
        when(studyRepository.findOne(Matchers.anyLong())).thenReturn(STUDY);
        when(currentUserDetailsService.getUserFromRequest(Matchers.any(HttpServletRequest.class))).thenReturn(
                SECURE_USER);
        when(associationUploadService.upload(file, STUDY, SECURE_USER)).thenReturn(Collections.EMPTY_LIST);

        mockMvc.perform(fileUpload("/studies/1234/associations/upload").file(file).param("studyId", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeExists("study"))
                .andExpect(view().name("redirect:/studies/1234/associations"));
        verify(studyRepository, times(1)).findOne(Matchers.anyLong());
    }

    @Test
    public void uploadStudySnpsFileWithMappingError() throws Exception {

        // Create objects required for testing
        MockMultipartFile file =
                new MockMultipartFile("file", "filename.txt", "text/plain", "TEST".getBytes());

        // Stubbing
        when(studyRepository.findOne(Matchers.anyLong())).thenReturn(STUDY);
        when(currentUserDetailsService.getUserFromRequest(Matchers.any(HttpServletRequest.class))).thenReturn(
                SECURE_USER);
        when(associationUploadService.upload(file, STUDY, SECURE_USER)).thenThrow(EnsemblMappingException.class);

        mockMvc.perform(fileUpload("/studies/1234/associations/upload").file(file).param("studyId", "1234"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("study"))
                .andExpect(view().name("ensembl_mapping_failure"));
        verify(studyRepository, times(1)).findOne(Matchers.anyLong());
    }

    @Test
    public void addStandardSnpsWithRowErrors() throws Exception {

        AssociationValidationView associationValidationView =
                new AssociationValidationView("SNP", "Value is not empty", false);
        List<AssociationValidationView> errors = Collections.singletonList(associationValidationView);

        // Stubbing
        when(studyRepository.findOne(Matchers.anyLong())).thenReturn(STUDY);
        when(currentUserDetailsService.getUserFromRequest(Matchers.any(HttpServletRequest.class))).thenReturn(
                SECURE_USER);
        when(associationOperationsService.checkSnpAssociationFormErrors(Matchers.any(SnpAssociationStandardMultiForm.class)))
                .thenReturn(errors);

        mockMvc.perform(post("/studies/1234/associations/add_standard").param("measurementType","or"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", instanceOf(SnpAssociationStandardMultiForm.class)))
                .andExpect(model().attribute("errors", instanceOf(List.class)))
                .andExpect(model().attribute("errors", hasSize(1)))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("measurementType"))
                .andExpect(view().name("add_standard_snp_association"));

        //verify properties of bound object
        ArgumentCaptor<SnpAssociationStandardMultiForm> formArgumentCaptor =
                ArgumentCaptor.forClass(SnpAssociationStandardMultiForm.class);
        verify(associationOperationsService).checkSnpAssociationFormErrors(formArgumentCaptor.capture());
        verify(studyRepository, times(1)).findOne(Matchers.anyLong());
        verifyZeroInteractions(singleSnpMultiSnpAssociationService);
    }

    @Test
    public void addStandardSnpsWithErrors() throws Exception {

        AssociationValidationView associationValidationView =
                new AssociationValidationView("OR", "Value is empty", false);
        List<AssociationValidationView> errors = Collections.singletonList(associationValidationView);

        // Stubbing
        when(studyRepository.findOne(Matchers.anyLong())).thenReturn(STUDY);
        when(currentUserDetailsService.getUserFromRequest(Matchers.any(HttpServletRequest.class))).thenReturn(
                SECURE_USER);
        when(associationOperationsService.checkSnpAssociationFormErrors(Matchers.any(SnpAssociationStandardMultiForm.class)))
                .thenReturn(Collections.EMPTY_LIST);
        when(singleSnpMultiSnpAssociationService.createAssociation(Matchers.any(SnpAssociationStandardMultiForm.class))).thenReturn(ASSOCIATION);
        when(associationOperationsService.saveAssociationCreatedFromForm(STUDY,ASSOCIATION,SECURE_USER)).thenReturn(errors);

        mockMvc.perform(post("/studies/1234/associations/add_standard").param("measurementType","or"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", instanceOf(SnpAssociationStandardMultiForm.class)))
                .andExpect(model().attribute("errors", instanceOf(List.class)))
                .andExpect(model().attribute("errors", hasSize(1)))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("measurementType"))
                .andExpect(view().name("add_standard_snp_association"));

        //verify properties of bound object
        ArgumentCaptor<SnpAssociationStandardMultiForm> formArgumentCaptor =
                ArgumentCaptor.forClass(SnpAssociationStandardMultiForm.class);
        verify(associationOperationsService).checkSnpAssociationFormErrors(formArgumentCaptor.capture());
        verify(singleSnpMultiSnpAssociationService).createAssociation(formArgumentCaptor.capture());
        verify(studyRepository, times(1)).findOne(Matchers.anyLong());
    }


    @Test
    public void addStandardSnpsWithNoErrors() throws Exception {

        AssociationValidationView associationValidationView =
                new AssociationValidationView("SNP", "SNP identifier rs34tt is not valid", true);
        List<AssociationValidationView> errors = Collections.singletonList(associationValidationView);

        // Stubbing
        when(studyRepository.findOne(Matchers.anyLong())).thenReturn(STUDY);
        when(currentUserDetailsService.getUserFromRequest(Matchers.any(HttpServletRequest.class))).thenReturn(
                SECURE_USER);
        when(associationOperationsService.checkSnpAssociationFormErrors(Matchers.any(SnpAssociationStandardMultiForm.class)))
                .thenReturn(Collections.EMPTY_LIST);
        when(singleSnpMultiSnpAssociationService.createAssociation(Matchers.any(SnpAssociationStandardMultiForm.class))).thenReturn(ASSOCIATION);
        when(associationOperationsService.saveAssociationCreatedFromForm(STUDY,ASSOCIATION,SECURE_USER)).thenReturn(errors);

        mockMvc.perform(post("/studies/1234/associations/add_standard").param("measurementType","or"))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("measurementType"))
                .andExpect(view().name("redirect:/associations/100"));

        //verify properties of bound object
        ArgumentCaptor<SnpAssociationStandardMultiForm> formArgumentCaptor =
                ArgumentCaptor.forClass(SnpAssociationStandardMultiForm.class);
        verify(associationOperationsService).checkSnpAssociationFormErrors(formArgumentCaptor.capture());
        verify(singleSnpMultiSnpAssociationService).createAssociation(formArgumentCaptor.capture());
        verify(studyRepository, times(1)).findOne(Matchers.anyLong());
    }

    @Test
    public void addMultiSnps() throws Exception {

    }

    @Test
    public void addSnpInteraction() throws Exception {

    }


    @Test
    public void viewAssociation() throws Exception {

    }

    @Test
    public void editAssociation() throws Exception {

    }

    @Test
    public void addRowEditMode() throws Exception {

    }

    @Test
    public void addColEditMode() throws Exception {

    }

    @Test
    public void removeRowEditMode() throws Exception {

    }

    @Test
    public void removeColEditMode() throws Exception {

    }

    @Test
    public void deleteAllAssociations() throws Exception {

    }

    @Test
    public void deleteChecked() throws Exception {

    }

    @Test
    public void approveSnpAssociation() throws Exception {

    }

    @Test
    public void unapproveSnpAssociation() throws Exception {

    }

    @Test
    public void approveChecked() throws Exception {

    }

    @Test
    public void unapproveChecked() throws Exception {

    }

    @Test
    public void approveAll() throws Exception {

    }

    @Test
    public void validateAll() throws Exception {

    }

    @Test
    public void downloadStudySnps() throws Exception {

    }

    @Test
    public void applyStudyEFOtraitToSnps() throws Exception {

    }

    @Test
    public void handleDataIntegrityException() throws Exception {

    }

    @Test
    public void handleInvalidFormatExceptionAndInvalidOperationException() throws Exception {

    }

    @Test
    public void handleIOException() throws Exception {

    }

    @Test
    public void handleFileUploadException() throws Exception {

    }

    @Test
    public void handleFileNotFound() throws Exception {

    }

    @Test
    public void populateEfoTraits() throws Exception {

    }

}