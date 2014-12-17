package uk.ac.ebi.spot.goci;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.spot.goci.exception.DispatcherException;
import uk.ac.ebi.spot.goci.service.GwasPubmedSearcher;


public class PubmedSearchDriver {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

//    public static void main(String[] args){
//
//        PubmedSearchDriver driver = new PubmedSearchDriver();
//        driver.launchSearcher();
//    }


    /*For Coldfusion integration, we don't need a main method*/

    private GwasPubmedSearcher searcher;

    public PubmedSearchDriver(){
        getLog().debug("PubmedSearchDriver initialised");
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:pubmedsearch.xml");
        searcher = ctx.getBean("searcher", GwasPubmedSearcher.class);
        launchSearcher();
    }

    public void launchSearcher(){
        try{
            getLog().debug("Dispatching the searcher");
            searcher.dispatchSearch();
            getLog().info("Search finished");
        }

        catch (DispatcherException e) {
            e.printStackTrace();

        }
    }
}
