package uk.ac.ebi.spot.goci.repository;

import uk.ac.ebi.spot.goci.model.Study;

/**
 * Created by Dani on 27/11/2014.
 */
public interface StudyRepository {
    Study findByPubmedId(String pubmedId);

    Object findAll();

    Object findOne(long l);

    Study save(Study study);
}
