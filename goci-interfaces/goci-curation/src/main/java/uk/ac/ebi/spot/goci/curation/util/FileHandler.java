package uk.ac.ebi.spot.goci.curation.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ebi.spot.goci.curation.dto.AnalysisDTO;
import uk.ac.ebi.spot.goci.curation.dto.StudyPatchRequest;
import uk.ac.ebi.spot.goci.curation.dto.FileUploadRequest;
import uk.ac.ebi.spot.goci.curation.exception.FileUploadException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

    public static List<AnalysisDTO> serializeDiseaseTraitAnalysisFile(FileUploadRequest fileUploadRequest) {

        CsvSchema.Builder builder = CsvSchema.builder();
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = builder.build().withHeader().withColumnSeparator('\t');
        MultipartFile multipartFile = fileUploadRequest.getMultipartFile();
        List<AnalysisDTO> studyPatchRequests;
        try {
            InputStream inputStream = multipartFile.getInputStream();
            MappingIterator<AnalysisDTO> iterator =
                    mapper.readerFor(AnalysisDTO.class).with(schema).readValues(inputStream);
            studyPatchRequests = iterator.readAll();
        } catch (IOException e) {
            throw new FileUploadException("Could not read the file");
        }
        return studyPatchRequests;
    }

    public static String serializePojoToTsv(List<?> pojoList) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        List<Map<String, Object>> dataList = csvMapper.convertValue(pojoList, new TypeReference<Object>() {
        });
        List<List<String>> csvData = new ArrayList<>();
        List<String> csvHead = new ArrayList<>();

        AtomicInteger counter = new AtomicInteger();
        dataList.forEach(row -> {
            List<String> rowData = new ArrayList<>();
            row.forEach((key, value) -> {
                rowData.add(String.valueOf(value));
                if (counter.get() == 0) {
                    csvHead.add(key);
                }
            });
            csvData.add(rowData);
            counter.getAndIncrement();
        });

        CsvSchema.Builder builder = CsvSchema.builder();
        csvHead.forEach(builder::addColumn);
        CsvSchema schema = builder.build().withHeader().withLineSeparator("\n").withColumnSeparator('\t');
        return csvMapper.writer(schema).writeValueAsString(csvData);
    }
}
