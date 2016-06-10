package uk.ac.ebi.spot.goci.utils;

/**
 * Created by emma on 10/06/2016.
 *
 * @author emma
 *         <p>
 *         Interface service that translates an upload spreadsheet header
 */
public interface TranslateUploadHeaders {

    UploadFileHeader translateToEnumValue(String value);
}
