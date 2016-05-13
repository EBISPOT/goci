package uk.ac.ebi.spot.goci.curation.service.tracking;

import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Trackable;

/**
 * Created by emma on 13/05/2016.
 *
 * @author emma
 *         <p>
 *         A component that manages operations on objects that require event tracking
 */
public interface TrackingOperationService {

    void create(Trackable trackable, SecureUser secureUser);

    void delete(Trackable trackable, SecureUser secureUser);

    void update(Trackable trackable, SecureUser secureUser);
}
