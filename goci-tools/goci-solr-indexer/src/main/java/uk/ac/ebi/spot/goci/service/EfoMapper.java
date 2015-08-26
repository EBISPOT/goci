package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.index.EfoIndex;
import uk.ac.ebi.spot.goci.model.EfoDocument;
import uk.ac.ebi.spot.goci.model.EfoTrait;

/**
 * Created by dwelter on 24/08/15.
 */
@Service
public class EfoMapper extends ObjectDocumentMapper<EfoTrait, EfoDocument> {
    @Autowired
    public EfoMapper(ObjectConverter objectConverter, EfoIndex efoIndex) {
        super(EfoDocument.class, objectConverter, efoIndex);
    }
}
