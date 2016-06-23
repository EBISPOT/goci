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
import uk.ac.ebi.spot.goci.builder.GeneBuilder;
import uk.ac.ebi.spot.goci.builder.LocusBuilder;
import uk.ac.ebi.spot.goci.builder.RiskAlleleBuilder;
import uk.ac.ebi.spot.goci.builder.RowValidationSummaryBuilder;
import uk.ac.ebi.spot.goci.builder.SecureUserBuilder;
import uk.ac.ebi.spot.goci.builder.SingleNucleotidePolymorphismBuilder;
import uk.ac.ebi.spot.goci.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.builder.ValidationErrorBuilder;
import uk.ac.ebi.spot.goci.builder.ValidationSummaryBuilder;
import uk.ac.ebi.spot.goci.curation.exception.FileUploadException;
import uk.ac.ebi.spot.goci.exception.SheetProcessingException;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationSummary;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.RowValidationSummary;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.ValidationError;
import uk.ac.ebi.spot.goci.model.ValidationSummary;
import uk.ac.ebi.spot.goci.service.AssociationFileUploadService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
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


    private AssociationUploadService associationUploadService;

    private static final SecureUser SECURE_USER =
            new SecureUserBuilder().setId(564L).setEmail("test@test.com").setPasswordHash("738274$$").build();

    private static final Study STUDY = new StudyBuilder().setId(100L).build();

    private static final Gene GENE_01 = new GeneBuilder().setGeneName("SFRP1").build();

    private static final Gene GENE_02 = new GeneBuilder().setGeneName("ELF1").build();

    private static final SingleNucleotidePolymorphism SNP_01 =
            new SingleNucleotidePolymorphismBuilder().setRsId("rs123").build();

    private static final RiskAllele RA_01 = new RiskAlleleBuilder().setRiskAlleleName("rs123-?").setSnp(SNP_01).build();

    private static final Locus LOCUS_03 =
            new LocusBuilder().setId(121L)
                    .setAuthorReportedGenes(Arrays.asList(GENE_01, GENE_02))
                    .setStrongestRiskAlleles(Collections.singletonList(RA_01)).build();

    private static final Association ASSOCIATION =
            new AssociationBuilder().setId(100L)
                    .setOrPerCopyNum((float) 1.22)
                    .setRange("[0.82-0.92]")
                    .setStandardError(
                            (float) 0.6)
                    .setPvalueMantissa(2)
                    .setPvalueExponent(-7)
                    .setLoci(Collections.singletonList(LOCUS_03))
                    .build();

    private static final AssociationUploadRow ROW_1 = new AssociationUploadRowBuilder().setRowNumber(1)
            .setSnp("rs123")
            .setAuthorReportedGene("SFRP1, ELF1")
            .setStrongestAllele("rs123-?")
            .setAssociationRiskFrequency("0.52")
            .setPvalueMantissa(2)
            .setPvalueExponent(-7)
            .setOrPerCopyNum((float) 1.22)
            .setRange("[0.82-0.92]")
            .setStandardError((float) 0.6)
            .build();

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

    private static final ValidationSummary VALIDATION_SUMMARY_NO_ERRORS =
            new ValidationSummaryBuilder().setRowValidationSummaries(Collections.singletonList(ROW_VALIDATION_NO_ERROR))
                    .setAssociationSummaries(Collections.singletonList(ASSOCIATION_SUMMARY_NO_ERROR))
                    .build();


    private static final RowValidationSummary ROW_VALIDATION_ERROR =
            new RowValidationSummaryBuilder().setRow(ROW_1).setErrors(Collections.singletonList(ERROR)).build();


    private static final ValidationSummary VALIDATION_SUMMARY_ROW_ERRORS =
            new ValidationSummaryBuilder().setRowValidationSummaries(Collections.singletonList(ROW_VALIDATION_ERROR))
                    .build();

    private static final ValidationSummary VALIDATION_SUMMARY_ASS_ERRORS =
            new ValidationSummaryBuilder().setAssociationSummaries(Collections.singletonList(ASSOCIATION_SUMMARY_ERROR))
                    .build();

    @Before
    public void setUp() throws Exception {
        associationUploadService = new AssociationUploadService(studyFileService,
                                                                associationFileUploadService,
                                                                associationOperationsService);
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

/*    @Test
    public void uploadFileNoErrors() throws Exception {
        MockMultipartFile uploadedFile =
                new MockMultipartFile("data", "filename.txt", "text/plain", "Test file".getBytes());
        File file = new File(uploadedFile.getOriginalFilename());

        // Return validation summary with no errors
        when(studyFileService.getFileFromFileName(STUDY.getId(),
                                                  uploadedFile.getOriginalFilename())).thenReturn(file);
        when(associationFileUploadService.processAndValidateAssociationFile(file,
                                                                 "full")).thenReturn(VALIDATION_SUMMARY_NO_ERRORS);

        // Test and verify
        assertThat(associationUploadService.upload(uploadedFile, STUDY, SECURE_USER)).hasSize(0);

        verify(studyFileService, times(1)).getFileFromFileName(STUDY.getId(), uploadedFile.getOriginalFilename());
        verify(associationFileUploadService, times(1)).processAndValidateAssociationFile(file, "full");
        verify(studyFileService, times(1)).createFileUploadEvent(STUDY.getId(), SECURE_USER);
        verify(associationOperationsService, times(1)).saveNewAssociation(ASSOCIATION, STUDY);
    }

    @Test
    public void uploadFileRowErrors() throws Exception {
        MockMultipartFile uploadedFile =
                new MockMultipartFile("data", "filename.txt", "text/plain", "Test file".getBytes());
        File file = new File(uploadedFile.getOriginalFilename());

        // Return validation summary with no errors
        when(studyFileService.getFileFromFileName(STUDY.getId(),
                                                  uploadedFile.getOriginalFilename())).thenReturn(file);
        when(associationFileUploadService.processAndValidateAssociationFile(file,
                                                                 "full")).thenReturn(VALIDATION_SUMMARY_ROW_ERRORS);

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
        when(studyFileService.getFileFromFileName(STUDY.getId(),
                                                  uploadedFile.getOriginalFilename())).thenReturn(file);
        when(associationFileUploadService.processAndValidateAssociationFile(file,
                                                                 "full")).thenReturn(VALIDATION_SUMMARY_ASS_ERRORS);

        // Test and verify
        assertThat(associationUploadService.upload(uploadedFile, STUDY, SECURE_USER)).hasSize(1);

        verify(studyFileService, times(1)).getFileFromFileName(STUDY.getId(), uploadedFile.getOriginalFilename());
        verify(associationFileUploadService, times(1)).processAndValidateAssociationFile(file, "full");
        verify(studyFileService, times(1)).deleteFile(STUDY.getId(), uploadedFile.getOriginalFilename());
    }*/
}