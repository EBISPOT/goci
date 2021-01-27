package uk.ac.ebi.spot.goci.curation.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.AssertTrue;

@Data
public class FileUploadRequest {

    private MultipartFile multipartFile;

    @AssertTrue(message = "File must be provided")
    public boolean isFileProvided() {
        return (multipartFile != null) && ( ! multipartFile.isEmpty());
    }
}
