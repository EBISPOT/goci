package uk.ac.ebi.fgpt.goci.service;

import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.dao.StudyDAO;
import uk.ac.ebi.fgpt.goci.exception.ObjectMappingException;
import uk.ac.ebi.fgpt.goci.exception.OntologyTermException;
import uk.ac.ebi.fgpt.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.fgpt.goci.model.Study;
import uk.ac.ebi.fgpt.goci.model.TraitAssociation;

import java.util.Collection;

/**
 * A default implementation of {@link GWASOWLConverter} that fetches data from the GWAS catalog using a {@link
 * uk.ac.ebi.fgpt.goci.dao.StudyDAO} and converts all obtained {@link Study} objects to OWL.
 *
 * @author Tony Burdett
 * @date 26/01/12
 */
public class DefaultGWASOWLConverter implements GWASOWLConverter {
    public void addStudiesToOntology(Collection<Study> studies, OWLOntology target) {
    }

    public void addSNPsToOntology(Collection<SingleNucleotidePolymorphism> snps, OWLOntology target) {
    }

    public void addAssociationsToOntology(Collection<TraitAssociation> associations, OWLOntology target) {
    }
}
