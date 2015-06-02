package uk.ac.ebi.spot.goci.utils;

import java.lang.annotation.*;

/**
 * An annotation that can be used on model objects to flag getter methods that uniquely identify that object
 *
 * @author Tony Burdett
 * Date 26/01/12
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface UniqueID {
}
