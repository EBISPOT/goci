package uk.ac.ebi.spot.goci.kb;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.ontology.OntologyConstants;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Loads a GOCI Knowledgebase and produces a count of number of associations to each trait
 *
 * @author Tony Burdett
 * @date 08/08/12
 */
@Service
@Component
public class KBLoader {
    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }


    public Map<IRI, Integer> quantifyKnowledgeBase(URL efoLocation, URL gwasSchemaLocation, URL kbLocation)
            throws OWLOntologyCreationException, URISyntaxException {
        Map<IRI, Integer> results = new HashMap<IRI, Integer>();

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        // do iri mapping
        URI efoURI = efoLocation.toURI();
        URI gwasSchemaURI = gwasSchemaLocation.toURI();
        URI kbURI = kbLocation.toURI();
        getLog().info("Mapping EFO to " + efoURI);
        manager.addIRIMapper(new SimpleIRIMapper(IRI.create(OntologyConstants.EFO_ONTOLOGY_SCHEMA_IRI),
                                                 IRI.create(efoURI)));
        getLog().info("Mapping GWAS schema to " + gwasSchemaURI);
        manager.addIRIMapper(new SimpleIRIMapper(IRI.create(OntologyConstants.GWAS_ONTOLOGY_SCHEMA_IRI),
                                                 IRI.create(gwasSchemaURI)));


        System.out.println("Loading knowledge base " + kbURI);
        getLog().info("Loading knowledge base " + kbURI);
        // load the knowledgebase
        OWLOntology kb = manager.loadOntology(IRI.create(kbURI));

        System.out.println("Processing knowledge base");
        getLog().info("Processing knowledge base");
        // retrieve all individuals
        Set<OWLNamedIndividual> inds = kb.getIndividualsInSignature();

        System.out.println("The knowledge base contains " + inds.size() + " individuals");
        getLog().info("The knowledge base contains " + inds.size() + " individuals");

        for (OWLNamedIndividual ind : inds) {
            // for each individual, check if it is an association
            boolean isAssociation = false;
            Set<OWLClassExpression> types = ind.getTypes(kb);
            for (OWLClassExpression type : types) {
                if (type.asOWLClass().getIRI().toString().equals(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI)) {
                    isAssociation = true;
                    break;
                }
            }

            if (isAssociation) {
                // get the IRI of the trait class (from EFO) this individual is associated with
                Set<IRI> traitClasses = getTraitClass(kb, ind);

                for (IRI traitClass : traitClasses) {
                    // skip SNPs
                    if (traitClass.toString().equals(OntologyConstants.SNP_CLASS_IRI)) {
                        continue;
                    }

                    // increment count
                    if (results.containsKey(traitClass)) {
                        int count = results.get(traitClass) + 1;
                        results.put(traitClass, count);
                    }
                    else {
                        results.put(traitClass, 1);
                    }
                }
            }
        }
        return results;
    }

    private Set<IRI> getTraitClass(OWLOntology ontology, OWLNamedIndividual individual) {
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        OWLDataFactory dataFactory = manager.getOWLDataFactory();

        Set<IRI> results = new HashSet<IRI>();

        // get all individuals related to this one by "is_about"
        OWLObjectProperty has_object = dataFactory.getOWLObjectProperty(IRI.create(OntologyConstants.HAS_OBJECT_IRI));
        Set<OWLIndividual> relatedInds = individual.getObjectPropertyValues(has_object, ontology);

        // for each related individual, get all types
        for (OWLIndividual related : relatedInds) {
            Set<OWLClassExpression> types = related.getTypes(ontology);
            for (OWLClassExpression type : types) {
                results.add(type.asOWLClass().getIRI());
            }
        }

        return results;
    }
}
