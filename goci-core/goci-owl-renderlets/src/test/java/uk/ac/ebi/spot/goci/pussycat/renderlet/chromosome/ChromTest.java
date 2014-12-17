package uk.ac.ebi.spot.goci.pussycat.renderlet.chromosome;

import junit.framework.TestCase;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.ac.ebi.spot.goci.owl.pussycat.renderlet.chromosome.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;

//import javax.swing.text.Document;

/**
 * Created by IntelliJ IDEA.
 * User: dwelter
 * Date: 01/03/12
 * Time: 11:20
 * To change this template use File | Settings | File Templates.
 */
public class ChromTest extends TestCase{


    @Test
    public void testPath(){

        OWLChromosomeRenderlet chrom = new OWLChromosomeOneRenderlet();

        URL path = chrom.getSVGFile();
        
        System.out.println(path);


        DocumentBuilderFactory docFactory =  DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Document doc = null;
        try {
            doc = docBuilder.parse(path.toString());
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        NodeList children = doc.getChildNodes();
        
        System.out.println("No of children: " + children.getLength());

        for(int i=0; i< children.getLength(); i++){
            Node child = children.item(i);
            
            System.out.println("Child's name: " + child.getNodeName());
            
        }

        assertNotNull(path);
    }

}
