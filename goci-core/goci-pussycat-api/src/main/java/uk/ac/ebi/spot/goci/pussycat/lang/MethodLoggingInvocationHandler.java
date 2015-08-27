package uk.ac.ebi.spot.goci.pussycat.lang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 22/07/14
 */
public class MethodLoggingInvocationHandler implements MethodLogger, InvocationHandler {
    private boolean captured = false;
    private Method invokedMethod;

    private final Map<Class<?>, Object> primitiveDefaults;

    public MethodLoggingInvocationHandler() {
        this.primitiveDefaults = new HashMap<Class<?>, Object>();
//        byte	0
//        short	0
//        int	0
//        long	0L
//        float	0.0f
//        double	0.0d
//        char	'\u0000'
//        String (or any object)  	null
//        boolean	false
        primitiveDefaults.put(byte.class, 0);
        primitiveDefaults.put(short.class, 0);
        primitiveDefaults.put(int.class, 0);
        primitiveDefaults.put(long.class, 0L);
        primitiveDefaults.put(float.class, 0.0f);
        primitiveDefaults.put(double.class, 0.0d);
        primitiveDefaults.put(char.class, '\u0000');
        primitiveDefaults.put(boolean.class, false);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!captured) {
            this.invokedMethod = method;
            captured = true;
        }
        if (method.getReturnType().isPrimitive()) {
            return primitiveDefaults.get(method.getReturnType());
        }
        else {
            return method.getReturnType().newInstance();
        }
    }

    public Method getLastInvokedMethod() {
        return invokedMethod;
    }
}
