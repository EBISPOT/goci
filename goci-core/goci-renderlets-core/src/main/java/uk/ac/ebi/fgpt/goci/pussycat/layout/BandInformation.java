package uk.ac.ebi.fgpt.goci.pussycat.layout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA. User: dwelter Date: 31/05/12 Time: 16:15 To change this template use File | Settings |
 * File Templates.
 */
public class BandInformation implements Comparable<BandInformation> {
    private final String bandName;
    private final String chromosome;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public BandInformation(String bandName) {
        Matcher m = Pattern.compile("(^[0-9XxYy]+)").matcher(bandName);
        if (m.find()) {
            this.bandName = bandName;
            this.chromosome = m.group(1);
        }
        else {
            throw new RuntimeException(
                    "Could not render an association - unrecognised chromosome name in band '" + bandName + "'");
        }
    }

    public BandInformation(String name, String chrom) {
        this.bandName = name;
        this.chromosome = chrom;
    }

    public String getBandName() {
        return bandName;
    }

    public String getChromosome() {
        return chromosome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BandInformation that = (BandInformation) o;

        if (bandName != null ? !bandName.equals(that.bandName) : that.bandName != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return bandName != null ? bandName.hashCode() : 0;
    }

    @Override public int compareTo(BandInformation bi) {
        String thisName = getBandName();
        String thatName = bi.getBandName();

        String thisChrom, thatChrom, thisArm, thatArm, thisPosition, thatPosition;

        if (thisName.contains("p")) {
            String[] parts = thisName.split("p");
            thisChrom = parts[0];
            thisArm = "p";
            thisPosition = parts[1];
        }
        else {
            if (thisName.contains("q")) {
                String[] parts = thisName.split("q");
                thisChrom = parts[0];
                thisArm = "q";
                thisPosition = parts[1];
            }
            else {
                getLog().error("Illegal band name notation - " + thisName + " is not a real band name");
                return 1;
            }
        }

        if (thatName.contains("p")) {
            String[] parts = thatName.split("p");
            thatChrom = parts[0];
            thatArm = "p";
            thatPosition = parts[1];
        }
        else {
            if (thatName.contains("q")) {
                String[] parts = thatName.split("q");
                thatChrom = parts[0];
                thatArm = "q";
                thatPosition = parts[1];
            }
            else {
                getLog().error("Illegal band name notation - " + thisName + " is not a real band name");
                return 1;
            }
        }

        // compare by chromosome first
        int thisChromInt;
        try {
            thisChromInt = Integer.parseInt(thisChrom);
        }
        catch (NumberFormatException e) {
            // should be x or y
            if (thisChrom.equalsIgnoreCase("x")) {
                thisChromInt = 23;
            }
            else if (thisChrom.equalsIgnoreCase("y")) {
                thisChromInt = 24;
            }
            else {
                thisChromInt = 30;
            }
        }
        int thatChromInt;
        try {
            thatChromInt = Integer.parseInt(thatChrom);
        }
        catch (NumberFormatException e) {
            // should be x or y
            if (thatChrom.equalsIgnoreCase("x")) {
                thatChromInt = 23;
            }
            else if (thatChrom.equalsIgnoreCase("y")) {
                thatChromInt = 24;
            }
            else {
                thatChromInt = 30;
            }
        }
        int chromDelta = thisChromInt - thatChromInt;
        if (chromDelta == 0) {
            if (thisArm.equals(thatArm)) {
                float thisPositionFloat;
                try {
                    thisPositionFloat = Float.parseFloat(thisPosition);
                }
                catch (NumberFormatException e) {
                    thisPositionFloat = Integer.MAX_VALUE;
                }
                float thatPositionFloat;
                try {
                    thatPositionFloat = Float.parseFloat(thatPosition);
                }
                catch (NumberFormatException e) {
                    thatPositionFloat = Integer.MAX_VALUE;
                }
                float positionDelta = thisPositionFloat - thatPositionFloat;
                return ((Double) (positionDelta > 0 ? Math.ceil(positionDelta) : Math.floor(positionDelta))).intValue();
            }
            else {
                return thisArm.equals("p") ? -1 : 1;
            }
        }
        else {
            return chromDelta;
        }
    }
}
