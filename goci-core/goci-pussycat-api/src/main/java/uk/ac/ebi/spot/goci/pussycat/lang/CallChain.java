package uk.ac.ebi.spot.goci.pussycat.lang;


import org.mockito.MockingDetails;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;

/**
 * A stubbing object that allows creation of a filter on a particular method call
 *
 * @author Tony Burdett
 * @date 03/06/14
 */
public class CallChain<T> {
    private T template;

    public CallChain(T template) {
        this.template = template;
    }

    public <M> Argument<T> on(M methodCall) {
        MockingDetails mockingDetails = Mockito.mockingDetails(template);
        if (mockingDetails.isMock()) {
            Collection<Invocation> invocations = mockingDetails.getInvocations();
            Method method = invocations.stream().reduce((current, next) -> next).get().getMethod();
            return new Argument<T>(template, method);
        }
        else {
            InvocationHandler h = Proxy.getInvocationHandler(template);
            if (h instanceof MethodLogger) {
                Method method = ((MethodLogger) h).getLastInvokedMethod();
                return new Argument<T>(template, method);
            }
            else {
                throw new IllegalArgumentException("Cannot refine the supplied template - " +
                                                           "did you first call Filtering.template()?");
            }
        }
    }
}

