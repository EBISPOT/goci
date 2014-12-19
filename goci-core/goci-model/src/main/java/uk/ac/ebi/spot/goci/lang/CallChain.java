package uk.ac.ebi.spot.goci.lang;

import uk.ac.ebi.spot.goci.ui.model.GWASObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * A stubbing object that allows creation of a filter on a particular method call
 *
 * @author Tony Burdett
 * @date 03/06/14
 */
public class CallChain<T extends GWASObject> {
    private T template;

    public CallChain(T template) {
        this.template = template;
    }

    public <M> uk.ac.ebi.spot.goci.lang.Argument<T> on(M methodCall) {
        InvocationHandler h = Proxy.getInvocationHandler(template);
        if (h instanceof uk.ac.ebi.spot.goci.lang.MethodLoggingInvocationHandler) {
            Method method = ((uk.ac.ebi.spot.goci.lang.MethodLoggingInvocationHandler) h).getLastInvokedMethod();
            return new uk.ac.ebi.spot.goci.lang.Argument<T>(template, method);
        }
        else {
            throw new IllegalArgumentException("Cannot refine the supplied template - " +
                                                       "did you first call Filtering.template()?");
        }
    }
}

