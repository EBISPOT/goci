package uk.ac.ebi.spot.goci.utils;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.QNameShortFormProvider;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.goci.exception.UnexpectedOntologyStructureException;

import java.net.URI;
import java.util.Set;

/**
 * A convenience class of useful ontology manipulation functions
 *
 * @author Tony Burdett
 * @date 03/08/12
 */
public class OntologyUtils {
    private final static Logger log = LoggerFactory.getLogger(OntologyUtils.class);

    private static Logger getLog() {
        return log;
    }

    public static void loadImports(final OWLOntologyManager manager, OWLOntology ontology)
            throws UnloadableImportException {
        // register a listener to the manager so we can transitively load all imports
        manager.addOntologyLoaderListener(new OWLOntologyLoaderListener() {
            public void startedLoadingOntology(LoadingStartedEvent evt) {
                getLog().debug("Loading '" + evt.getOntologyID().getOntologyIRI() + "'...");
            }

            public void finishedLoadingOntology(LoadingFinishedEvent evt) {
                if (evt.isSuccessful()) {
                    getLog().debug("Ontology '" + evt.getOntologyID().getOntologyIRI() + "' loaded ok");
                    try {
                        recursivelyLoadImports(manager, manager.getOntology(evt.getOntologyID().getOntologyIRI()));
                    }
                    catch (UnloadableImportException e) {
                        getLog().error("Failed to load imports for ");
                    }
                }
                else {
                    getLog().warn("Ontology '" + evt.getOntologyID().getOntologyIRI() + "' " +
                                          "did not load successfully, results may not be valid");
                }
            }
        });

        // and load the direct imports we can get from the current ontology
        for (int i = 0; i < 5; i++) {
            try {
                recursivelyLoadImports(manager, ontology);
                break;
            }
            catch (UnloadableImportException e) {
                // max 5 tries, if this was our last try throw the exception
                if (i < 4) {
                    // unloadable import, we can catch this and retry, might be a connection problem
                    getLog().warn("Loading imported ontology failed (" + e.getMessage() + ") retrying...    \t" +
                                          "[" + (4 - i) + " attempts remaining]");
                }
                else {
                    getLog().error("Failed to load imported ontology (" + e.getMessage() + ").  " +
                                           "Maximum number of retries reached");
                    throw e;
                }
            }
        }
    }

    public static String getShortForm(String entityURI, OWLOntology ontology) {
        return getShortForm(URI.create(entityURI), ontology);
    }

    public static String getShortForm(URI entityURI, OWLOntology ontology) {
        return getShortForm(IRI.create(entityURI), ontology);
    }

    public static String getShortForm(IRI entityIRI, OWLOntology ontology) {
        // preferentially use classes
        Set<OWLClass> clses = ontology.getClassesInSignature();
        for (OWLClass cls : clses) {
            if (cls.getIRI().equals(entityIRI)) {
                return getShortForm(cls);
            }
        }

        // no class with this iri, return first entity (or null)
        Set<OWLEntity> entities = ontology.getEntitiesInSignature(entityIRI);
        if (!entities.isEmpty()) {
            return getShortForm(entities.iterator().next());
        }
        else {
            throw new NullPointerException("No entity with IRI '" + entityIRI + "' could be found");
        }
    }

    public static String getShortForm(OWLEntity entity) {
        QNameShortFormProvider shortener = new QNameShortFormProvider();
        return shortener.getShortForm(entity);
    }

    public static String getClassLabel(OWLOntology ontology, OWLClass cls) {
        OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

        String className = null;
        for (OWLAnnotation annotation : cls.getAnnotations(ontology, label)) {
            if (annotation.getValue() instanceof OWLLiteral) {
                OWLLiteral val = (OWLLiteral) annotation.getValue();
                className = val.getLiteral();
            }
            if (cls.getAnnotations(ontology, label).size() != 1) {
                throw new UnexpectedOntologyStructureException("More than one label for class " + cls);
            }
        }

        if (className != null) {
            return className;
        }
        else {
            throw new UnexpectedOntologyStructureException("There is no label for class " + cls);
        }
    }

    private static void recursivelyLoadImports(OWLOntologyManager manager, OWLOntology ontology)
            throws UnloadableImportException {
        OWLOntologyLoaderConfiguration loadConfig = new OWLOntologyLoaderConfiguration();
        // get each ontology the current ontology imports
        getLog().debug("Collecting imports of '" + ontology.getOntologyID().getOntologyIRI() + "'...");
        for (OWLImportsDeclaration decl : ontology.getImportsDeclarations()) {
            // check if this ontology is already loaded
            IRI importedOntologyIRI = decl.getIRI();
            getLog().debug("Ontology '" + ontology.getOntologyID().getOntologyIRI() + "' " +
                                   "imports '" + importedOntologyIRI + "', " +
                                   "checking whether this ontology is already loaded...");
            if (!manager.contains(importedOntologyIRI)) {
                getLog().debug("Ontology '" + importedOntologyIRI + "' has not been loaded in this session, " +
                                       "making import request...");
                manager.makeLoadImportRequest(decl, loadConfig);
            }
            else {
                getLog().debug("Imported ontology '" + importedOntologyIRI + "' was already loaded");
            }
        }
    }
}
