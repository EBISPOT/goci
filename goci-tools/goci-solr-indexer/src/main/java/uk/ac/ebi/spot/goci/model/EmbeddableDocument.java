package uk.ac.ebi.spot.goci.model;

import uk.ac.ebi.spot.goci.exception.DocumentEmbeddingException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 13/02/15
 */
public abstract class EmbeddableDocument<O> extends Document<O> {
    public EmbeddableDocument(O object) {
        super(object);
    }

    public void embed(Document document) {
        try {
            BeanInfo docInfo = Introspector.getBeanInfo(document.getClass());
            for (PropertyDescriptor descriptor : docInfo.getPropertyDescriptors()) {
                if (!descriptor.getName().equals("id")) {
                    try {
                        // determine method to update this document with doc being embedded
                        Method propertyAdderMethod = findPropertyAdder(descriptor);
                        // invoke read method on passed document
                        Object fieldToEmbed = descriptor.getReadMethod().invoke(document);
                        propertyAdderMethod.invoke(this, fieldToEmbed);
                    }
                    catch (InvocationTargetException | IllegalAccessException e) {
                        throw new DocumentEmbeddingException("Failed to read property '" + descriptor.getName() + "'",
                                                             e);
                    }
                }
            }
        }
        catch (IntrospectionException e) {
            throw new DocumentEmbeddingException("Failed to analyse document in preparation for embedding", e);
        }
    }

    protected Method findPropertyAdder(PropertyDescriptor propertyDescriptor) throws DocumentEmbeddingException {
        String propertyName = propertyDescriptor.getName();
        Class<?> propertyType = propertyDescriptor.getPropertyType();

        String adderTarget = "add".concat(propertyName.substring(0, 1).toUpperCase()).concat(propertyName.substring(1));

        Method[] methods = getClass().getMethods();
        List<Method> matchedMethods = new ArrayList<>();
        for (Method m : methods) {
            if (m.getName().equals(adderTarget)) {
                Class<?>[] parameterTypes = m.getParameterTypes();
                if (parameterTypes.length == 1) {
                    if (parameterTypes[0].isAssignableFrom(propertyType)) {
                        matchedMethods.add(m);
                    }
                    else {
                        throw new DocumentEmbeddingException(
                                "Incompatible method '" + m.getName() + "': wrong argument type.  " +
                                        "Expected " + propertyType.getName() + ", " +
                                        "found " + parameterTypes[0].getClass().getName());
                    }
                }
                else {
                    throw new DocumentEmbeddingException(
                            "Incompatible method '" + m.getName() + "': too many arguments.  " +
                                    "Expected 1 (" + propertyType.getName() + "), " +
                                    "found " + parameterTypes.length + " (" + Arrays.toString(parameterTypes) + ")");
                }
            }
        }

        if (matchedMethods.size() > 1) {
            throw new DocumentEmbeddingException(
                    "Ambiguous property type: found " + matchedMethods.size() + " " +
                            "methods named '" + adderTarget + "' with matching argument types");
        }

        if (matchedMethods.size() == 0) {
            throw new DocumentEmbeddingException(
                    "Failed to identify appropriate method for embedding: " + this.getClass().getName() + " " +
                            "has no method to embed property '" + propertyDescriptor.getName() + "' " +
                            "(missing method " + adderTarget + ")");
        }

        return matchedMethods.iterator().next();
    }
}
