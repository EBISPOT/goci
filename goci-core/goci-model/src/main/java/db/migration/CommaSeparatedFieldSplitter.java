package db.migration;

import java.util.ArrayList;
import java.util.List;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 29/01/15
 */
public abstract class CommaSeparatedFieldSplitter {
    protected List<String> split(String commaSeparatedString) {
        List<String> elements = new ArrayList<>();
        String[] rawTokens = commaSeparatedString.split(",");
        for (String rawToken : rawTokens) {
            elements.add(rawToken.trim());
        }
        return elements;
    }
}
