package uk.ac.ebi.spot.goci.model;

import org.junit.Test;
import uk.ac.ebi.spot.goci.model.deposition.BodyOfWorkDto;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSampleDto;
import uk.ac.ebi.spot.goci.model.deposition.DepositionStudyDto;

import static org.junit.Assert.*;

public class BeanMapperTest {

    @Test
    public void testConvertStudy() {
        DepositionStudyDto studyDto = new DepositionStudyDto();
        UnpublishedStudy study = BeanMapper.MAPPER.convert(studyDto);
        assertNotNull(study);
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