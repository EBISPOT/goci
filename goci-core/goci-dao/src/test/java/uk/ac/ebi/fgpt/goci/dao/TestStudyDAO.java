package uk.ac.ebi.fgpt.goci.dao;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.ebi.fgpt.goci.model.Study;

import java.util.Collection;
import java.util.Collections;

/**
 * Javadocs go here.
 *
 * @author Junit Generation Plugin for Maven, written by Tony Burdett
 * Date 26-01-2012
 */
public class TestStudyDAO extends TestCase {
    private Study study;
    private StudyDAO dao;
    
    public void setUp() {
        study = Mockito.mock(Study.class);

        JdbcTemplate mockTemplate = Mockito.mock(JdbcTemplate.class);
        Mockito.when(mockTemplate.query(Matchers.anyString(), Matchers.isA(RowMapper.class))).thenReturn(Collections.singletonList(study));

        TraitAssociationDAO traitAssociationDAO = Mockito.mock(TraitAssociationDAO.class);

        // create study dao
        dao = new StudyDAO();
        // inject dependencies
        dao.setJdbcTemplate(mockTemplate);
        dao.setTraitAssociationDAO(traitAssociationDAO);
        dao.init();
    }

    public void tearDown() {
        study = null;
        dao = null;
    }


    public void testRetrieveAllStudies() {
        Collection<Study> studies = dao.retrieveAllStudies();
        Assert.assertEquals(1, studies.size());
        Study fetchedStudy = studies.iterator().next();
        Assert.assertEquals(study, fetchedStudy);
    }
}
