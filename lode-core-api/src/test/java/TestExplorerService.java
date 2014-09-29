import junit.framework.TestCase;
import org.junit.Ignore;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.fgpt.lode.exception.LodeException;
import uk.ac.ebi.fgpt.lode.model.ExplorerViewConfiguration;
import uk.ac.ebi.fgpt.lode.model.RelatedResourceDescription;
import uk.ac.ebi.fgpt.lode.service.ExploreService;

import java.net.URI;
import java.util.LinkedHashSet;

/**
 * @author Simon Jupp
 * @date 03/05/2013
 * Functional Genomics Group EMBL-EBI
 */
@Ignore
public class TestExplorerService extends TestCase {


    ExplorerViewConfiguration viewConfiguration;
    ExploreService service;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:test-explorer-config.xml");

        viewConfiguration = (ExplorerViewConfiguration) ctx.getBean("testExplorerConfig");

        service = (ExploreService) ctx.getBean("testExplorerServiceImpl");



    }

    public void testExplorerService ()  {

//        URI test = URI.create("http://rdf.ebi.ac.uk/resource/atlas/experiment/E-MEXP-3394");
//        try {
////            for (RelatedResourceDescription resdec : service.getRelatedResourceByProperty(test, new LinkedHashSet<URI>(viewConfiguration.getTopRelationships()), viewConfiguration.getIgnoreTypes(), viewConfiguration.ignoreBlankNodes())) {
////
//////                System.out.println(resdec.toString());
////
////            }
//        } catch (LodeException e) {
//            e.printStackTrace();
//        }
    }

}
