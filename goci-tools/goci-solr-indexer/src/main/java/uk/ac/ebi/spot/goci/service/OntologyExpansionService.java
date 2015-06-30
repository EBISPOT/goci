package uk.ac.ebi.spot.goci.service;

import org.semanticweb.owlapi.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.OntologyEnabledDocument;
import uk.ac.ebi.spot.goci.ontology.owl.OntologyLoader;

import java.net.URI;

/**
 * A service that is capable of enriching {@link uk.ac.ebi.spot.goci.model.DiseaseTraitDocument}s with information obtained
 * from a supplied {@link uk.ac.ebi.spot.goci.ontology.owl.OntologyLoader}
 *
 * @author Tony Burdett
 * @date 19/01/15
 */
@Service
public class OntologyExpansionService implements DocumentEnrichmentService<OntologyEnabledDocument<?>> {
    private OntologyLoader ontologyLoader;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public OntologyExpansionService(OntologyLoader ontologyLoader) {
        this.ontologyLoader = ontologyLoader;
    }

    @Override public int getPriority() {
        return 4;
    }

    @Override public void doEnrichment(OntologyEnabledDocument<?> document) {
        // improve trait document with parent and child terms etc here
        for (String traitUriString : document.getTraitUris()) {
            URI traitURI = URI.create(traitUriString);

            // get additional fields for trait documents
            IRI traitIRI = IRI.create(traitURI);
            if (ontologyLoader.getAccession(traitIRI) != null) {
                document.addShortForm(ontologyLoader.getAccession(traitIRI));
                document.addLabel(ontologyLoader.getLabel(traitIRI));

                String efolink = ontologyLoader.getLabel(traitIRI)
                        .concat("|").concat(ontologyLoader.getAccession(traitIRI))
                        .concat("|").concat(traitIRI.toString());
                document.addEfoLink(efolink);
                ontologyLoader.getParentLabels(traitIRI).forEach(document::addSuperclassLabel);
                ontologyLoader.getSynonyms(traitIRI).forEach(document::addSynonym);
            }
            else {
                getLog().warn("No EFO term for " + traitURI + " could be found - this term will not be mapped");
            }
        }
    }
}
