package uk.ac.ebi.spot.goci.service;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;

import java.util.Collection;
import java.util.Map;

/**
 * Created by emma on 13/04/2016.
 *
 * @author emma
 *         <p>
 *         Service to read an spreadsheet of associations, create a map of headers in the file and process all further
 *         rows
 */
public interface UploadSheetProcessor {

    Map<Integer, String> createHeaderMap(XSSFRow row);

    Collection<AssociationUploadRow> readSheetRows(XSSFSheet sheet);
}