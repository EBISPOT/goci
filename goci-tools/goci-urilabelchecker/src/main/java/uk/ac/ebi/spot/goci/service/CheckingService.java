package uk.ac.ebi.spot.goci.service;

import org.semanticweb.owlapi.model.OWLClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.ontology.owl.OntologyLoader;
import uk.ac.ebi.spot.goci.pussycat.service.OntologyService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
* Created with IntelliJ IDEA.
* User: dwelter
* Date: 22/04/13
* Time: 14:22
* To change this template use File | Settings | File Templates.
*/

@Service
public class CheckingService {

    private OntologyLoader ontologyLoader;
    private TraitService traitService;
    private OntologyService ontologyService;

    private Logger log = LoggerFactory.getLogger("checking");

    private Logger getLog() {
        return log;
    }


    @Autowired
    public CheckingService(OntologyLoader ontologyLoader, TraitService traitService, OntologyService ontologyService){
        this.ontologyLoader = ontologyLoader;
        this.traitService = traitService;
        this.ontologyService = ontologyService;
    }


    public void checkURIs(){
        System.out.println("Loading data from GWAS database");

        List<EfoTrait> allEfoTraits = traitService.findAllEfoTraits();


        System.out.println("Data loading complete");


        for(EfoTrait efoTrait : allEfoTraits){
            validateEfoTrait(efoTrait);
        }

    }




    public void validateEfoTrait(EfoTrait trait){
        String uri = trait.getUri();
        String traitLabel = trait.getTrait();
        OWLClass cls = null;

        try{
            cls = ontologyService.getOWLClassByURI(uri);
        }
        catch (Exception e){
            getLog().debug("IRI " + uri + " is not a valid IRI");
        }

        if(cls != null){
            String label = ontologyLoader.getLabel(cls.getIRI());

            boolean found = false;

            if(label.equalsIgnoreCase(traitLabel)){
                found = true;
            }

            if(!found){
                Set<String> syns = ontologyLoader.getSynonyms(cls.getIRI());

                for(String syn : syns){
                    if(syn.equalsIgnoreCase(traitLabel)){
                        found = true;
                    }
                }
                if(!found){
                    getLog().info("Class " + uri + " does not have a label or synonym of " + traitLabel + ". DB ID is " + trait.getId());
                    getLog().info("Label for class " + uri + " is " + label);
                }
            }
        }
        else{
            getLog().info(uri + " is not a valid EFO URI");

            Collection<OWLClass> classes = ontologyService.getOWLClassesByLabel(traitLabel);

            if(classes.isEmpty()){
                getLog().info("No EFO classes match the label " + traitLabel);
            }
        }
    }
}
