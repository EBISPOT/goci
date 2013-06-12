package uk.ac.ebi.fgpt.goci;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.fgpt.goci.exception.DispatcherException;
import uk.ac.ebi.fgpt.goci.service.EndnoteExporter;

/**
 * Hello world!
 *
 */
public class EndnoteExportDriver
{
    //    public static void main(String[] args){
//
//        PubmedSearchDriver driver = new PubmedSearchDriver();
//        driver.launchSearcher();
//    }


    /*For Coldfusion integration, we don't need a main method*/

    private EndnoteExporter exporter;
    private String data;

    public EndnoteExportDriver(String table){
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:enexport.xml");
        exporter = ctx.getBean("searcher", EndnoteExporter.class);
        launchExporter(table);
    }

    public void launchExporter(String table){
        try{
            data = exporter.dispatchQuery(table);
        }

        catch (DispatcherException e) {
            e.printStackTrace();

        }
    }

    public String getData(){
        return data;
    }
}
