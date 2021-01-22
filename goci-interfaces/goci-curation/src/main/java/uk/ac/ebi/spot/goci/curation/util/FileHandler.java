package uk.ac.ebi.spot.goci.curation.util;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ebi.spot.goci.curation.dto.StudyPatchRequest;
import uk.ac.ebi.spot.goci.curation.dto.FileUploadRequest;
import uk.ac.ebi.spot.goci.curation.exception.FileUploadException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FileHandler {

    private FileHandler() {
        // Hide implicit default constructor
    }

    public static List<StudyPatchRequest> getStudyPatchRequests(FileUploadRequest fileUploadRequest) {

        CsvSchema.Builder builder = CsvSchema.builder();
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = builder.build().withHeader().withColumnSeparator('\t');
        MultipartFile multipartFile = fileUploadRequest.getMultipartFile();
        List<StudyPatchRequest> studyPatchRequests;
        try {
            InputStream inputStream = multipartFile.getInputStream();
            MappingIterator<StudyPatchRequest> iterator =
                    mapper.readerFor(StudyPatchRequest.class).with(schema).readValues(inputStream);
            studyPatchRequests = iterator.readAll();
        } catch (IOException e) {
            throw new FileUploadException("Could not read the file");
        }
        return studyPatchRequests;
    }
}
