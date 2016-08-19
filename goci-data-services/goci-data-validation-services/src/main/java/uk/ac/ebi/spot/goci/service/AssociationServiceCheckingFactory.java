package uk.ac.ebi.spot.goci.service;

/**
 * Created by emma on 25/04/2016.
 *
 * @author emma
 *         <p>
 *         Abstract factory which creates the requires association checking service
 */
public interface AssociationServiceCheckingFactory {

    AssociationCheckingService create();
}
