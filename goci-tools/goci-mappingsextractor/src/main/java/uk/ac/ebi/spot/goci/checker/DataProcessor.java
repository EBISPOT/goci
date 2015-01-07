package uk.ac.ebi.spot.goci.checker;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import uk.ac.ebi.spot.goci.dao.DefaultOntologyDAO;
import uk.ac.ebi.spot.goci.dataloader.DataLoader;
import uk.ac.ebi.spot.goci.lang.OntologyConfiguration;
import uk.ac.ebi.spot.goci.lang.ParentList;

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
    private DefaultOntologyDAO ontologyDAO;
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

    public void setOntologyDAO(DefaultOntologyDAO ontologyDAO) {
             this.ontologyDAO = ontologyDAO;
         }

    public DefaultOntologyDAO getOntologyDAO() {
             return ontologyDAO;
         }


    public OWLOntologyManager getManager(){
        return config.getOWLOntologyManager();
    }

    public OWLDataFactory getDataFactory(){
        return config.getOWLDataFactory();

    }


    public Collection<Mapping> processData(){
        System.out.println("Loading data from GWAS database");
        Collection<Mapping> allMappings = dataLoader.retrieveAllMappings();

        System.out.println("Data loading complete");

        OWLOntology efo = getOntologyDAO().getOntology();

        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
        OWLReasonerConfiguration con = new SimpleConfiguration(progressMonitor);
        reasoner = reasonerFactory.createReasoner(efo, con);

        System.out.println("Mapping parent classes");
        for(Mapping mapping : allMappings){
            findParent(mapping);
        }

        System.out.println("Parent mapping complete");
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
                System.out.println("Could not identify a suitable parent category for trait " + mapping.getEfotrait());
            }
        }
        mapping.setParent(ParentList.PARENT_URI.get(parent));
    }
}
