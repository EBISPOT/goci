package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

/**
 * Created by emma on 20/04/2016.
 *
 * @author emma
 *         <p>
 *         Test for StudyFileService
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(StudyFileService.class)
public class StudyFileServiceTest {

    private StudyFileService studyFileService;

    private File studyDirRoot;

    private static final Long STUDY_ID = Long.valueOf(100);

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        studyFileService = new StudyFileService();
        studyDirRoot = null;
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateStudyDirWithNullRootDir() throws Exception {
        studyFileService.createStudyDir(STUDY_ID);
    }

    @Test
    public void testGetStudyFiles() throws Exception {

    }

    @Test
    public void testUpload() throws Exception {

    }

    @Test
    public void testGetFileFromFileName() throws Exception {

    }

    @Test
    public void testGetStudyDirRoot() throws Exception {

    }
}