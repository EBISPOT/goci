package uk.ac.ebi.fgpt.goci.lang;

import uk.ac.ebi.fgpt.goci.model.GWASObject;

import java.util.List;

/**
 * A filter object that can be used to perform queries on the GWAS catalog.  It is possible to interrogate the
 *
 * @author Tony Burdett
 * @date 03/06/14
 */
public class Filter<T extends GWASObject, M, V> {
    public Class<T> getFilteredType() {
        return null;
    }

    public M getFilteredMethod() {
        return null;
    }

    public List<V> getFilteredValues() {
        return null;
    }

    public Range<V> getFilteredRange() {
        return null;
    }

    public class Range<W> {
        public W from() {
            return null;
        }

        public W to() {
            return null;
        }
    }
}
