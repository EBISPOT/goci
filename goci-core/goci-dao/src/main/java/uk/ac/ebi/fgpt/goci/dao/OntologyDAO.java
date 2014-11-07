package uk.ac.ebi.fgpt.goci.dao;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.core.io.Resource;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by dwelter on 06/11/14.
 */
public interface OntologyDAO {

    Resource getOntologyResource();

    String getOntologyURI();

    String getOntologySynonymAnnotationURI();

    OWLOntology getOntology();

    OntologyConfiguration getOntologyConfiguration();

    String getOntologyObsoleteClassURI();

    Collection<OWLClass> getOWLClassesByLabel(String label);

    OWLClass getOWLClassByURI(String str);

    OWLClass getOWLClassByURI(URI uri);

    OWLClass getOWLClassByIRI(IRI iri);

    Set<String> getClassRDFSLabels(OWLClass owlClass);

    Set<String> getClassSynonyms(OWLClass owlClass);

//    String normalizeSearchString(String string);
//
//    Set<OWLClass> matchSearchString(String searchString);
//
//    List<String> getClassNames(OWLOntology owlOntology, OWLClass owlClass);
//
//    Set<String> getClassRDFSLabels(OWLOntology owlOntology, OWLClass owlClass);
//
//    Set<String> getClassSynonyms(OWLOntology owlOntology, OWLClass owlClass);
//
//    boolean isObsolete(OWLOntology owlOntology, OWLClass obsoleteClass, OWLClass owlClass);
}
