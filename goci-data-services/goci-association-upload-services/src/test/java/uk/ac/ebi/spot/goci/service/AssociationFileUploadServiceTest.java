package uk.ac.ebi.spot.goci.service;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.model.ValidationSummary;

import java.io.File;
import java.io.FileNotFoundException;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 14/07/2016.
 *
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class AssociationFileUploadServiceTest {

    @Mock
    private UploadSheetProcessorBuilder uploadSheetProcessorBuilder;

    @Mock
    private AssociationRowProcessor associationRowProcessor;

    @Mock
    private ValidationService validationService;

    @Mock
    private SheetCreationService sheetCreationService;

    @Mock
    private UploadSheetProcessor uploadSheetProcessor;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private AssociationFileUploadService associationFileUploadService;

    @Before
    public void setUp() throws Exception {
        associationFileUploadService = new AssociationFileUploadService(uploadSheetProcessorBuilder,
                                                                        associationRowProcessor,
                                                                        validationService,
                                                                        sheetCreationService);
    }

    @Test
    public void processAndValidateAssociationFile() throws Exception {

        // Create a temp file
        final File file = folder.newFile("myfile.txt");

        // Stubbing
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("test");
        when(sheetCreationService.createSheet(file.getAbsolutePath())).thenReturn(sheet);
        when(uploadSheetProcessorBuilder.buildProcessor("full")).thenReturn(uploadSheetProcessor);

        ValidationSummary validationSummary =
                associationFileUploadService.processAndValidateAssociationFile(file, "full");
        verify(sheetCreationService, times(1)).createSheet(Matchers.anyString());
        verify(uploadSheetProcessorBuilder, times(1)).buildProcessor("full");
        verify(uploadSheetProcessor, times(1)).readSheetRows(sheet);
    }

    @Test(expected = FileNotFoundException.class)
    public void processAndValidateNonexistentAssociationFile() throws Exception {
        File fileDoesNotExist = new File("test");
        associationFileUploadService.processAndValidateAssociationFile(fileDoesNotExist, "full");
    }
}