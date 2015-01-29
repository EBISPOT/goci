package db.migration;

import java.util.HashSet;
import java.util.Set;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 29/01/15
 */
public abstract class CommaSeparatedFieldSplitter {
    protected Set<String> split(String commaSeparatedString) {
        Set<String> elements = new HashSet<>();
        String[] rawTokens = commaSeparatedString.split(",");
        for (String rawToken : rawTokens) {
            elements.add(rawToken.trim());
        }
        return elements;
    }
}
