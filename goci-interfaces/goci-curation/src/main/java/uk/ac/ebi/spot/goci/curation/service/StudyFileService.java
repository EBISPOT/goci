package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ebi.spot.goci.service.TrackingOperationService;
import uk.ac.ebi.spot.goci.curation.exception.FileUploadException;
import uk.ac.ebi.spot.goci.curation.exception.NoStudyDirectoryException;
import uk.ac.ebi.spot.goci.curation.model.StudyFileSummary;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private TrackingOperationService trackingOperationService;

    private StudyRepository studyRepository;

    @Autowired
    public StudyFileService(@Qualifier("studyTrackingOperationServiceImpl") TrackingOperationService trackingOperationService,
                            StudyRepository studyRepository) {
        this.trackingOperationService = trackingOperationService;
        this.studyRepository = studyRepository;
    }

    public synchronized void createStudyDir(Long studyId) {

        if (getStudyDirRoot() == null) {
            getLog().error("Cannot find root dir for study with ID" + studyId);
            throw new IllegalStateException("Cannot find root directory");
        }

        if (!getStudyDirRoot().exists()) {
            getLog().info("Making root directory '" + getStudyDirRoot().getAbsolutePath() + "'...");
            boolean success = getStudyDirRoot().mkdirs();
            if (success) {
                getLog().debug("Root directory created!");
            }
            else {
                getLog().error("Root directory not created");
                throw new NoStudyDirectoryException("Root directory not created");
            }
        }

        // Make a study specific dir based on study ID
        boolean success = getStudyDirPath(studyId).mkdir();
        if (!success) {
            getLog().error("Could not create directory for study with ID " + studyId);
            throw new NoStudyDirectoryException("Could not create directory for a study");
        }
    }

    /**
     * Get all files in a study directory
     *
     * @param studyId Study ID which is used to find study specific dir
     */
    public List<StudyFileSummary> getStudyFiles(Long studyId) {
        List<StudyFileSummary> files = new ArrayList<>();
        File pathToStudyDir = getStudyDirPath(studyId);

        if (pathToStudyDir.exists()) {
            File[] filesInDir = pathToStudyDir.listFiles();

            if (filesInDir != null) {
                for (File file : filesInDir) {
                    // Ignore any hidden files
                    if (!file.isHidden()) {
                        if (file.exists()) {
                            StudyFileSummary studyFileSummary =
                                    new StudyFileSummary(file.getName());
                            files.add(studyFileSummary);
                        }
                    }
                }
            }
            else {
                getLog().error("No files in study directory for study with ID: " + studyId);
            }
        }
        else {
            getLog().warn("No study directory found for study with ID: " + studyId);

            // create a study dir
            createStudyDir(studyId);
        }
        return files;
    }

    /**
     * Upload a file to a study specific dir
     *
     * @param fileFromUpload File user is uploading
     * @param studyId        Study ID which is used to find study specific dir
     */
    public synchronized void upload(MultipartFile fileFromUpload, Long studyId)
            throws IOException {

        if (!fileFromUpload.isEmpty()) {
            File file = createFileInStudyDir(fileFromUpload.getOriginalFilename(), studyId);

            // Copy contents of multipart request to newly created file
            try {
                fileFromUpload.transferTo(file);
                // Set some permissions
                file.setWritable(true, false);
                file.setReadable(true, false);
            }
            catch (IOException e) {
                getLog().error("Unable to copy file: " + fileFromUpload.getName() + " to study dir");
                throw new FileUploadException("Unable to copy file");
            }
        }
        else {
            getLog().error(fileFromUpload.getName() + " is empty");
            throw new FileUploadException("Upload file is empty");
        }
    }

    /**
     * Record file upload event
     *
     * @param studyId Study ID which is used to find study specific dir
     * @param user    User carrying out request
     */
    public void createFileUploadEvent(Long studyId, SecureUser user) {
        Study study = studyRepository.findOne(studyId);
        trackingOperationService.update(study, user, "STUDY_FILE_UPLOAD");
        studyRepository.save(study);
        getLog().info("Study ".concat(String.valueOf(study.getId())).concat(" updated"));
    }

    /**
     * Delete a file by study ID and name
     *
     * @param studyId  Study ID, this will help locate dir
     * @param fileName Name of file to delete
     */
    @Async
    public void deleteFile(Long studyId, String fileName) {
        File fileToDelete = null;
        try {
            fileToDelete = getFileFromFileName(studyId, fileName);
            boolean fileDeleted = fileToDelete.delete();
            if (fileDeleted) {getLog().info(fileName.concat(" deleted"));}
        }
        catch (FileNotFoundException e) {
            getLog().error(fileName.concat(" not found"));
        }
    }

    /**
     * Return a subdir based on the study id
     *
     * @param id Study ID
     */
    private File getStudyDirPath(Long id) {
        String uploadDirName = getStudyDirRoot() + File.separator + id;
        return new File(uploadDirName);
    }

    /**
     * Create a file object
     *
     * @param name Name of file
     * @param id   Study ID
     */
    private File createFileInStudyDir(String name, Long id) throws IOException {

        // Check study dir exists, if not create it
        File studyDirPath = getStudyDirPath(id);
        if (!studyDirPath.exists()) {
            createStudyDir(id);
        }

        // Create our new file
        String fileName = getStudyDirRoot() + File.separator + id + File.separator + name;
        File file = new File(fileName);

        // If file already exists, backup current file
        if (file.exists()) {
            getLog().info("File " + file.getName() + " already exists, will backup current file");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String now = dateFormat.format(new Date());
            String newFileName =
                    getStudyDirRoot() + File.separator + id + File.separator + file.getName().concat("_").concat(now);
            File backUpExistingFile = new File(newFileName);
            boolean fileBackupSuccess = file.renameTo(backUpExistingFile);

            if (!fileBackupSuccess) {
                getLog().error("Could not create a backup of file " + file.getName());
                throw new FileUploadException("Could not create a backup of file");
            }
        }

        boolean success = file.createNewFile();
        if (!success) {
            getLog().error("Could not create a file: " + fileName);
            throw new FileUploadException("Could not create a file");
        }
        return file;
    }

    /**
     * Get file by study ID and path
     *
     * @param studyId  Study ID, this will help locate dir
     * @param fileName Name of file to return
     */
    public File getFileFromFileName(Long studyId, String fileName) throws FileNotFoundException {
        String fileNameWithFullPath = getStudyDirRoot() + File.separator + studyId + File.separator + fileName;
        File file = new File(fileNameWithFullPath);

        if (file.exists() && file.isFile()) {
            return file;
        }
        else {
            getLog().error(fileNameWithFullPath.concat(" not found"));
            throw new FileNotFoundException(fileNameWithFullPath.concat(" not found"));
        }
    }

    public File getStudyDirRoot() {
        return studyDirRoot;
    }

    public void setStudyDirRoot(File studyDirRoot) {
        this.studyDirRoot = studyDirRoot;
    }
}