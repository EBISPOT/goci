package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.junit.Test;
import uk.ac.ebi.spot.goci.model.deposition.BodyOfWorkDto;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSampleDto;
import uk.ac.ebi.spot.goci.model.deposition.DepositionStudyDto;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSubmission;

import java.io.IOException;

import static org.junit.Assert.*;

public class BeanMapperTest {

    @Test
    public void testConvertStudy() {
        String dtoString = "{\n" + "      \"study_tag\": \"t1\",\n" + "      \"study_accession\": \"GCST90000116\",\n" +
                "      \"genotyping_technology\": \"Genome-wide genotyping array\",\n" +
                "      \"imputation\": false,\n" + "      \"variant_count\": 4,\n" + "      \"trait\": \"bmi\",\n" +
                "      \"summary_statistics_file\": \"abc123.tsv\",\n" +
                "      \"checksum\": \"a1195761f082f8cbc2f5a560743077cc\",\n" +
                "      \"summary_statistics_assembly\": \"GRCh38\",\n" + "      \"readme_file\": \"some text\"\n" +
                "    }";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        DepositionStudyDto studyDto = null;
        try {
            studyDto = objectMapper.readValue(dtoString, DepositionStudyDto.class);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        UnpublishedStudy study = BeanMapper.MAPPER.convert(studyDto);
        assertNotNull(study);
        assertNotNull(study.getSummaryStatsFile());
    }

    @Test
    public void testConvertSample() {
        DepositionSampleDto sampleDto = new DepositionSampleDto();
        UnpublishedAncestry ancestry = BeanMapper.MAPPER.convert(sampleDto);
        assertNotNull(ancestry);
    }

    @Test
    public void testConvertBodyOfWork() {
        BodyOfWorkDto bodyOfWorkDto = new BodyOfWorkDto();
        BodyOfWork bodyOfWork = BeanMapper.MAPPER.convert(bodyOfWorkDto);
        assertNotNull(bodyOfWork);
    }
}