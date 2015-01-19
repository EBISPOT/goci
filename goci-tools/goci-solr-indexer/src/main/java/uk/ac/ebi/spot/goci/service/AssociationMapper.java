package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.index.AssociationIndex;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationDocument;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 19/01/15
 */
@Service
public class AssociationMapper extends ObjectDocumentMapper<Association, AssociationDocument> {
    @Autowired
    public AssociationMapper(AssociationIndex associationIndex) {
        super(AssociationDocument.class, associationIndex);
    }
}
