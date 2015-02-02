package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.index.TraitIndex;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.TraitDocument;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 19/01/15
 */
@Service
public class TraitMapper extends ObjectDocumentMapper<DiseaseTrait, TraitDocument> {
    @Autowired
    public TraitMapper(ObjectConverter objectConverter, TraitIndex traitIndex) {
        super(TraitDocument.class, objectConverter, traitIndex);
    }
}
