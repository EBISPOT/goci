package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by emma on 10/08/2016.
 *
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class AttributeUpdateServiceTest {

    private AttributeUpdateService attributeUpdateService;

    @Before
    public void setUp() throws Exception {
        attributeUpdateService = new AttributeUpdateService();
    }

    @Test
    public void compareAttribute() throws Exception {
        assertThat(attributeUpdateService.compareAttribute("EFO trait", null, "asthma")).isEqualTo(
                "EFO trait set to 'asthma'");
        assertThat(attributeUpdateService.compareAttribute("EFO trait", "acne", "asthma")).isEqualTo(
                "EFO trait updated from 'acne' to 'asthma'");
        assertThat(attributeUpdateService.compareAttribute("EFO trait", "acne", null)).isEqualTo(
                "EFO trait value removed");
        assertThat(attributeUpdateService.compareAttribute("EFO trait", null, null)).isNull();
    }
}