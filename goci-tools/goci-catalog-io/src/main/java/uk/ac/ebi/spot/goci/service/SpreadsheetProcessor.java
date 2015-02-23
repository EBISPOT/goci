package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/02/15
 */
public class SpreadsheetProcessor {
    @Value("${gwas.export.delimiter}")
    private String delim;
    @Value("${gwas.export.encoding}")
    private String encoding = "UTF-8";

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
            return readFromInputStream(in);
        }
    }

    public String[][] readFromInputStream(InputStream in) {
        return new String[][]{};
    }
}
