package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.index.SnpIndex;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.SnpDocument;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 19/01/15
 */
@Service
public class SingleNucleotidePolymorphismMapper
        extends ObjectDocumentMapper<SingleNucleotidePolymorphism, SnpDocument> {
    @Autowired
    public SingleNucleotidePolymorphismMapper(ObjectConverter objectConverter, SnpIndex snpIndex) {
        super(SnpDocument.class, objectConverter, snpIndex);
    }
}
