package uk.ac.ebi.fgpt.goci.service;

import org.semanticweb.owlapi.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.lang.UniqueID;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * An {@link IRIMinter} implementation that looks for annotations on the supplied model object to identify the unique
 * ID, which is then used to mint a new IRI for that object
 *
 * @author Tony Burdett
 * @date 26/01/12
 */
public class ReflexiveIRIMinter implements IRIMinter<Object> {
    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public IRI mint(String base, Object o) {
        String iri;
        String fragment = inspectObjectForID(o);

        if (base.endsWith("/")) {
            iri = base.concat(o.getClass().getSimpleName())
                    .concat("_").concat(fragment);
        }
        else {
            iri = base.concat("/").concat(o.getClass().getSimpleName())
                    .concat("_").concat(fragment);
        }
        return IRI.create(iri);
    }

    public IRI mint(String base, String prefix, Object o) {
        String iri;
        String fragment = inspectObjectForID(o);

        if (base.endsWith("/")) {
            iri = base.concat(o.getClass().getSimpleName())
                    .concat("_").concat(prefix)
                    .concat("_").concat(fragment);
        }
        else {
            iri = base.concat("/").concat(o.getClass().getSimpleName())
                    .concat("_").concat(prefix)
                    .concat("_").concat(fragment);
        }
        return IRI.create(iri);
    }

    private String inspectObjectForID(Object o) {
        // get methods on o
        Method[] methods = o.getClass().getDeclaredMethods();

        // check methods for annotations
        Method annotatedMethod = null;
        int methodCount = 0;
        for (Method method : methods) {
            if (method.isAnnotationPresent(UniqueID.class)) {
                annotatedMethod = method;
                methodCount++;
            }
            else {
                // check all definitions of this method on superclasses too
            }
        }

        // verify that there is only one unique ID method
        if (methodCount == 0 || methodCount > 1) {
            throw new IllegalArgumentException(
                    "Provided " + o.getClass().getSimpleName() + " contains " +
                            annotatedMethod + " methods with an @UniqueID annotation");
        }
        else {
            // invoke the method and return the result .toString()
            try {
                annotatedMethod.setAccessible(true);
                Object result = annotatedMethod.invoke(o);
                if (result != null) {
                    if (result instanceof String) {
                        return (String) result;
                    }
                    else {
                        getLog().debug(
                                "Unique ID for supplied " + o.getClass().getSimpleName() +
                                        " was not a string getter: " +
                                        "the unique ID will be converted using toString(), " +
                                        "but you should check this returns sensible IDs");
                        return result.toString();
                    }
                }
                else {
                    throw new NullPointerException(
                            "Null ID for the supplied " + o.getClass().getSimpleName() + " object");
                }
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException(e.getCause());
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("This should never happen", e);
            }
        }
    }
}
