package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import uk.ac.ebi.spot.goci.service.TrackingOperationService;
import uk.ac.ebi.spot.goci.builder.SecureUserBuilder;
import uk.ac.ebi.spot.goci.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.curation.exception.FileUploadException;
import uk.ac.ebi.spot.goci.curation.model.StudyFileSummary;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 20/04/2016.
 *
 * @author emma
 *         <p>
 *         Test for StudyFileService
 */
@RunWith(MockitoJUnitRunner.class)
public class StudyFileServiceTest {

    private StudyFileService studyFileService;

    @Mock
    private TrackingOperationService trackingOperationService;

    @Mock
    private StudyRepository studyRepository;

    private static final SecureUser SECURE_USER =
            new SecureUserBuilder().setId(564L).setEmail("test@test.com").setPasswordHash("738274$$").build();

    private static final Long STUDY_ID = 100L;

    private static final Study STUDY = new StudyBuilder().setId(100L).build();

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        studyFileService = new StudyFileService(trackingOperationService, studyRepository);
        studyFileService.setStudyDirRoot(testFolder.newFolder("test_study_files"));
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateStudyDirWithNullRootDir() throws Exception {
        studyFileService.setStudyDirRoot(null);
        studyFileService.createStudyDir(STUDY_ID);
    }

    @Test
    public void testCreateStudyDirWithRootDir() throws Exception {

        studyFileService.createStudyDir(STUDY_ID);
        assert (studyFileService.getStudyDirRoot().exists());

        // Check our study specific dir was created in root
        assertThat(studyFileService.getStudyDirRoot().listFiles()).isNotEmpty();
        assertThat(studyFileService.getStudyDirRoot().listFiles()).hasSize(1);
        assertThat(studyFileService.getStudyDirRoot().listFiles()).hasOnlyElementsOfType(File.class);
        assertThat(studyFileService.getStudyDirRoot().listFiles()).extracting("name").contains("100");
    }

    @Test
    public void testUploadWithFile() throws Exception {

        // Create our study dir , it should be empty
        studyFileService.createStudyDir(STUDY_ID);
        assertThat(studyFileService.getStudyFiles(STUDY_ID)).isEmpty();

        // Mock a file coming in via the controller
        MockMultipartFile file =
                new MockMultipartFile("data", "filename.txt", "text/plain", "Some study details".getBytes());
        studyFileService.upload(file, STUDY_ID);

        assertThat(studyFileService.getStudyFiles(STUDY_ID)).isNotEmpty();
        assertThat(studyFileService.getStudyFiles(STUDY_ID)).hasSize(1);
        assertThat(studyFileService.getStudyFiles(STUDY_ID)).hasOnlyElementsOfType(StudyFileSummary.class);
        assertThat(studyFileService.getStudyFiles(STUDY_ID)).extracting("fileName").contains("filename.txt");
    }

    @Test(expected = FileUploadException.class)
    public void testUploadWithEmptyFile() throws Exception {

        // Create our study dir , it should be empty
        studyFileService.createStudyDir(STUDY_ID);
        assertThat(studyFileService.getStudyFiles(STUDY_ID)).isEmpty();

        // Mock a file coming in via the controller
        MockMultipartFile file =
                new MockMultipartFile("data", "filename.txt", "text/plain", "".getBytes());
        studyFileService.upload(file, STUDY_ID);
    }

    @Test
    public void testGetStudyDirRoot() throws Exception {
        studyFileService.setStudyDirRoot(testFolder.newFolder("test"));
        assertThat(studyFileService.getStudyDirRoot()).hasName("test");
    }

    @Test
    public void testCreateFileUploadEvent() {
        // Stubbing
        when(studyRepository.findOne(STUDY_ID)).thenReturn(STUDY);

        studyFileService.createFileUploadEvent(STUDY_ID, SECURE_USER);
        verify(studyRepository, times(1)).findOne(STUDY_ID);
        verify(trackingOperationService, times(1)).update(STUDY,
                                                          SECURE_USER,
                                                          "STUDY_FILE_UPLOAD");
        verify(studyRepository, times(1)).save(STUDY);
    }
}