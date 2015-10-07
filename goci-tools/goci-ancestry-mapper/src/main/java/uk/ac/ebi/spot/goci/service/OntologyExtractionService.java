package uk.ac.ebi.spot.goci.service;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.ontology.owl.ReasonedOntologyLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by dwelter on 07/10/15.
 */
@Service
public class OntologyExtractionService {

    final String ancestralURI = "http://www.ebi.ac.uk/ancestro/ancestro_0004";
    final String countryURI = "http://www.ebi.ac.uk/ancestro/ancestro_0003";

    private ReasonedOntologyLoader ontologyLoader;
    private Map<String, String> ancestralGroups;
    private Map<String, String> countries;

    @Autowired
    public OntologyExtractionService(ReasonedOntologyLoader ontologyLoader){
        this.ontologyLoader = ontologyLoader;
        this.ancestralGroups = new HashMap<>();
        this.countries = new HashMap<>();

        preLoadMaps();
    }

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }


    public void preLoadMaps() {

        OWLClass country = ontologyLoader.getFactory().getOWLClass(IRI.create(countryURI));
        OWLClass ancestralGroup = ontologyLoader.getFactory().getOWLClass(IRI.create(ancestralURI));


        Set<OWLClass> allCountries = ontologyLoader.getOWLReasoner().getSubClasses(country, false).getFlattened();
        Set<OWLClass> allGroups = ontologyLoader.getOWLReasoner().getSubClasses(ancestralGroup, false).getFlattened();

         for(OWLClass cls : allCountries){
             String label = ontologyLoader.getLabel(cls.getIRI());

             countries.put(label, cls.getIRI().toString());
         }

        for(OWLClass cls : allGroups){
            String label = ontologyLoader.getLabel(cls.getIRI());

            ancestralGroups.put(label, cls.getIRI().toString());
        }
    }

    public Map<String, String> getAncestralGroups(){
        return ancestralGroups;
    }

    public Map<String, String> getCountries(){
        return countries;
    }

    public String getAncestralGroupURI(String group){
        return ancestralGroups.get(group);
    }

    public String getCountryURI(String country){
        return countries.get(country);
    }
}
