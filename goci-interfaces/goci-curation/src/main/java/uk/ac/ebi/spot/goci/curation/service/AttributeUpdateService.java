package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.stereotype.Service;

/**
 * Created by emma on 03/08/2016.
 *
 * @author emma
 *         <p>
 *         Service that compares to fields to see if a change has been made by a user
 */
@Service
public class AttributeUpdateService {

    /**
     * Compare attribute values to determine type of update made
     *
     * @param attribute     Attribute whose value has been changed
     * @param existingValue
     * @param newValue
     */
    public String compareAttribute(String attribute, String existingValue, String newValue) {

        String updateDescription = null;

        if (existingValue != null && newValue != null) {
            if (!existingValue.equals(newValue)) {
                updateDescription =
                        attribute.concat(" updated from ").concat(existingValue).concat(" to ").concat(newValue);
            }
        }
        else {

            if (existingValue == null) {
                updateDescription =
                        attribute.concat(" set to ").concat(newValue);
            }
            else {
                updateDescription =
                        attribute.concat(" set to ").concat("null");
            }

        }
        return updateDescription;
    }
}