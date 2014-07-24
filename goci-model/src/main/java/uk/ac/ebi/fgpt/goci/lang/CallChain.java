package uk.ac.ebi.fgpt.goci.lang;

import uk.ac.ebi.fgpt.goci.model.GWASObject;

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

    public <M> Argument<T> on(M methodCall) {
        InvocationHandler h = Proxy.getInvocationHandler(template);
        if (h instanceof MethodLoggingInvocationHandler) {
            Method method = ((MethodLoggingInvocationHandler) h).getLastInvokedMethod();
            return new Argument<T>(template, method);
        }
        else {
            throw new IllegalArgumentException("Cannot refine the supplied template - " +
                                                       "did you first call Filtering.template()?");
        }
    }
}

