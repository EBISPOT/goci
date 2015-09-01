package uk.ac.ebi.spot.goci.pussycat.lang;

import org.mockito.MockingDetails;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

/**
 * A filter object that can be used to perform queries on the GWAS catalog.  It is possible to interrogate the
 *
 * @author Tony Burdett
 * @date 03/06/14
 */
public class Filter<T, V> {
    private Class<?> filteredType;
    private Method filteredMethod;
    private List<V> filteredValues;
    private Range<V> filteredRange;

    public Filter(T template) {
        this.filteredType = inferFilteredType(template);
    }

    public Filter(T template, Method filteredMethod, V... filteredValues) {
        this.filteredType = inferFilteredType(template);
        this.filteredMethod = filteredMethod;
        this.filteredValues = Arrays.asList(filteredValues);
    }

    public Filter(T template, Method filteredMethod, V rangeFrom, V rangeTo, boolean isRange) {
        if (isRange) {
            this.filteredType = inferFilteredType(template);
            this.filteredMethod = filteredMethod;
            this.filteredRange = new Range<V>(rangeFrom, rangeTo);
        }
        else {
            this.filteredType = inferFilteredType(template);
            this.filteredMethod = filteredMethod;
            this.filteredValues = Arrays.asList(rangeFrom, rangeTo);
        }
    }

    public Class<?> getFilteredType() {
        return filteredType;
    }

    public Method getFilteredMethod() {
        return filteredMethod;
    }

    public List<V> getFilteredValues() {
        return filteredValues;
    }

    public Range<V> getFilteredRange() {
        return filteredRange;
    }

    private Class<?> inferFilteredType(T template) {
        if (Proxy.isProxyClass(template.getClass())) {
            Class<?>[] interfaces = template.getClass().getInterfaces();
            if (interfaces.length == 1) {
                Class<?> i = interfaces[0];
                return (Class<?>) i;
            }
            else {
                throw new IllegalArgumentException("Not a single filter type");
            }
        }
        else {
            MockingDetails mockingDetails = Mockito.mockingDetails(template);
            if (mockingDetails.isMock()) {
                // return the superclass of the mock -
                // note that this *ONLY* works because we only use Mockito when templating concrete classes
                // if we were templating an interface or classes with many parents this would fail
                return template.getClass().getSuperclass();
            }
            else {
                throw new IllegalArgumentException("Cannot workout the supplied template template - " +
                                                           "did you first call Filtering.template()?");
            }
        }
    }

    public class Range<W> {
        private W fromValue;
        private W toValue;

        public Range(W fromValue, W toValue) {
            this.fromValue = fromValue;
            this.toValue = toValue;
        }

        public W from() {
//            return null;
            return fromValue;
        }

        public W to() {
//            return null;
            return toValue;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Filter filter = (Filter) o;

        if (filteredMethod != null ? !filteredMethod.equals(filter.filteredMethod) : filter.filteredMethod != null)
            return false;
        if (filteredRange != null ? !filteredRange.equals(filter.filteredRange) : filter.filteredRange != null)
            return false;
        if (!filteredType.equals(filter.filteredType)) return false;
        if (filteredValues != null ? !filteredValues.equals(filter.filteredValues) : filter.filteredValues != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = filteredType.hashCode();
        result = 31 * result + (filteredMethod != null ? filteredMethod.hashCode() : 0);
        result = 31 * result + (filteredValues != null ? filteredValues.hashCode() : 0);
        result = 31 * result + (filteredRange != null ? filteredRange.hashCode() : 0);
        return result;
    }
}
