package uk.ac.ebi.spot.goci.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;

/**
 * Created by emma on 13/04/2016.
 *
 * @author emma
 *         <p>
 *         Simple utility class to handle values in the upload spreadsheet
 */
public class SheetCellProcessingService {

    public static String processStringValue(XSSFCell cell) {
        String stringValue = null;
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    stringValue = cell.getRichStringCellValue().getString().trim();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    stringValue = Double.toString(cell.getNumericCellValue());
                    break;
            }
        }
        return stringValue;
    }

    public static Integer processIntValues(XSSFCell cell) {
        Integer intValue = null;
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    intValue = (int) cell.getNumericCellValue();
                    break;
                default:
                    intValue = null;
                    break;
            }
        }
        return intValue;
    }

    public static Float processFloatValues(XSSFCell cell) {
        Float floatValue = null;
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    floatValue = (float) cell.getNumericCellValue();
                    break;
                default:
                    floatValue = null;
                    break;
            }
        }
        return floatValue;
    }
}