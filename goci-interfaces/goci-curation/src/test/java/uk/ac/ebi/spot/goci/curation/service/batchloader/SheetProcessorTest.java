package uk.ac.ebi.spot.goci.curation.service.batchloader;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.curation.model.batchloader.BatchUploadRow;
import uk.ac.ebi.spot.goci.curation.service.AssociationCalculationService;

import java.util.Collection;

import static org.junit.Assert.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by emma on 24/03/2016.
 *
 * @author emma
 *         <p>
 *         Test for /goci/curation/service/batchloader/SheetProcessor.java
 */
@RunWith(MockitoJUnitRunner.class)
public class SheetProcessorTest {

    private SheetProcessor sheetProcessor;

    @Mock
    private XSSFSheet sheet;

    @Mock
    private AssociationCalculationService associationCalculationService;

    @Before
    public void setUp() throws Exception {
        sheetProcessor = new SheetProcessor(associationCalculationService);
    }

    @Test
    public void testMocks() {
        // Test mock creation
        assertNotNull(sheet);
        assertNotNull(associationCalculationService);
    }

    @Test
    public void testReadSheetRows() throws Exception {
        assertThat(sheetProcessor.readSheetRows(sheet)).isInstanceOf(Collection.class);
        assertThat(sheetProcessor.readSheetRows(sheet)).hasOnlyElementsOfType(BatchUploadRow.class);
    }
}