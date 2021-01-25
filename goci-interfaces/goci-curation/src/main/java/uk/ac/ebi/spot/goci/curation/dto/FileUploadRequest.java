package uk.ac.ebi.spot.goci.curation.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.AssertTrue;

@Data
public class FileUploadRequest {

    private MultipartFile multipartFile;

    @NotBlank(message = "Please title must not be blank")
    private String title;

    @NotBlank(message = "Please description must not be blank")
    private String description;

    @AssertTrue(message = "File must be provided")
    public boolean isFileProvided() {
        return (multipartFile != null) && ( ! multipartFile.isEmpty());
    }
}
