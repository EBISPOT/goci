package uk.ac.ebi.spot.goci;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.spot.goci.checker.Checker;

public class GOCIUriLabelCheckerDriver
 {

     public static void main( String[] args )
     {
         GOCIUriLabelCheckerDriver driver = new GOCIUriLabelCheckerDriver();
         driver.validateURIs();
     }

     private Checker checker;

     public GOCIUriLabelCheckerDriver(){
         ApplicationContext ctx = new ClassPathXmlApplicationContext("goci-urilabelchecker.xml");
         checker = ctx.getBean("checker", Checker.class);
     }

     public void validateURIs(){
        checker.checkURIs();
        System.out.println("All URIs-label pairs checked");
     }

 }
