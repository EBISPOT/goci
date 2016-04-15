package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.StudyFileSummary;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by emma on 15/04/2016.
 *
 * @author emma
 *         <p>
 *         Service to handle upload and download of study files
 */
@Service
public class StudyFileService {

    @Value("${study.directory}")
    private File studyDirRoot;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public synchronized void createStudyDir(Long studyId) {

        if (getStudyDirRoot() == null) {
            getLog().error("Cannot find root dir for study with ID" + studyId);
            throw new IllegalStateException("Cannot find root directory");
        }

        if (!getStudyDirRoot().exists()) {
            getLog().debug("Making root directory '" + getStudyDirRoot().getAbsolutePath() + "'...");
            boolean success = getStudyDirRoot().mkdirs();
            if (success) {
                getLog().debug("Directory created!");
            }
            else {
                getLog().error("Directory not created for study with ID: " + studyId);
            }
        }

        boolean success = getStudyDirPath(studyId).mkdir();
        if (!success) {
            getLog().error("Could not create directory for a study");
        }
    }


    public List<StudyFileSummary> getStudyFiles(Long studyId) {
        List<StudyFileSummary> files = new ArrayList<>();

        File pathToStudyDir = getStudyDirPath(studyId);

        if (pathToStudyDir.exists()) {
            String[] fileNames = pathToStudyDir.list();

            for (String filename : fileNames) {
                StudyFileSummary studyFileSummary = new StudyFileSummary(filename);
                files.add(studyFileSummary);
            }
        }
        else {
            getLog().error("No study directory found for study with ID: " + studyId);
        }
        return files;
    }

    public File getStudyDirRoot() {
        return studyDirRoot;
    }

    /**
     * Create a subdir based on the study id
     *
     * @param id Study ID
     */
    private File getStudyDirPath(Long id) {
        String uploadDirName = getStudyDirRoot() + File.separator + id;
        return new File(uploadDirName);
    }
}
