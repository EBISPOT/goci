package uk.ac.ebi.fgpt.goci.lang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 22/07/14
 */
public class MethodLoggingInvocationHandler implements InvocationHandler {
    private Method invokedMethod;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        this.invokedMethod = method;
        return null;
    }

    public Method getLastInvokedMethod() {
        return invokedMethod;
    }
}
