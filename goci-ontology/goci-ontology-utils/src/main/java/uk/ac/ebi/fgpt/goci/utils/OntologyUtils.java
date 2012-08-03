package uk.ac.ebi.fgpt.goci.utils;

import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
