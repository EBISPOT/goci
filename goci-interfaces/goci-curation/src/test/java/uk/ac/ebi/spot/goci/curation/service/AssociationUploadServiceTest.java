package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import uk.ac.ebi.spot.goci.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.builder.AssociationSummaryBuilder;
import uk.ac.ebi.spot.goci.builder.AssociationUploadRowBuilder;
import uk.ac.ebi.spot.goci.builder.CuratorBuilder;
import uk.ac.ebi.spot.goci.builder.HousekeepingBuilder;
import uk.ac.ebi.spot.goci.builder.RowValidationSummaryBuilder;
import uk.ac.ebi.spot.goci.builder.SecureUserBuilder;
import uk.ac.ebi.spot.goci.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.builder.ValidationErrorBuilder;
import uk.ac.ebi.spot.goci.builder.ValidationSummaryBuilder;
import uk.ac.ebi.spot.goci.curation.exception.FileUploadException;
import uk.ac.ebi.spot.goci.exception.SheetProcessingException;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationSummary;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.RowValidationSummary;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.ValidationError;
import uk.ac.ebi.spot.goci.model.ValidationSummary;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.AssociationFileUploadService;
import uk.ac.ebi.spot.goci.service.StudyTrackingOperationServiceImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 16/06/2016.
 *
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class AssociationUploadServiceTest {

    @Mock
    private StudyFileService studyFileService;

    @Mock
    private AssociationFileUploadService associationFileUploadService;

    @Mock
    private AssociationOperationsService associationOperationsService;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private StudyTrackingOperationServiceImpl trackingOperationService;


    private AssociationUploadService associationUploadService;

    private static final SecureUser SECURE_USER =
            new SecureUserBuilder().setId(564L).setEmail("test@test.com").setPasswordHash("738274$$").build();

    private static final Curator LEVEL_1_CURATOR = new CuratorBuilder().setId(803L)
            .setLastName("Level 1 Curator")
            .build();

    private static final Housekeeping HOUSEKEEPING =
            new HousekeepingBuilder()
                    .setId(799L)
                    .setCurator(LEVEL_1_CURATOR)
                    .build();

    private static final Study STUDY = new StudyBuilder().setId(100L).setHousekeeping(HOUSEKEEPING).build();

    private static final Association ASSOCIATION = new AssociationBuilder().setId(100L).build();

    private static final AssociationUploadRow ROW_1 = new AssociationUploadRowBuilder().setRowNumber(1).build();

    private static final ValidationError ERROR = new ValidationErrorBuilder().setError("ERROR").setField("OR").build();

    private static final RowValidationSummary ROW_VALIDATION_NO_ERROR =
            new RowValidationSummaryBuilder().setRow(ROW_1).setErrors(Collections.EMPTY_LIST).build();

    private static final AssociationSummary
            ASSOCIATION_SUMMARY_NO_ERROR =
            new AssociationSummaryBuilder().setRowNumber(1)
                    .setAssociation(ASSOCIATION)
                    .setErrors(Collections.EMPTY_LIST)
                    .build();

    private static final AssociationSummary
            ASSOCIATION_SUMMARY_ERROR =
            new AssociationSummaryBuilder().setRowNumber(1)
                    .setAssociation(ASSOCIATION)
                    .setErrors(Collections.singletonList(ERROR))
                    .build();

    private static final RowValidationSummary ROW_VALIDATION_SUMMARY_ERROR =
            new RowValidationSummaryBuilder().setRow(ROW_1).setErrors(Collections.singletonList(ERROR)).build();

    private static final ValidationSummary VALIDATION_SUMMARY_NO_ERRORS =
            new ValidationSummaryBuilder().setRowValidationSummaries(Collections.singletonList(ROW_VALIDATION_NO_ERROR))
                    .setAssociationSummaries(Collections.singletonList(ASSOCIATION_SUMMARY_NO_ERROR))
                    .build();

    private static final ValidationSummary VALIDATION_SUMMARY_ROW_ERRORS =
            new ValidationSummaryBuilder().setRowValidationSummaries(Collections.singletonList(
                    ROW_VALIDATION_SUMMARY_ERROR))
                    .build();

    private static final ValidationSummary VALIDATION_SUMMARY_ASS_ERRORS =
            new ValidationSummaryBuilder().setAssociationSummaries(Collections.singletonList(ASSOCIATION_SUMMARY_ERROR))
                    .build();

    @Before
    public void setUp() throws Exception {
        associationUploadService = new AssociationUploadService(associationFileUploadService,
                                                                associationOperationsService,
                                                                studyFileService,
                                                                studyRepository,
                                                                trackingOperationService);
    }

    @Test(expected = FileUploadException.class)
    public void uploadBlankFile() throws Exception {
        MockMultipartFile blankFile =
                new MockMultipartFile("data", "filename.txt", "text/plain", "".getBytes());
        doThrow(new FileUploadException()).when(studyFileService).upload(blankFile, STUDY.getId());
        associationUploadService.upload(blankFile, STUDY, SECURE_USER);
    }

    @Test(expected = SheetProcessingException.class)
    public void uploadIncorrectlyFormattedFile() throws Exception {
        MockMultipartFile incorrectlyFormattedFile =
                new MockMultipartFile("data", "filename.txt", "text/plain", "Not an Excel file".getBytes());
        File file = new File(incorrectlyFormattedFile.getOriginalFilename());

        // Stubbing
        when(studyFileService.getFileFromFileName(STUDY.getId(),
                                                  incorrectlyFormattedFile.getOriginalFilename())).thenReturn(file);
        when(associationFileUploadService.processAndValidateAssociationFile(file,
                                                                            "full")).thenThrow(new SheetProcessingException());
        associationUploadService.upload(incorrectlyFormattedFile, STUDY, SECURE_USER);
    }

    @Test(expected = IOException.class)
    public void uploadMissingFile() throws Exception {

        MockMultipartFile file =
                new MockMultipartFile("data", "filename.txt", "text/plain", "Blank file".getBytes());

        // Stubbing
        when(studyFileService.getFileFromFileName(STUDY.getId(),
                                                  file.getOriginalFilename())).thenThrow(new FileNotFoundException());
        associationUploadService.upload(file, STUDY, SECURE_USER);
    }

    @Test
    public void uploadFileNoErrors() throws Exception {
        MockMultipartFile uploadedFile =
                new MockMultipartFile("data", "filename.txt", "text/plain", "Test file".getBytes());
        File file = new File(uploadedFile.getOriginalFilename());

        // Return validation summary with no errors
        when(studyFileService.getFileFromFileName(STUDY.getId(),
                                                  uploadedFile.getOriginalFilename())).thenReturn(file);
        when(associationFileUploadService.processAndValidateAssociationFile(file,
                                                                            "full")).thenReturn(
                VALIDATION_SUMMARY_NO_ERRORS);

        // Test and verify
        assertThat(associationUploadService.upload(uploadedFile, STUDY, SECURE_USER)).hasSize(0);

        verify(studyFileService, times(1)).getFileFromFileName(STUDY.getId(), uploadedFile.getOriginalFilename());
        verify(associationFileUploadService, times(1)).processAndValidateAssociationFile(file, "full");

        // Verify batch upload event
        verify(trackingOperationService, times(1)).update(STUDY,SECURE_USER,
                                                          "ASSOCIATION_BATCH_UPLOAD", "1 associations created from upload of 'filename.txt'");
        verify(studyRepository, times(1)).save(STUDY);

        verify(associationOperationsService, times(1)).saveAssociation(ASSOCIATION,
                                                                      STUDY,
                                                                      ASSOCIATION_SUMMARY_NO_ERROR.getErrors());
        verify(associationOperationsService, times(1)).createAssociationCreationEvent(ASSOCIATION, SECURE_USER);
        verify(associationOperationsService, times(1)).runMapping(STUDY.getHousekeeping().getCurator(),
                                                                  ASSOCIATION,
                                                                  SECURE_USER);

    }

    @Test
    public void uploadFileRowErrors() throws Exception {
        MockMultipartFile uploadedFile =
                new MockMultipartFile("data", "filename.txt", "text/plain", "Test file".getBytes());
        File file = new File(uploadedFile.getOriginalFilename());

        // Return validation summary with no errors
        when(studyFileService.getFileFromFileName(STUDY.getId(), uploadedFile.getOriginalFilename())).thenReturn(file);
        when(associationFileUploadService.processAndValidateAssociationFile(file, "full")).thenReturn(
                VALIDATION_SUMMARY_ROW_ERRORS);

        // Test and verify
        assertThat(associationUploadService.upload(uploadedFile, STUDY, SECURE_USER)).hasSize(1);

        verify(studyFileService, times(1)).getFileFromFileName(STUDY.getId(), uploadedFile.getOriginalFilename());
        verify(associationFileUploadService, times(1)).processAndValidateAssociationFile(file, "full");
        verify(studyFileService, times(1)).deleteFile(STUDY.getId(), uploadedFile.getOriginalFilename());
    }

    @Test
    public void uploadFileValidationErrors() throws Exception {
        MockMultipartFile uploadedFile =
                new MockMultipartFile("data", "filename.txt", "text/plain", "Test file".getBytes());
        File file = new File(uploadedFile.getOriginalFilename());

        // Return validation summary with no errors
        when(studyFileService.getFileFromFileName(STUDY.getId(), uploadedFile.getOriginalFilename())).thenReturn(file);
        when(associationFileUploadService.processAndValidateAssociationFile(file, "full")).thenReturn(
                VALIDATION_SUMMARY_ASS_ERRORS);

        // Test and verify
        assertThat(associationUploadService.upload(uploadedFile, STUDY, SECURE_USER)).hasSize(1);

        verify(studyFileService, times(1)).getFileFromFileName(STUDY.getId(), uploadedFile.getOriginalFilename());
        verify(associationFileUploadService, times(1)).processAndValidateAssociationFile(file, "full");
        verify(studyFileService, times(1)).deleteFile(STUDY.getId(), uploadedFile.getOriginalFilename());
    }
}