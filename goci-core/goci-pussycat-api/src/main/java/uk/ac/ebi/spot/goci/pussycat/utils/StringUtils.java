package uk.ac.ebi.spot.goci.pussycat.utils;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 03/08/12
 */
public class StringUtils {
    private static final String HEXES = "0123456789ABCDEF";

    public static String getHexRepresentation(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }
}
