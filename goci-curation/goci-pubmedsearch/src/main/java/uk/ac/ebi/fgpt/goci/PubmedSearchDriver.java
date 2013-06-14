package uk.ac.ebi.fgpt.goci;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.fgpt.goci.exception.DispatcherException;
import uk.ac.ebi.fgpt.goci.service.GwasPubmedSearcher;


public class PubmedSearchDriver {

    public static void main(String[] args){

        PubmedSearchDriver driver = new PubmedSearchDriver();
        driver.launchSearcher();
    }


    /*For Coldfusion integration, we don't need a main method*/

    private GwasPubmedSearcher searcher;

    public PubmedSearchDriver(){
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:pubmedsearch.xml");
        searcher = ctx.getBean("searcher", GwasPubmedSearcher.class);
  //      launchSearcher();
    }

    public void launchSearcher(){
        try{
            searcher.dispatchSearch();
        }

        catch (DispatcherException e) {
            e.printStackTrace();

        }
    }
}
