package uk.ac.ebi.spot.goci.model;

import java.lang.reflect.InvocationTargetException;

/**
 * Generic wrapper class to convert any model objects into the corresponding Document object types.  The document type
 * parameter must be a class that contains a constructor that takes the model object as it's only parameter.
 *
 * @author Tony Burdett
 * @date 14/01/15
 */
public class ObjectConverter<O, D> {
    private Class<D> documentType;

    public ObjectConverter(Class<D> documentType) {
        this.documentType = documentType;
    }

    public D convert(O object) {
        try {
            Class<?> paramType = documentType.getDeclaredConstructors()[0].getParameterTypes()[0];
            if (paramType.isAssignableFrom(object.getClass())) {
                return documentType.getDeclaredConstructor(object.getClass()).newInstance(object);
            }
            else {
                throw new RuntimeException(
                        "Object of type '" + object.getClass().getName() + "' is of wrong type to convert " +
                                "to document type '" + documentType.getName() + "'");
            }
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}