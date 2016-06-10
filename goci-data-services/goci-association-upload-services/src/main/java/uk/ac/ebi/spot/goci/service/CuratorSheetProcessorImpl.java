package uk.ac.ebi.spot.goci.service;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.utils.TranslateUploadHeaders;
import uk.ac.ebi.spot.goci.utils.UploadFileHeader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by emma on 10/06/2016.
 *
 * @author emma
 *         <p>
 *         This class takes an Excel spreadsheet sheet and extracts all records. For each record, a row object is
 *         created containing association information
 */
public class CuratorSheetProcessorImpl implements UploadSheetProcessor {

    private TranslateUploadHeaders translateUploadHeaders;

    // Logging
    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public CuratorSheetProcessorImpl(TranslateUploadHeaders translateUploadHeaders) {
        this.translateUploadHeaders = translateUploadHeaders;
    }

    @Override public Collection<AssociationUploadRow> readSheetRows(XSSFSheet sheet) {


    }


    @Override public Map<Integer, UploadFileHeader> createHeaderMap(XSSFRow row) {
        Map<Integer, UploadFileHeader> headerMap = new HashMap<>();

        if (row.getPhysicalNumberOfCells() != 0) {

            short minColIx = row.getFirstCellNum();
            short maxColIx = row.getLastCellNum();
            for (short colIx = minColIx; colIx < maxColIx; colIx++) {
                XSSFCell cell = row.getCell(colIx);
                UploadFileHeader headerType =
                        translateUploadHeaders.translateToEnumValue(cell.getStringCellValue().trim());
                headerMap.put((int) colIx, headerType);
            }
        }
        else {
            getLog().error("Header column contains no cells");
        }
        return headerMap;
    }


}
