package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ebi.spot.goci.curation.model.AssociationUploadErrorView;
import uk.ac.ebi.spot.goci.exception.EnsemblMappingException;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationSummary;
import uk.ac.ebi.spot.goci.model.RowValidationSummary;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.ValidationSummary;
import uk.ac.ebi.spot.goci.service.AssociationFileUploadService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by emma on 14/06/2016.
 *
 * @author emma
 *         <p>
 *         Service to upload and validate a spreadsheet file containing SNP associations
 */
@Service
public class AssociationUploadService {

    private StudyFileService studyFileService;

    private AssociationFileUploadService associationFileUploadService;

    private AssociationOperationsService associationOperationsService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public AssociationUploadService(StudyFileService studyFileService,
                                    AssociationFileUploadService associationFileUploadService,
                                    AssociationOperationsService associationOperationsService) {
        this.studyFileService = studyFileService;
        this.associationFileUploadService = associationFileUploadService;
        this.associationOperationsService = associationOperationsService;
    }

    public List<AssociationUploadErrorView> upload(MultipartFile file, Study study, SecureUser user)
            throws IOException, EnsemblMappingException {

        List<AssociationUploadErrorView> fileErrors = new ArrayList<>();
        String originalFilename = file.getOriginalFilename();
        getLog().info("Uploading file: ".concat(originalFilename));

        // Upload file
        try {
            uploadFile(file, study.getId());

            // Send file, including path, to SNP batch loader process
            File uploadedFile = studyFileService.getFileFromFileName(study.getId(), originalFilename);
            ValidationSummary validationSummary =
                    associationFileUploadService.processAndValidateAssociationFile(uploadedFile, "full");

            List<Association> associationsToSave = new ArrayList<>();
            if (validationSummary != null) {
                // Check if we have any row errors
                long rowErrorCount = validationSummary.getRowValidationSummaries().parallelStream()
                        .filter(rowValidationSummary -> !rowValidationSummary.getErrors().isEmpty())
                        .count();

                // Errors found
                if (rowErrorCount > 0) {
                    studyFileService.deleteFile(study.getId(), originalFilename);
                    getLog().error("Row errors found in file: " + originalFilename);
                    validationSummary.getRowValidationSummaries().forEach(
                            rowValidationSummary -> fileErrors.addAll(processRowError(rowValidationSummary))
                    );
                }
                else {

                    long associationErrorCount = validationSummary.getAssociationSummaries().parallelStream()
                            .filter(associationSummary -> !associationSummary.getErrors().isEmpty())
                            .count();

                    if (associationErrorCount > 0) {
                        studyFileService.deleteFile(study.getId(), originalFilename);
                        getLog().error("Association errors found in file: " + originalFilename);
                        validationSummary.getAssociationSummaries().forEach(
                                associationSummary -> fileErrors.addAll(processAssociationError(associationSummary))
                        );
                    }
                    else {
                        associationsToSave = validationSummary.getAssociationSummaries().stream().map(
                                AssociationSummary::getAssociation)
                                .collect(Collectors.toList());
                    }
                }
            }

            if (!associationsToSave.isEmpty()) {
                studyFileService.createFileUploadEvent(study.getId(), user);

                for (Association association : associationsToSave) {
                    associationOperationsService.saveNewAssociation(association, study);
                }
            }

            return fileErrors;
        }
        catch (IOException e) {
            throw new IOException(e);
        }
    }

    public void uploadAndSave(MultipartFile file, Study study, SecureUser user)
            throws IOException, EnsemblMappingException {

        String originalFilename = file.getOriginalFilename();
        getLog().info("Uploading file: ".concat(originalFilename));

        // Upload file
        try {
            uploadFile(file, study.getId());

            // Send file, including path, to SNP batch loader process
            File uploadedFile = studyFileService.getFileFromFileName(study.getId(), originalFilename);
            Collection<Association> associations =
                    associationFileUploadService.processFile(uploadedFile);


            if (!associations.isEmpty()) {
                studyFileService.createFileUploadEvent(study.getId(), user);

                for (Association association : associations) {
                    associationOperationsService.saveNewAssociation(association, study);
                }
            }
        }
        catch (IOException e) {
            throw new IOException(e);
        }
    }


    /**
     * Upload a file to the study specific dir
     *
     * @param file    XLSX file supplied by user
     * @param studyId study to link file to
     */
    private void uploadFile(MultipartFile file, Long studyId) throws IOException {
        studyFileService.upload(file, studyId);
    }

    /**
     * Process summary of row validation into object that can be returned to view
     *
     * @param rowValidationSummary
     */
    private List<AssociationUploadErrorView> processRowError(RowValidationSummary rowValidationSummary) {

        List<AssociationUploadErrorView> errors = new ArrayList<>();
        rowValidationSummary.getErrors().forEach(validationError -> {
                                                     AssociationUploadErrorView associationUploadErrorView =
                                                             new AssociationUploadErrorView(rowValidationSummary.getRow().getRowNumber(),
                                                                                            validationError.getField(),
                                                                                            validationError.getError(), validationError.getWarning());
                                                     errors.add(associationUploadErrorView);
                                                 }
        );
        return errors;
    }

    /**
     * Process summary of association validation into object that can be returned to view
     *
     * @param associationSummary
     */
    private List<AssociationUploadErrorView> processAssociationError(AssociationSummary associationSummary) {

        List<AssociationUploadErrorView> errors = new ArrayList<>();
        associationSummary.getErrors().forEach(validationError -> {
                                                   AssociationUploadErrorView associationUploadErrorView =
                                                           new AssociationUploadErrorView(associationSummary.getRowNumber(),
                                                                                          validationError.getField(),
                                                                                          validationError.getError(), validationError.getWarning());
                                                   errors.add(associationUploadErrorView);
                                               }
        );
        return errors;
    }
}
