package uk.ac.ebi.fgpt.goci;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.fgpt.goci.exception.DispatcherException;
import uk.ac.ebi.fgpt.goci.lang.ImporterProperties;
import uk.ac.ebi.fgpt.goci.service.GwasPubmedImporter;


public class PubmedImportDriver {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    /*For Coldfusion integration, we don't need a main method*/

    private GwasPubmedImporter importer;

    public PubmedImportDriver(String PMID, String table){
        getLog().debug("PubmedImportDriver initialised");
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:pubmedimport.xml");
        importer = ctx.getBean("searcher", GwasPubmedImporter.class);
        getLog().info("The study " + PMID + " is to be added to table " + table);
        ImporterProperties.setOutputTable(table);
 //       launchImporter(PMID);
    }

//    public void launchImporter(String PMID){
//        try{
//            getLog().debug("Dispatching the importer");
//            importer.dispatchSearch(PMID);
//            getLog().info("Import finished");
//        }
//
//        catch (DispatcherException e) {
//            e.printStackTrace();
//
//        }
//    }

    public String launchImporter(String PMID){
        PMID = PMID.trim();
        String output = null;
        try{
            getLog().debug("Dispatching the importer");
            output = importer.dispatchSearch(PMID);
            getLog().info("Import finished");
        }

        catch (DispatcherException e) {
            e.printStackTrace();

        }
        if(output == null){
            output = "Oh dear, something went wrong. Please contact your system admin.";
        }
        return output;
    }
}
