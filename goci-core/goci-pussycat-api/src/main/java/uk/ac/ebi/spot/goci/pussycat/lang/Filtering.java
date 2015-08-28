package uk.ac.ebi.spot.goci.pussycat.lang;

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
    public static <T> T template(Class<T> filterType) {
//        if(filterType.isInterface()){
            InvocationHandler handler = new MethodLoggingInvocationHandler();
            return (T) Proxy.newProxyInstance(filterType.getClassLoader(), new Class[]{filterType}, handler);
//        }
//        else{
//            try {
//                return filterType.newInstance();
//            } catch (InstantiationException e) {
//                throw new RuntimeException(e);
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//        }

    }

    public static <T> CallChain<T> refine(T template) {
        return new CallChain<T>(template);
    }

    public static <T> Filter<T, ?> filter(T template) {
        return new Filter<T, Object>(template);
    }
}
