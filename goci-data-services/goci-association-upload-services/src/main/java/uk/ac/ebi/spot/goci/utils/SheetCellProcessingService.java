package uk.ac.ebi.spot.goci.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import uk.ac.ebi.spot.goci.exception.CellProcessingException;

/**
 * Created by emma on 13/04/2016.
 *
 * @author emma
 *         <p>
 *         Simple utility class to handle values in the upload spreadsheet
 */
public class SheetCellProcessingService {


    // This method raises an exception if the cell is not a String. The cell must be a String.
    public static String processMandatoryStringValue(XSSFCell cell) {
        String stringValue = null;
        try {
            stringValue = cell.getRichStringCellValue()
                    .getString()
                    .trim();
        } catch (Exception ise){
            throw new CellProcessingException("The field must contain both letters and numbers");
        }
        return stringValue;
    }

    // This method raises an exception if the conversation fails.
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
                default:
                    throw new CellProcessingException("The field value cannot be converted");
            }
        }
        return stringValue;
    }

    // This method raises an exception if the conversation fails.
    public static Integer processIntValues(XSSFCell cell) {
        Integer intValue = null;
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    Long roundedValue = Math.round(cell.getNumericCellValue());
                    intValue = roundedValue.intValue();
                    break;
                default:
                    throw new CellProcessingException("The field must be a Number");
            }
        }
        return intValue;
    }

    // This method raises an exception if the conversation fails.
    public static Float processFloatValues(XSSFCell cell) {
        Float floatValue = null;
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    floatValue = (float) cell.getNumericCellValue();
                    break;
                default:
                    throw new CellProcessingException("The field must be a Float");
            }
        }
        return floatValue;
    }
}