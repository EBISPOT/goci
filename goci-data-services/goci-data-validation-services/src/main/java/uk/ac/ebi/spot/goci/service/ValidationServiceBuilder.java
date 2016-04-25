package uk.ac.ebi.spot.goci.service;

import org.springframework.stereotype.Service;

/**
 * Created by emma on 01/04/2016.
 *
 * @author emma
 *         <p>
 *         Builder that returns the required validation service based on the level set by client input
 */
@Service
public class ValidationServiceBuilder {

    /**
     * Create sheet from file, this is then used to read through each row
     *
     * @param validationLevel Level of required checks. At present we only have one level but there is potential for
     *                        different flavours of checking
     */
    public AssociationCheckingService buildValidator(String validationLevel) {
        AssociationServiceCheckingFactory factory;
        switch (validationLevel) {
            case "full":
                factory = new FullAssociationServiceCheckingFactory();
                break;
            case "author":
                factory = new AuthorAssociationCheckingServiceFactory();
                break;
            default:
                factory = new FullAssociationServiceCheckingFactory();
                break;
        }
        return factory.create();
    }
}
