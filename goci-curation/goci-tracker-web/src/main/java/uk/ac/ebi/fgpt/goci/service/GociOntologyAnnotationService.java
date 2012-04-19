package uk.ac.ebi.fgpt.goci.service;

import uk.ac.ebi.fgpt.goci.model.GociStudy;

/**
 * A service that takes the abstracts of papers entered into the GOCI tracking system and annotates the abstracts
 * against a supplied ontology.
 *
 * @author Tony Burdett
 * Date 26/10/11
 */
public interface GociOntologyAnnotationService {
    /**
     * Placeholder annotation method for annotating paper abstracts against an ontology. TODO.
     *
     * @param study        the study to annotate
     * @param abstractText the text of the paper abstract
     */
    void annotateAbstract(GociStudy study, String abstractText);
}
