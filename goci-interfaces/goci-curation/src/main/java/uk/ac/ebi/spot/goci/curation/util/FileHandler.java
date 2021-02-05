package uk.ac.ebi.spot.goci.curation.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ebi.spot.goci.curation.constants.FileUploadType;
import uk.ac.ebi.spot.goci.curation.dto.AnalysisDTO;
import uk.ac.ebi.spot.goci.curation.dto.DiseaseTraitDto;
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
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = getSchemaFromMultiPartFile(fileUploadRequest.getMultipartFile());
        List<StudyPatchRequest> studyPatchRequests;
        try {
            InputStream inputStream = fileUploadRequest.getMultipartFile().getInputStream();
            MappingIterator<StudyPatchRequest> iterator =
                    mapper.readerFor(StudyPatchRequest.class).with(schema).readValues(inputStream);
            studyPatchRequests = iterator.readAll();
        } catch (IOException e) {
            throw new FileUploadException("Could not read the file");
        }
        return studyPatchRequests;
    }

    public static List<AnalysisDTO> serializeDiseaseTraitAnalysisFile(FileUploadRequest fileUploadRequest) {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = getSchemaFromMultiPartFile(fileUploadRequest.getMultipartFile());
        List<AnalysisDTO> analysisDTOS;
        try {
            InputStream inputStream = fileUploadRequest.getMultipartFile().getInputStream();
            MappingIterator<AnalysisDTO> iterator =
                    mapper.readerFor(AnalysisDTO.class).with(schema).readValues(inputStream);
            analysisDTOS = iterator.readAll();
        } catch (IOException e) {
            throw new FileUploadException("Could not read the file");
        }
        return analysisDTOS;
    }

    public static String serializePojoToTsv(List<?> pojoList) {
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
        String result = "";
        try {
            result = csvMapper.writer(schema).writeValueAsString(csvData);
        } catch (IOException e) {
            throw new FileUploadException("Could not read the file");
        }
        return result;
    }

    public static String getTemplate(String fileUploadType) {
        if (fileUploadType.equals(FileUploadType.SIMILARITY_ANALYSIS_FILE)) {

            List<AnalysisDTO> analysisDTO = new ArrayList<>();
            analysisDTO.add(AnalysisDTO.builder().userTerm("Yeast Infection").build());
            analysisDTO.add(AnalysisDTO.builder().userTerm("mean interproximal clinical attachment level").build());
            return serializePojoToTsv(analysisDTO);
        } else {

            List<DiseaseTraitDto> diseaseTraitDtos = new ArrayList<>();
            diseaseTraitDtos.add(DiseaseTraitDto.builder().trait("Uterine Carcinoma").build());
            diseaseTraitDtos.add(DiseaseTraitDto.builder().trait("Malaria Parasite").build());
            return serializePojoToTsv(diseaseTraitDtos);
        }
    }


    public static CsvSchema getSchemaFromMultiPartFile(MultipartFile multipartFile){
        CsvSchema.Builder builder = CsvSchema.builder();
        CsvSchema schema = builder.build().withHeader();
        if (FilenameUtils.getExtension(multipartFile.getOriginalFilename()).equals("tsv")) {
            schema = schema.withColumnSeparator('\t');
        }
        return schema;
    }
}
