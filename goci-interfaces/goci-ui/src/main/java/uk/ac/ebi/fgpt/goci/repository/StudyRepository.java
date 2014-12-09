package uk.ac.ebi.fgpt.goci.repository;

import uk.ac.ebi.fgpt.goci.model.Study;

/**
 * Created by Dani on 27/11/2014.
 */
public interface StudyRepository {
    Study findByPubmedId(String pubmedId);
}
