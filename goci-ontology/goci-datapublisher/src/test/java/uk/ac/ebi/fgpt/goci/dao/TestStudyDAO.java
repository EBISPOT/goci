package uk.ac.ebi.fgpt.goci.dao;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Javadocs go here.
 *
 * @author Junit Generation Plugin for Maven, written by Tony Burdett
 * @date 26-01-2012
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

    public void testGetTraitAssociationMap() {
        fail("Unimplemented test testGetTraitAssociationMap");
    }

    public void testGetJdbcTemplate() {
        fail("Unimplemented test testGetJdbcTemplate");
    }

    public void testRetrieveAllStudies() {
        fail("Unimplemented test testRetrieveAllStudies");
    }

    public void testSetTraitAssocationDAO() {
        fail("Unimplemented test testSetTraitAssocationDAO");
    }

    public void testGetTraitAssocationDAO() {
        fail("Unimplemented test testGetTraitAssocationDAO");
    }

    public void testSetJdbcTemplate() {
        fail("Unimplemented test testSetJdbcTemplate");
    }

    public void testDoInitialization() {
        fail("Unimplemented test testDoInitialization");
    }
}
