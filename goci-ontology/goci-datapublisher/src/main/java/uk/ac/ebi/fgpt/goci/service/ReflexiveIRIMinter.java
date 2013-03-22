package uk.ac.ebi.fgpt.goci.service;

import org.semanticweb.owlapi.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.lang.UniqueID;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;

/**
 * An {@link IRIMinter} implementation that looks for annotations on the supplied model object to identify the unique
 * ID, which is then used to mint a new IRI for that object
 *
 * @author Tony Burdett
 * Date 26/01/12
 */
public class ReflexiveIRIMinter implements IRIMinter<Object> {
    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public IRI mint(String base, Object o) {
        // derive the prefix (which is the object type)
        String prefix = o.getClass().getSimpleName();
        String objectName = inspectObjectForID(o);
        return mint(base, prefix, objectName);
    }

    public IRI mint(String base, String prefix, Object o) {
        String fullPrefix;
        if (prefix == null || prefix.equals("")) {
            fullPrefix = o.getClass().getSimpleName();
        }
        else {
            fullPrefix = o.getClass().getSimpleName().concat("_").concat(prefix);
        }
        String objectName = inspectObjectForID(o);
        return mint(base, fullPrefix, objectName);
    }

    public IRI mint(String base, String objectName) {
        return mint(base, "", objectName);
    }

    public IRI mint(String base, String prefix, String objectName) {
        // derive the full object    name
        String fullName;
        if (prefix == null || prefix.equals("")) {
            fullName = objectName;
        }
        else {
            fullName = prefix.concat("_").concat(objectName);
        }

        // encode as URI and generate IRI
        try {
            String fragment = encodeToURI(fullName);

            String iri;
            if (base.endsWith("/")) {
                iri = base.concat(fragment);
            }
            else {
                iri = base.concat("/").concat(fragment);
            }
            return IRI.create(iri);
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("The unique id of the supplied object ('" + fullName +
                                                       "') cannot be encoded as an IRI");
        }
    }

    public String encodeToURI(String stringToEncode) throws UnsupportedEncodingException {
        String encodedString = URLEncoder.encode(stringToEncode, "UTF-8");
        return encodedString.replaceAll("\\+", "%20");
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
                            methodCount + " methods with a @UniqueID annotation");
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
