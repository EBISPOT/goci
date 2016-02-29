package uk.ac.ebi.spot.goci.curation.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.curation.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.model.Association;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 18/12/2015.
 *
 * @author emma
 *         <p>
 *         Test for AssociationBatchLoaderService
 */
@RunWith(MockitoJUnitRunner.class)
public class AssociationBatchLoaderServiceTest {

    private AssociationBatchLoaderService associationBatchLoaderService;

    @Mock
    private AssociationSheetProcessor associationSheetProcessor;

    private static final Association ASS1 =
            new AssociationBuilder().setId(800L).build();

    private static final Association ASS2 =
            new AssociationBuilder().setId(801L).build();

    @Before
    public void setUp() throws Exception {
        associationBatchLoaderService = new AssociationBatchLoaderService(associationSheetProcessor);
    }

    @Test
    public void testProcessData() throws IOException, InvalidFormatException {

        File file = new File("/Users/emma/Desktop/java_checkouts/goci_new/goci/goci-interfaces/goci-curation/src/test/resources/test.xlsx");

        // Stubbing
  
   assertNotNull(associationBatchLoaderService.processData(file.getAbsolutePath()));


        // Run tests
//   assertNotNull(associationBatchLoaderService.processData(file.getName()));
   //     verify(associationSheetProcessor, times(1)).readSnpAssociations(sheet);
   //     assertNotNull(associationBatchLoaderService.processData(file.getName()));
        assertThat(associationBatchLoaderService.processData(file.getAbsolutePath())).containsExactly(ASS1, ASS2);
    }
}
