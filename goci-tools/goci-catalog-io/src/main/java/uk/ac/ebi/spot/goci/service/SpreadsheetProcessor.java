package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/02/15
 */
@Service
public class SpreadsheetProcessor {
    @Value("${gwas.export.delimiter:\t}")
    private String delim;
    @Value("${gwas.export.encoding:UTF-8}")
    private String encoding;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public void writeToFile(String[][] data, File fileOut) throws IOException {
        writeToFile(data, fileOut, this.delim);
    }

    public void writeToFile(String[][] data, File fileOut, String delimiter) throws IOException {
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileOut))) {
            writeToOutputStream(data, out, delimiter);
        }
    }

    public void writeToOutputStream(String[][] data, OutputStream out) throws IOException {
        writeToOutputStream(data, out, this.delim);
    }

    public void writeToOutputStream(String[][] data, OutputStream out, String delimiter)
            throws IOException {
        int rowCount = 0;
        for (String[] line : data) {
            rowCount++;
            int colCount = 0;
            for (String cell : line) {
                colCount++;
                out.write(cell.getBytes(encoding));
                if (colCount >= line.length) {
                    break;
                }
                else {
                    out.write(delimiter.getBytes(encoding));
                }
            }
            if (rowCount >= data.length) {
                break;
            }
            else {
                out.write(System.lineSeparator().getBytes(encoding));
            }
            out.flush();
        }
    }

    public String[][] readFromFile(File fileIn) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileIn))) {
            return readFromInputStream(in, encoding, true, true);
        }
    }

    public String[][] readFromInputStream(InputStream in,
                                          String encoding,
                                          boolean stripEscaping,
                                          boolean trimWhitespace) throws IOException {
        // if we have a merged document being read from a stream that doesn't support mark(),
        // we won't be able to read it using current methods
        if (!in.markSupported()) {
            throw new UnsupportedOperationException(
                    "Unable to read documents from a remote URL currently " +
                            "(input stream that does not support the mark() operation).  " +
                            "Try downloading the file and reading a local copy instead.");
        }
        else {
            // read from the input stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding));

            getLog().debug("Reading data from input stream and converting to String[][]");
            boolean containsEscapedNewlines = false;

            // mark the start of this section and track the number of bytes we read so we can read from stream
            // elsewhere without losing contents stored in the buffer of this reader
            getLog().trace("Marking stream...");
            in.mark(Integer.MAX_VALUE);
            long bytesInSection = 0;
            // grab every line in the file
            List<String[]> lines = new ArrayList<>();
            String line = reader.readLine();
            if (line != null) {
                // +1 to add on size of newline character automatically trimmed by readLine()
                bytesInSection += (line.getBytes(encoding).length + 1);
                getLog().trace(
                        "Next line: " + (line.length() < 10 ? line : line.substring(0, 10)) + "...\tBytes read now = " +
                                bytesInSection);
            }
            getLog().trace("First line read from input stream is:\n\t" + line);
            while (line != null) {
                if (!line.trim().equals("")) {
                    if (!line.startsWith("#")) {
                        // non-empty, non-commented line so add to matrix
                        // first, reformat and unescape lines if necessary
                        String firstLine = line;
                        while (endsWithEscapedNewline(firstLine)) {
                            // loop through lines: if any lines end with an escaped newline we should concatenate them
                            containsEscapedNewlines = true;
                            String secondLine = reader.readLine();
                            // +1 to add on size of newline character automatically trimmed by readLine()
                            bytesInSection += (secondLine.getBytes(encoding).length + 1);
                            getLog().trace("Next line: " +
                                                   (secondLine.length() < 10 ? secondLine :
                                                           secondLine.substring(0, 10)) +
                                                   "...\tBytes read now = " + bytesInSection);
                            line = compensateForEscapedNewlines(firstLine, secondLine);
                            firstLine = secondLine;
                        }

                        // split the line into it's constituent cells
                        String[] cells = splitLine(line, false, trimWhitespace);

                        // now remove all escaping if the flag is set - do this second to avoid changing how cells are split
                        if (stripEscaping) {
                            cells = removeEscaping(cells);
                        }

                        // finally, add our cells
                        lines.add(cells);
                    }
                    else {
                        // commented line, add the comment wholesale
                        lines.add(new String[]{line});
                    }
                }
                else {
                    // empty line, add an empty String[] to represent this line
                    lines.add(new String[0]);
                }

                // and read next line
                line = reader.readLine();
                if (line != null) {
                    // +1 to add on size of newline character automatically trimmed by readLine()
                    bytesInSection += (line.getBytes(encoding).length + 1);
                    getLog().trace(
                            "Next line: " + (line.length() < 10 ? line : line.substring(0, 10)) +
                                    "...\tBytes read now = " +
                                    bytesInSection);
                }
            }

            if (containsEscapedNewlines) {
                getLog().warn("This file contains escaped newline characters.  " +
                                      "Logical line numbers (as reported by this parser) may be different from " +
                                      "physical line numbers, depending on the application being used to view this file");
            }

            String[][] result = lines.toArray(new String[lines.size()][]);
            getLog().debug("Read all data from stream, String[][] contains " + result.length + " elements");
            getLog().debug("Total number of bytes read from this section: " + bytesInSection);
            in.reset();
            long bytesSkipped = in.skip(bytesInSection);
            getLog().debug("Reset input stream and skipped " + bytesSkipped + " bytes");
            return result;
        }
    }

    /**
     * Determine whether the string supplied ends with an escaped newline character.  If the line does not end with a
     * newline character, it is assumed to have been previously removed, and therefore this will determine if the line
     * ends "within" a quote.
     *
     * @param line the line to inspect
     * @return true if the string ends "within" a quote, false otherwise
     */
    public boolean endsWithEscapedNewline(String line) {
        // split line into cells
        String[] cells = line.split("\t");

        // counting back from last cell...
        int i = cells.length - 1;
        String nextCell = cells[i];

        while (i > -1) {
            if (nextCell.endsWith("\"")) {
                // if it ends with a quote, we're no longer escaped
                return false;
            }
            else if (nextCell.startsWith("\"")) {
                // if it starts with a quote, we're defo escaped
                return true;
            }
            else {
                // nextCell neither ends or starts with newline, so check previous cell for context
                nextCell = cells[i--];
            }
        }

        // if we got to here, it means we reached first cell and none are escaped
        return false;
    }

    /**
     * Compensate for a newline character that has been inserted into a single line and then escaped.       *
     *
     * @param firstLine  the first line, i.e. the one ending with an escaped newline character
     * @param secondLine the second line, i.e. the one that is a logical continuation of the first
     * @return the result string, with the newline character removed
     */
    public String compensateForEscapedNewlines(String firstLine, String secondLine) {
        if (endsWithEscapedNewline(firstLine)) {
            return firstLine.concat(System.getProperty("line.separator")).concat(secondLine);
        }
        else {
            // if the first line doesn't end in an escaped newline, you've done something daft
            String message = "A line was supplied that did not end in an escaped " +
                    "newline character.  The line was: " + firstLine;

            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Splits this line into cells, ignoring any escaping found.  Empty cells are preserved as empty strings.  Any
     * trailing whitespace in cells present is trimmed if 'trimWhitespace' is true, otherwise every cell is preserved
     * verbosely.
     *
     * @param line           the line to split
     * @param ignoreEscaping whether to ignore escaped tab and newline characters
     * @param trimWhitespace whether to act in 'strict' mode, preserving any leading and trailing whitespace characters,
     *                       or whether to automatically remove them
     * @return the resulting string array
     */
    public String[] splitLine(String line, boolean ignoreEscaping, boolean trimWhitespace) {
        if (!ignoreEscaping) {
            String[] cells;
            if (trimWhitespace) {
                // trim the line and split into cells
                cells = line.trim().split("\t", -1);
            }
            else {
                // just split into cells
                cells = line.split("\t", -1);
            }

            List<String> logicalCells = new ArrayList<String>();

            StringBuffer sb = null;
            for (String s : cells) {
                // trim if desired
                String cell = s;
                if (trimWhitespace) {
                    cell = cell.trim();
                }

                if (cell.startsWith("\"") && !cell.endsWith("\"")) {
                    // this cell starts with a quote but doesn't end with one
                    // so this is the start of a new logical cell
                    // NOTE that we might have trimmed escaped whitespace from the end of this string, so restore

                    // restore by adding a full stop to the end of the original string, then trim
                    cell = s.concat(".").trim();
                    // now remove final character
                    cell = cell.substring(0, cell.length() - 1);
                    // start new logical cell
                    sb = new StringBuffer();
                    sb.append(cell).append("\t");
                }
                else {
                    if (!cell.startsWith("\"") && cell.endsWith("\"")) {
                        // this cell ends with a quote but doesn't start with one
                        // so this is could be a continuation of the current logical cell
                        if (sb != null) {
                            // there is a logical cell, append
                            sb.append(cell);
                            String logicalCell = sb.toString();
                            logicalCells.add(logicalCell);
                        }
                        else {
                            // there is no current logical cell, so just ignore...
                            // might just be that the cell contains a quoted remark (e.g. 'Tony said "Hello World"')
                            logicalCells.add(cell);
                        }
                    }
                    else {
                        // this cell both starts and ends with or without quotes
                        logicalCells.add(cell);
                    }
                }
            }
            return logicalCells.toArray(new String[logicalCells.size()]);
        }
        else {
            // trim if desired - check each cell
            if (trimWhitespace) {
                // trim the line and split into cells
                String[] cells = line.trim().split("\t", -1);
                List<String> logicalCells = new ArrayList<String>();
                for (String cell : cells) {
                    logicalCells.add(cell.trim());
                }
                return logicalCells.toArray(new String[logicalCells.size()]);
            }
            else {
                return line.split("\t", -1);
            }
        }
    }

    /**
     * Removes any quotation marks around the given cells that are used to escape the values contained within, using
     * relaxed mode by default.  Equivalent to <code>removeEscaping(cells, false);</code>
     *
     * @param cells the cells to remove escaping from
     * @return the resulting cells, after escaping has been removed
     */
    public String[] removeEscaping(String[] cells) {
        return removeEscaping(cells, false);
    }

    /**
     * Removes any escaping present in the given cells, akin to how Excel writes out CSV files surrounding cells with
     * quotation marks. If quotation marks are used to surround some values, and this signifies that they should be used
     * as part of the value supplied; this includes tabs, newlines and other such characters.
     * <p>
     * In 'strict' mode, all such quotation marks are removed from the whole cell.  Strict checking requires regular
     * expression checking, and therefore can fail if the string contains irregular markup (for example, HTML
     * fragments).  Also note that this method will not work with lines that end in an escaped newline character, and
     * you should explicitly remove these characters first using the {@link #endsWithEscapedNewline(String)} and {@link
     * #compensateForEscapedNewlines(String, String)} methods first.
     * <p>
     * In 'relaxed' mode, this method removes quotations ONLY when present at the beginning and end of a cell. This form
     * is different from strict checking in that it does not use regular expressions to check for quoted substrings -
     * rather, it just checks the first and last character of each cell for quotations. Relaxed mode will work with
     * lines that end in an escaped newline character or may not pass regular expression checks.
     * <p>
     *
     * @param cells     the cells on a line, after escaping with quotation marks
     * @param useStrict whether to use the strict definition of escaping (i.e. quotes in any position on a line), or
     *                  whether to consider quotations at the beginning and end of cells only.
     * @return a cell with all quotations removed
     */
    public String[] removeEscaping(String[] cells, boolean useStrict) {
        String[] result = new String[cells.length];
        for (int i = 0; i < cells.length; i++) {
            String cell = cells[i];
            if (useStrict) {
                // regex to find quoted substrings
                Pattern p = Pattern.compile("\"[^\"\\r\\n]*\"");
                Matcher m = p.matcher(cell);
                boolean quotedSubstrings = false;
                while (m.find()) {
                    MatchResult mr = m.toMatchResult();
                    getLog().debug("Found string escaped with quotes, '" + mr.group() + "', removing quotations");
                    quotedSubstrings = true;
                }
                // finally, remove all quotes
                if (quotedSubstrings) {
                    result[i] = cell.replaceAll("\"", "");
                }
                else {
                    result[i] = cell;
                }
            }
            else {
                if (cell.startsWith("\"") && cell.endsWith("\"")) {
                    result[i] = cell.substring(1, cell.length() - 1);
                }
                else {
                    result[i] = cell;
                }
            }
        }

        return result;
    }
}
