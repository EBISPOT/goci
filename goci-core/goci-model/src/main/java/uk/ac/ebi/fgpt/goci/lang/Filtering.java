package uk.ac.ebi.fgpt.goci.lang;

import uk.ac.ebi.fgpt.goci.model.GWASObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * A filter describes a mechanism for constraining GWAS queries to known model objects based on values set for their
 * fields
 *
 * @author Tony Burdett
 * @date 03/06/14
 */
public class Filtering {
    /**
     * Declares a new filter on a type of GWAS object.  This returns a "mock" template instance of the supplied class to
     * use in refinement.
     *
     * @param filterType
     * @param <T>
     * @return
     */
    public static <T extends GWASObject> T template(Class<T> filterType) {
        InvocationHandler handler = new MethodLoggingInvocationHandler();
        return (T) Proxy.newProxyInstance(filterType.getClassLoader(), new Class[]{filterType}, handler);
    }

    public static <T extends GWASObject> CallChain<T> refine(T template) {
        return new CallChain<T>(template);
    }

    public static <T extends GWASObject> Filter<T, ?> filter(T template) {
        return new Filter<T, Object>(template);
    }
}
