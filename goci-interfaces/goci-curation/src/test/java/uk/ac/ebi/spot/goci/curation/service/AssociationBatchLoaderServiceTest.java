package uk.ac.ebi.spot.goci.curation.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.model.Association;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 18/12/2015.
 *
 * @author emma
 *         <p>
 *         Unit test for AssociationBatchLoaderService
 */
@RunWith(MockitoJUnitRunner.class)
public class AssociationBatchLoaderServiceTest {

    @InjectMocks
    private AssociationBatchLoaderService associationBatchLoaderService;

    @Mock
    private AssociationSheetProcessor associationSheetProcessor;

    @Mock
    private XSSFSheet sheet;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testProcessData() throws IOException, InvalidFormatException {

        Collection<Association> testAssociations = new ArrayList<>();

        // Checking return type


        // TODO FULL PATH
        Collection<Association> returnedCollection = associationBatchLoaderService.processData(
                "/Users/emma/Desktop/java_checkouts/goci_new/goci/goci-interfaces/goci-curation/src/test/java/uk/ac/ebi/spot/goci/curation/service/test.xlsx");
        assertEquals(returnedCollection, testAssociations);

        // Stubbing behaviour of Association Sheet Processor
        when(associationSheetProcessor.readSnpAssociations(sheet)).thenReturn(testAssociations);
        assertEquals(testAssociations, associationSheetProcessor.readSnpAssociations(sheet));
        verify(associationSheetProcessor, atLeastOnce()).readSnpAssociations(sheet);

    }

    // TODO TEST EXCEPTIONS ARE THROWN
}
