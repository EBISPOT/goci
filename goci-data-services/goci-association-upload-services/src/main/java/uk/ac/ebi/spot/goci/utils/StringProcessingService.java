package uk.ac.ebi.spot.goci.utils;

/**
 * Created by emma on 13/04/2016.
 *
 * @author emma
 *         <p>
 *         Simple utility class to tidy up user entered strings before saving to database
 */
public class StringProcessingService {

    public static String tidy_curator_entered_string(String string) {
        String newString = string.trim();
        String newline = System.getProperty("line.separator");

        if (newString.contains(newline)) {
            newString = newString.replace(newline, "");
        }
        return newString;
    }
}