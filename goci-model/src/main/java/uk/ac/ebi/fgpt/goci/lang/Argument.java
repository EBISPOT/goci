package uk.ac.ebi.fgpt.goci.lang;

import uk.ac.ebi.fgpt.goci.model.GWASObject;

/**
 * A stubbing object that allows creation of a filter on a particular value or range of values
 *
 * @author Tony Burdett
 * @date 03/06/14
 */
public class Argument<T extends GWASObject, M> {
    public <V> Filter<T, M, V> hasValue(V value) {
        return new Filter<T, M, V>();
    }

    public <V> Filter<T, M, V> hasValues(V... values) {
        return new Filter<T, M, V>();
    }

    public <V> Filter<T, M, V> hasRange(V from, V to) {
        return new Filter<T, M, V>();
    }
}
