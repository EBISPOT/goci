package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import uk.ac.ebi.spot.goci.builder.SecureUserBuilder;
import uk.ac.ebi.spot.goci.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.curation.exception.FileUploadException;
import uk.ac.ebi.spot.goci.exception.SheetProcessingException;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.service.AssociationFileUploadService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.mockito.Mockito.doThrow;
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
        when(associationFileUploadService.processAssociationFile(file,
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

    public void uploadFileNoErrors() throws Exception {
        MockMultipartFile uploadedFile =
                new MockMultipartFile("data", "filename.txt", "text/plain", "Test file".getBytes());
        File file = new File(uploadedFile.getOriginalFilename());
        when(associationFileUploadService.processAssociationFile(file, "full")).thenReturn(null);
    }
}