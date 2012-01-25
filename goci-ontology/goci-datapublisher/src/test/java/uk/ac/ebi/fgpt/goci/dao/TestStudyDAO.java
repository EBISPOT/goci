package uk.ac.ebi.fgpt.goci.dao;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.fgpt.goci.exception.ObjectMappingException;
import uk.ac.ebi.fgpt.goci.exception.OntologyTermException;
import uk.ac.ebi.fgpt.goci.model.Study;
import uk.ac.ebi.fgpt.goci.model.TraitAssociation;

import java.util.Collection;
import java.util.Iterator;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 25/01/12
 */
public class TestStudyDAO extends TestCase {
    private StudyDAO studyDAO;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ApplicationContext ctx = new ClassPathXmlApplicationContext("goci-datapublisher-dao.xml");
        studyDAO = ctx.getBean("studyDAO", StudyDAO.class);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        studyDAO = null;
    }

    public void testRetrieveAllStudies() throws Exception {
        try {
            // fetch studies
            Collection<Study> studies = studyDAO.retrieveAllStudies();
            assertNotNull("Collection of studies was null", studies);
            getLog().debug("Query complete, got " + studies.size() + " studies");

            // now check a random assortment of 5 studies for trait associations, abandoning broken ones
            int count = 0;
            int noAssocCount = 0;
            int termMismatches = 0;
            for (Study study : studies) {
                try {
                    Collection<TraitAssociation> associations = study.getIdentifiedAssociations();
                    assertNotNull("Collection of associations was null", associations);
                    getLog().debug("Study (PubMed ID '" + study.getPubMedID() + "') had " + associations.size() +
                                           " associations");
                    if (associations.size() > 0) {
                        for (TraitAssociation association : associations) {
                            assertNotNull("Association should not link to a null SNP", association.getAssociatedSNP());
                            assertNotNull("Association should not link to a null EFO class",
                                          association.getAssociatedTrait());
                            getLog().debug(
                                    "    Association: SNP '" + association.getAssociatedSNP().getRSID() +
                                            "' <-> Trait '" +
                                            association.getAssociatedTrait().getIRI() + "'");
                        }
                        count++;
                    }
                    else {
                        noAssocCount++;
                    }
                }
                catch (ObjectMappingException e) {
                    if (e instanceof OntologyTermException) {
                        termMismatches++;
                    }
                    else {
                        getLog().error(
                                "Excluding Study (PubMed ID '" + study.getPubMedID() + "'), " + e.getMessage());
                    }
                }
            }
            int eligCount = studies.size() - noAssocCount;
            getLog().info("\n\nREPORT:\n" +
                                  eligCount + "/" + studies.size() +
                                  " declared associations and therefore could usefully be mapped.\n" +
                                  count + "/" + eligCount +
                                  " studies could be completely mapped after passing all checks.\n" +
                                  termMismatches + "/" + eligCount +
                                  " failed due to missing or duplicated terms in EFO,\n" +
                                  (eligCount - count - termMismatches) + "/" + eligCount +
                                  " failed due to data integrity concerns");
        }
        catch (Exception e) {
            getLog().error("Query failed", e);
            fail();
        }
    }
}
