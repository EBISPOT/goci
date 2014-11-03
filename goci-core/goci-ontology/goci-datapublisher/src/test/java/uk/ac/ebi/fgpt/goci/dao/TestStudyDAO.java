package uk.ac.ebi.fgpt.goci.dao;

import junit.framework.TestCase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.ebi.fgpt.goci.model.Study;

import java.util.Collection;
import java.util.Collections;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        study = mock(Study.class);

        JdbcTemplate mockTemplate = mock(JdbcTemplate.class);
        when(mockTemplate.query(anyString(), isA(RowMapper.class))).thenReturn(Collections.singletonList(study));

        TraitAssociationDAO traitAssociationDAO = mock(TraitAssociationDAO.class);

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
        assertEquals(1, studies.size());
        Study fetchedStudy = studies.iterator().next();
        assertEquals(study, fetchedStudy);
    }
}
