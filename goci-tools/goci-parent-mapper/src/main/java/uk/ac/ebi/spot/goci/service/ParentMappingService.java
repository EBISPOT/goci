package uk.ac.ebi.spot.goci.service;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Trait;
import uk.ac.ebi.spot.goci.ontology.owl.ReasonedOntologyLoader;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Dani on 15/02/2016.
 */

@Service
public class ParentMappingService {


    private ReasonedOntologyLoader ontologyLoader;

    @Autowired
    public ParentMappingService(ReasonedOntologyLoader ontologyLoader){
          this.ontologyLoader = ontologyLoader;
    }

    public List<Trait> mapTraits(Map<String, List<Trait>> unmappedTraits) {
//        for(Mapping term : terms){
//            String map = findParent(term.getChildURI());
//            term.setParentURI(map);
//            term.setParentName(ParentList.PARENT_URI.get(map));
//        }


        return null;
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
            }
        }
        return parent;
    }
}
