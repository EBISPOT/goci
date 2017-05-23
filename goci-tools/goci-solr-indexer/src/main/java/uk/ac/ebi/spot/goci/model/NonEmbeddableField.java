package uk.ac.ebi.spot.goci.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 16/02/15
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NonEmbeddableField {
}
