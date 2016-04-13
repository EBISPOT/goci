package uk.ac.ebi.spot.goci.service;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;

import java.util.Collection;

/**
 * Created by emma on 13/04/2016.
 *
 * @author emma
 *         <p>
 *         Service to read an spreadsheet of associations
 */
public interface UploadSheetProcessor {
    Collection<AssociationUploadRow> readSheetRows(XSSFSheet sheet);
}
