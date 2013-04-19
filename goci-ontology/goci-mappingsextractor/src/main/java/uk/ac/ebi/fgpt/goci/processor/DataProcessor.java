package uk.ac.ebi.fgpt.goci.processor;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.dao.OntologyDAO;
import uk.ac.ebi.fgpt.goci.dataloader.DataLoader;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.lang.ParentList;

import java.util.Collection;
import java.util.Set;


/**
  * Created with IntelliJ IDEA.
  * User: dwelter
  * Date: 17/04/13
  * Time: 16:11
  * To change this template use File | Settings | File Templates.
  */
 public class DataProcessor {
    private DataLoader dataLoader;
    private OntologyConfiguration config;
    private OntologyDAO ontologyDAO;
    private OWLReasoner reasoner;

    public void setDataLoader(DataLoader dataLoader) {
             this.dataLoader = dataLoader;
         }

    public DataLoader getDataLoader() {
        return dataLoader;
    }

    public void setConfig(OntologyConfiguration config) {
        this.config = config;
    }

    public OntologyConfiguration getConfig() {
        return config;
    }

    public void setOntologyDAO(OntologyDAO ontologyDAO) {
             this.ontologyDAO = ontologyDAO;
         }

    public OntologyDAO getOntologyDAO() {
             return ontologyDAO;
         }

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
             return log;
         }

    public OWLOntologyManager getManager(){
        return config.getOWLOntologyManager();
    }

    public OWLDataFactory getDataFactory(){
        return config.getOWLDataFactory();

    }


    public Collection<Mapping> processData(){
        Collection<Mapping> allMappings = dataLoader.retrieveAllMappings();

        OWLOntology efo = getOntologyDAO().getOntology();

        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
        OWLReasonerConfiguration con = new SimpleConfiguration(progressMonitor);
        reasoner = reasonerFactory.createReasoner(efo, con);

        for(Mapping mapping : allMappings){
            findParent(mapping);
        }

        return allMappings;
    }


    public void findParent(Mapping mapping){
        String parent= null;

        OWLClass cls =  getDataFactory().getOWLClass(IRI.create(mapping.getEfouri()));

        Set<OWLClass> parents = reasoner.getSuperClasses(cls, false).getFlattened();
        Set<String> available = ParentList.PARENT_URI.keySet();

        OWLClass leaf = null;
        int largest = 0;

        if(parents.size() == 2){
            System.out.println("Trait " + mapping.getEfotrait() + " is not mapped");
        }
        else{
            for (OWLClass t : parents) {
                String iri = t.getIRI().toString();
                int allp = reasoner.getSuperClasses(t, false).getFlattened().size();

                if (allp > largest && available.contains(iri)) {
                    largest = allp;
                    leaf = t;
                }
            }
            if (leaf != null) {
                parent = leaf.getIRI().toString();
            }
            else {
                System.out.println("Could not identify a suitable  parent category for trait " + mapping.getEfotrait());
            }
        }
        mapping.setParent(ParentList.PARENT_URI.get(parent));
    }
}
