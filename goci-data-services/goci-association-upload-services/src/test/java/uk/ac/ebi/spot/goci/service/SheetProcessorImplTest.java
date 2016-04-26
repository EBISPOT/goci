package uk.ac.ebi.spot.goci.service;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;

import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.data.MapEntry.entry;

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

    @Before
    public void setUp() throws Exception {
        uploadSheetProcessor = new SheetProcessorImpl();

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
        row2.createCell(0).setCellValue("rs123"); // SNP
        row2.createCell(1).setCellValue("rs123-?"); // Strongest SNP-Risk Allele
        row2.createCell(2).setCellValue("rs222-T"); // Other Allele
        row2.createCell(3).setCellValue("0.6"); // Effect allele frequency in controls
        row2.createCell(4).setCellValue("TEST, SFRP1"); // Gene(s)
        row2.createCell(5).setCellValue(1); // P-value mantissa
        row2.createCell(6).setCellValue(-9); // P-value exponent
        row2.createCell(7).setCellValue(2.48); //  OR
        row2.createCell(8); // Beta
        row2.createCell(9); // Beta unit
        row2.createCell(10); // Beta direction
        row2.createCell(11).setCellValue(0.01); // Standard Error
        row2.createCell(12).setCellValue("[1.22-1.43]"); // Range
    }

    @Test
    public void testReadSheetRows() throws Exception {

        Collection<AssociationUploadRow> rows = uploadSheetProcessor.readSheetRows(sheet);
        assertThat(rows).isInstanceOf(Collection.class);
        assertThat(rows).hasOnlyElementsOfType(AssociationUploadRow.class);
        assertThat(rows).hasSize(1);

        // Check first row
        assertThat(rows).extracting("rowNumber",
                                    "authorReportedGene",
                                    "strongestAllele",
                                    "snp",
                                    "proxy",
                                    "riskFrequency",
                                    "associationRiskFrequency",
                                    "pvalueMantissa",
                                    "pvalueExponent",
                                    "pvalueDescription",
                                    "orPerCopyNum",
                                    "orPerCopyRecip",
                                    "betaNum",
                                    "betaUnit",
                                    "betaDirection",
                                    "range",
                                    "orPerCopyRecipRange",
                                    "standardError",
                                    "description",
                                    "multiSnpHaplotype",
                                    "snpInteraction",
                                    "snpStatus",
                                    "snpType",
                                    "efoTrait")
                .containsExactly(tuple(1,
                                       "TEST, SFRP1",
                                       "rs123-?",
                                       "rs123",
                                       null,
                                       null,
                                       "0.6",
                                       1,
                                       -9,
                                       null, (float) 2.48, null, null, null, null, "[1.22-1.43]",
                                       null, (float) 0.01, null, null, null, null, null, null));


    }

    @Test
    public void testCreateHeaderMap() throws Exception {

        Map<Integer, String> headerRowMap = uploadSheetProcessor.createHeaderMap(sheet.getRow(0));
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