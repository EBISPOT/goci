package uk.ac.ebi.spot.goci.service;

/**
 * Created by emma on 25/04/2016.
 *
 * @author emma
 *         <p>
 *         Creates the required author association checking service
 */
public class AuthorAssociationCheckingServiceFactory implements AssociationServiceCheckingFactory {
    @Override public AssociationCheckingService create() {
        return new AuthorAssociationCheckingService();
    }
}
