package uk.ac.ebi.fgpt.goci.checker;

import org.semanticweb.owlapi.model.OWLClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.dao.DefaultOntologyDAO;
import uk.ac.ebi.fgpt.goci.dataloader.DBEntry;
import uk.ac.ebi.fgpt.goci.dataloader.DBLoader;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;

import java.util.Collection;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 22/04/13
 * Time: 14:22
 * To change this template use File | Settings | File Templates.
 */
public class Checker {
    private DBLoader dbLoader;
    private OntologyConfiguration config;
    private DefaultOntologyDAO ontologyDAO;

    public void setDbLoader(DBLoader dbLoader) {
        this.dbLoader = dbLoader;
    }

    public DBLoader getDbLoader() {
        return dbLoader;
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

    public void checkURIs(){
        System.out.println("Loading data from GWAS database");
        Collection<DBEntry> dbdata = getDbLoader().retrieveAllEntries();

        System.out.println("Data loading complete");


        for(DBEntry entry : dbdata){
            validateEntry(entry);
        }

    }

    private Logger log = LoggerFactory.getLogger(getClass());

    private Logger getLog() {
        return log;
    }


    public void validateEntry(DBEntry entry){
        String uri = entry.getEfouri();
        String efoTrait = entry.getEfotrait();
        OWLClass cls = null;

        try{
            cls = getOntologyDAO().getOWLClassByURI(uri);
        }
        catch (Exception e){
            getLog().debug("IRI " + uri + " is not a valid IRI");
        }

        if(cls != null){
            Set<String> labels = getOntologyDAO().getClassRDFSLabels(cls);


            boolean found = false;

            for(String label : labels){
                if(label.equalsIgnoreCase(efoTrait)){
                    found = true;
                }
            }
            if(!found){
                Set<String> syns = getOntologyDAO().getClassSynonyms(cls);

                for(String syn : syns){
                    if(syn.equalsIgnoreCase(efoTrait)){
                        found = true;
                    }
                }
                if(!found){
                    getLog().info("Class " + uri + " does not have a label or synonym of " + efoTrait + ". DB ID is " + entry.getID());
                    for(String label : labels){
                        getLog().info("Label for class " + uri + " is " + label);
                    }

                }
            }
        }
        else{
            getLog().info(uri + " is not a valid EFO URI");

            Collection<OWLClass> classes = getOntologyDAO().getOWLClassesByLabel(efoTrait);

            if(classes.isEmpty()){
                getLog().info("No EFO classes match the label " + efoTrait);
            }
        }
    }
}
