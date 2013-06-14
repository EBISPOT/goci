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
//        public static void main(String[] args){
//
//         new EndnoteExportDriver("notgwasstudies");
//        //driver.launchExporter("notgwasstudies");
//    }
//

    /*For Coldfusion integration, we don't need a main method*/

    private EndnoteExporter exporter;
    private String data;

    public EndnoteExportDriver(String table){
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:enexport.xml");
        exporter = ctx.getBean("exporter", EndnoteExporter.class);
        launchExporter(table);
    }

    public void launchExporter(String table){
        try{
            data = exporter.dispatchQuery(table);
            System.out.println(data);
        }

        catch (DispatcherException e) {
            e.printStackTrace();

        }
    }

    public String getData(){
        return data;
    }
}
