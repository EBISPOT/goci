package uk.ac.ebi.fgpt.goci;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.fgpt.goci.exception.DispatcherException;
import uk.ac.ebi.fgpt.goci.service.GwasPubmedImporter;


public class PubmedImportDriver {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

//    public static void main(String[] args){
//
//        PubmedSearchDriver driver = new PubmedSearchDriver();
//        driver.launchImporter();
//    }


    /*For Coldfusion integration, we don't need a main method*/

    private GwasPubmedImporter importer;

    public PubmedImportDriver(int PMID){
        getLog().debug("PubmedImportDriver initialised");
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:pubmedimport.xml");
        importer = ctx.getBean("searcher", GwasPubmedImporter.class);
        launchImporter(PMID);
    }

    public void launchImporter(int PMID){
        try{
            getLog().debug("Dispatching the importer");
            importer.dispatchSearch();
            getLog().info("Import finished");
        }

        catch (DispatcherException e) {
            e.printStackTrace();

        }
    }
}
