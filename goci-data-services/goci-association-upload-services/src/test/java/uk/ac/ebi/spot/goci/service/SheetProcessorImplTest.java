package uk.ac.ebi.spot.goci.service;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Created by emma on 13/04/2016.
 *
 * @author emma
 *         <p>
 *         Test SheetProcessor class
 */
@RunWith(MockitoJUnitRunner.class)
public class SheetProcessorImplTest {

    private UploadSheetProcessor uploadSheetProcessor;

    private XSSFSheet sheet;

    @Mock
    private AssociationCalculationService associationCalculationService;

    @Before
    public void setUp() throws Exception {
        uploadSheetProcessor = new SheetProcessorImpl(associationCalculationService);


        // Create spreadsheet for testing
        XSSFWorkbook workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("test");

        // Create header row
        XSSFRow row1 = sheet.createRow(0);
        row1.createCell(0).setCellValue("SNP ID");
        row1.createCell(1).setCellValue("effect allele");
        row1.createCell(2).setCellValue("other allele");
        row1.createCell(3).setCellValue("effect allele frequency in controls");
        row1.createCell(4).setCellValue("gene");
        row1.createCell(5).setCellValue("p-value mantissa");
        row1.createCell(6).setCellValue("p-value exponent");
        row1.createCell(7).setCellValue("OR");
        row1.createCell(8).setCellValue("beta");
        row1.createCell(9).setCellValue("beta unit");
        row1.createCell(10).setCellValue("beta direction");
        row1.createCell(11).setCellValue("OR/beta SE");
        row1.createCell(12).setCellValue("OR/beta range");


        //Create second row
        XSSFRow row2 = sheet.createRow(1);
        row2.createCell(0).setCellValue("TEST, SFRP1"); // Gene(s)
        row2.createCell(1).setCellValue("rs123-?"); // Strongest SNP-Risk Allele
        row2.createCell(2).setCellValue("rs123"); // SNP
        row2.createCell(3).setCellValue("rs222, rs111, rs333"); // Proxy SNP
        row2.createCell(4); // Independent SNP risk allele frequency in controls (ONLY REQUIRED FOR SNP INTERACTION ASSOCIATIONS)
        row2.createCell(5)
                .setCellValue("0.6"); // Risk element (allele,haplotype or SNPxSNP interaction) frequency in controls
        row2.createCell(6).setCellValue(1); // P-value mantissa
        row2.createCell(7).setCellValue(-9); // P-value exponent
        row2.createCell(8); // P-value description
        row2.createCell(9).setCellValue("OR"); // Effect type
        row2.createCell(10).setCellValue(2.48); //  OR
        row2.createCell(11); //  OR reciprocal
        row2.createCell(12); // Beta
        row2.createCell(13); // Beta unit
        row2.createCell(14); // Beta direction
        row2.createCell(15); // Range
        row2.createCell(16); // OR reciprocal range
        row2.createCell(17).setCellValue(0.01); // Standard Error
        row2.createCell(18); // OR/Beta description
        row2.createCell(19).setCellValue("N"); // Multi-SNP Haplotype?
        row2.createCell(20).setCellValue("N"); // SNP:SNP interaction?
        row2.createCell(21); // SNP Status  (ONLY REQUIRED FOR SNP INTERACTION ASSOCIATIONS)
        row2.createCell(22).setCellValue("novel"); // SNP type (novel/known)
        row2.createCell(23).setCellValue("EFO_0003894"); // SNP type (novel/known)

    }

    @Test
    public void testReadSheetRows() throws Exception {

    }

    @Test
    public void testCreateHeaderMap() throws Exception {

        Map<Integer, String> headerRowMap = uploadSheetProcessor.createHeaderMap(sheet.getRow(0));
        verifyZeroInteractions(associationCalculationService);
        assertThat(headerRowMap).hasSize(13);
        assertThat(headerRowMap).contains(entry(0, "SNP ID"),
                                          entry(1, "effect allele"),
                                          entry(2, "other allele"),
                                          entry(3, "effect allele frequency in controls"),
                                          entry(4, "gene"),
                                          entry(5, "p-value mantissa"),
                                          entry(6, "p-value exponent"),
                                          entry(7, "OR"),
                                          entry(8, "beta"),
                                          entry(9, "beta unit"),
                                          entry(10, "beta direction"),
                                          entry(11, "OR/beta SE"),
                                          entry(12, "OR/beta range")
        );
    }
}