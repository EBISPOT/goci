package uk.ac.ebi.spot.goci.service;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.ParentList;
import uk.ac.ebi.spot.goci.model.TraitEntity;
import uk.ac.ebi.spot.goci.ontology.owl.ReasonedOntologyLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Dani on 15/02/2016.
 */

@Service
public class ParentMappingService {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    private ReasonedOntologyLoader ontologyLoader;

    @Autowired
    public ParentMappingService(@Qualifier("parentTermOntologyLoader") ReasonedOntologyLoader ontologyLoader){
          this.ontologyLoader = ontologyLoader;

    }

    public List<TraitEntity> mapTraits(Map<String, List<TraitEntity>> unmappedTraits) {
        List<TraitEntity> mappedTraits = new ArrayList<>();

        if(ontologyLoader.isReady()){
            getLog().debug("Mapping " + unmappedTraits.keySet().size() + " traits");
            for(String uri : unmappedTraits.keySet()){
                if(existsInPublicEFO(uri)){
                    getLog().warn("Acquiring mapping for trait " + uri);
                    String map = findParent(uri);
                    for(TraitEntity trait : unmappedTraits.get(uri)) {
                        trait.setParentUri(map);
                        trait.setParentName(ParentList.PARENT_URI.get(map));
                        mappedTraits.add(trait);
                    }
                }
                else {
                   getLog().warn(uri + " is not yet available in public EFO");
                }

            }

        }
        else {
            getLog().debug("OntologyLoader not ready yet");
            try {
                ontologyLoader.waitUntilReady();
                getLog().debug("Now the OntologyLoader is ready");
                mappedTraits = mapTraits(unmappedTraits);

            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return mappedTraits;
    }

    public boolean existsInPublicEFO(String uri) {
        boolean exists = true;

        if(ontologyLoader.getLabel(IRI.create(uri)) == null){
            exists = false;
        }

        return exists;
    }


    public String findParent(String term){
        String parent= null;

        OWLClass cls = ontologyLoader.getFactory().getOWLClass(IRI.create(term));

        Set<OWLClass> parents = ontologyLoader.getOWLReasoner().getSuperClasses(cls, false).getFlattened();
        Set<String> available = ParentList.PARENT_URI.keySet();

        OWLClass leaf = null;
        int largest = 0;

        if(parents.size() == 2){
            System.out.println("Trait " + term + " is not mapped");
            getLog().debug("Trait " + term + " is not mapped");

        }
        else{
            for (OWLClass t : parents) {
                String iri = t.getIRI().toString();
                int allp = ontologyLoader.getOWLReasoner().getSuperClasses(t, false).getFlattened().size();

                if (allp > largest && available.contains(iri)) {
                    largest = allp;
                    leaf = t;
                }
            }
            if (leaf != null) {
                parent = leaf.getIRI().toString();
            }
            else {
                System.out.println("Could not identify a suitable  parent category for trait " + term);
                getLog().debug("Could not identify a suitable  parent category for trait " + term);
            }
        }
        return parent;
    }
}
