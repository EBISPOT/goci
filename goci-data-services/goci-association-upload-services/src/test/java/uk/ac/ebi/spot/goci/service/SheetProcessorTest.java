package uk.ac.ebi.spot.goci.service;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

/**
 * Created by emma on 12/04/2016.
 *
 * @author emma
 *         <p>
 *         Test SheetProcessor class
 */
@RunWith(MockitoJUnitRunner.class)
public class SheetProcessorTest {

    private SheetProcessor sheetProcessor;

    private XSSFSheet sheet;

    @Mock
    private AssociationCalculationService associationCalculationService;

    @Before
    public void setUp() throws Exception {
        sheetProcessor = new SheetProcessor(associationCalculationService);

        // Create spreadsheet for testing
        XSSFWorkbook workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("test");

        //Create Second Row, ignore first row as that is a header
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

        Collection<AssociationUploadRow> rows = sheetProcessor.readSheetRows(sheet);
        assertThat(rows).isInstanceOf(Collection.class);
        assertThat(rows).hasOnlyElementsOfType(AssociationUploadRow.class);

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
                                    "effectType",
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
                                       "rs222, rs111, rs333",
                                       null,
                                       "0.6",
                                       1,
                                       -9,
                                       null,
                                       "OR", (float) 2.48, null, null, null, null,
                                       null, null, (float) 0.01, null, "N", "N", null, "novel", "EFO_0003894"));
    }
}