package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ebi.spot.goci.exception.EnsemblMappingException;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationSummary;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.ValidationSummary;
import uk.ac.ebi.spot.goci.service.AssociationFileUploadService;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by emma on 14/06/2016.
 *
 * @author emma
 */
@Service
public class AssociationUploadService {

    private StudyFileService studyFileService;

    private CurrentUserDetailsService currentUserDetailsService;

    private AssociationFileUploadService associationFileUploadService;

    private AssociationOperationsService associationOperationsService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public AssociationUploadService(StudyFileService studyFileService,
                                    CurrentUserDetailsService currentUserDetailsService,
                                    AssociationFileUploadService associationFileUploadService,
                                    AssociationOperationsService associationOperationsService) {
        this.studyFileService = studyFileService;
        this.currentUserDetailsService = currentUserDetailsService;
        this.associationFileUploadService = associationFileUploadService;
        this.associationOperationsService = associationOperationsService;
    }

    public ValidationSummary upload(MultipartFile file, Study study, HttpServletRequest request)
            throws IOException, EnsemblMappingException {

        String originalFilename = file.getOriginalFilename();
        getLog().info("Uploading file: ".concat(originalFilename));

        // Upload file
        uploadFile(file, study.getId());

        // Send file, including path, to SNP batch loader process
        File uploadedFile = studyFileService.getFileFromFileName(study.getId(), originalFilename);
        studyFileService.createFileUploadEvent(study.getId(), currentUserDetailsService.getUserFromRequest(request));

        ValidationSummary validationSummary = null;
        validationSummary =
                associationFileUploadService.processAssociationFile(uploadedFile, "full");

        List<Association> associationsToSave = new ArrayList<>();
        if (validationSummary != null) {
            // Check if we have any errors
            long rowErrorCount = validationSummary.getRowValidationSummaries().parallelStream()
                    .filter(rowValidationSummary -> !rowValidationSummary.getErrors().isEmpty())
                    .count();

            // Errors found
            if (rowErrorCount > 0) {
                studyFileService.deleteFile(study.getId(), originalFilename);
                getLog().error("Errors found in file: " + originalFilename);
            }
            else {

                long associationErrorCount = validationSummary.getAssociationSummaries().parallelStream()
                        .filter(associationSummary -> !associationSummary.getErrors().isEmpty())
                        .count();

                if (associationErrorCount > 0) {
                    studyFileService.deleteFile(study.getId(), originalFilename);
                    getLog().error("Errors found in file: " + originalFilename);
                }
                else {
                    associationsToSave = validationSummary.getAssociationSummaries().stream().map(
                            AssociationSummary::getAssociation)
                            .collect(Collectors.toList());
                }
            }
        }

        if (!associationsToSave.isEmpty()) {
            for (Association association: associationsToSave){
                studyFileService.createFileUploadEvent(study.getId(), currentUserDetailsService.getUserFromRequest(request));
                associationOperationsService.saveAndMap(association, study);
            }
        }

        return validationSummary;
    }

    private void uploadFile(MultipartFile file, Long studyId) throws IOException {
        // TODO DO WE KNOW TO THROW FILENOTFOUNDEXCEPTION
        studyFileService.upload(file, studyId);
    }
}
