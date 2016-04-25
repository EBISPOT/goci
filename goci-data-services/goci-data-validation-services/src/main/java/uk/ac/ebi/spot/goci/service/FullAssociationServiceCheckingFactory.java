package uk.ac.ebi.spot.goci.service;

/**
 * Created by emma on 25/04/2016.
 *
 * @author emma
 *         <p>
 *         Creates the required full association checking service
 */
public class FullAssociationServiceCheckingFactory implements AssociationServiceCheckingFactory {
    @Override public AssociationCheckingService create() {
        return new FullAssociationCheckingService();
    }
}
