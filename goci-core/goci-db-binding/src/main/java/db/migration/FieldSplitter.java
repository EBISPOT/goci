package db.migration;

import java.util.ArrayList;
import java.util.List;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 29/01/15
 */
abstract class FieldSplitter {
    static List<String> split(String commaSeparatedString) {
        List<String> elements = new ArrayList<>();
        String[] rawTokens = commaSeparatedString.split(",");
        for (String rawToken : rawTokens) {
            elements.add(rawToken.trim());
        }
        return elements;
    }

    static List<String> split(String stringToSplit, String regexToSplitOn) {
        List<String> elements = new ArrayList<>();
        String[] rawTokens = stringToSplit.split(regexToSplitOn);
        for (String rawToken : rawTokens) {
            elements.add(rawToken.trim());
        }
        return elements;
    }

    static List<String> split(String stringToSplit, String splitOnRegex, String thenSplitOnRegex) {
        List<String> elements = new ArrayList<>();
        String[] rawTokens = stringToSplit.split(splitOnRegex);
        for (String rawToken : rawTokens) {
            String[] nextTokens = rawToken.split(thenSplitOnRegex);
            for (String nextToken : nextTokens) {
                elements.add(nextToken.trim());
            }
        }
        return elements;
    }

    static List<String> split(String stringToSplit,
                              String splitOnRegex,
                              String thenSplitOnRegex,
                              String finallySplitOnRegex) {
        List<String> elements = new ArrayList<>();
        String[] rawTokens = stringToSplit.split(splitOnRegex);
        for (String rawToken : rawTokens) {
            String[] nextTokens = rawToken.split(thenSplitOnRegex);
            for (String nextToken : nextTokens) {
                String[] finalTokens = nextToken.split(finallySplitOnRegex);
                for (String finalToken : finalTokens) {
                    elements.add(finalToken.trim());
                }
            }
        }
        return elements;
    }
}
