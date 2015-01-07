package uk.ac.ebi.spot.goci.lang;

import uk.ac.ebi.spot.goci.model.GWASObject;

import java.lang.reflect.Method;

/**
 * A stubbing object that allows creation of a filter on a particular value or range of values
 *
 * @author Tony Burdett
 * @date 03/06/14
 */
public class Argument<T extends GWASObject> {
    private T template;
    private Method method;

    public Argument(T template, Method method) {
        this.template = template;
        this.method = method;
    }

    public <V> Filter<T, V> hasValue(V value) {
        return new Filter<T, V>(template, method, value);
    }

    public <V> Filter<T, V> hasValues(V... values) {
        return new Filter<T, V>(template, method, values);
    }

    public <V> Filter<T, V> hasRange(V from, V to) {
        return new Filter<T, V>(template, method, from, to, true);
    }
}
