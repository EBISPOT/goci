package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.model.AssociationReport;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 17/12/2015.
 *
 * @author emma
 *         <p>
 *         Test
 */
@RunWith(MockitoJUnitRunner.class)
public class AssociationMappingErrorServiceTest {

    @InjectMocks
    private AssociationMappingErrorService associationMappingErrorService;

    @Mock
    private AssociationReport associationReport;

    @Test
    public void testCreateAssociationErrorMap() {

        // Checking return type
        Map mockedMap = new HashMap();
        Map<String, String> map = associationMappingErrorService.createAssociationErrorMap(associationReport);
        assertEquals(mockedMap, map);

        when(associationReport.getSnpError()).thenReturn("Snp error");
        assertEquals("Snp error", associationReport.getSnpError());

        when(associationReport.getSnpGeneOnDiffChr()).thenReturn("Snp gene on different chromosome");
        assertEquals("Snp gene on different chromosome", associationReport.getSnpGeneOnDiffChr());

        when(associationReport.getNoGeneForSymbol()).thenReturn("No gene for symbol");
        assertEquals("No gene for symbol", associationReport.getNoGeneForSymbol());

        when(associationReport.getRestServiceError()).thenReturn("Rest service error");
        assertEquals("Rest service error", associationReport.getRestServiceError());

        when(associationReport.getSuspectVariationError()).thenReturn("Suspect variation error");
        assertEquals("Suspect variation error", associationReport.getSuspectVariationError());

        when(associationReport.getGeneError()).thenReturn("Gene error");
        assertEquals("Gene error", associationReport.getGeneError());

    }
}
