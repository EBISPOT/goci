package uk.ac.ebi.fgpt.goci.dao;

import junit.framework.TestCase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.ebi.fgpt.goci.model.TraitAssociation;

import java.util.Collection;
import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * Javadocs go here.
 *
 * @author Junit Generation Plugin for Maven, written by Tony Burdett
 * Date 26-01-2012
 */
public class TestTraitAssociationDAO extends TestCase {
    private TraitAssociation traitAssociation;
    private TraitAssociationDAO dao;

    public void setUp() {
        traitAssociation = mock(TraitAssociation.class);

        JdbcTemplate mockTemplate = mock(JdbcTemplate.class);
        when(mockTemplate.query(anyString(), isA(RowMapper.class))).thenReturn(Collections.singletonList(traitAssociation));

        SingleNucleotidePolymorphismDAO snpDAO = mock(SingleNucleotidePolymorphismDAO.class);
        OntologyDAO ontologyDAO = mock(OntologyDAO.class);

        // create trait association dao
        dao = new TraitAssociationDAO();
        // inject dependencies
        dao.setJdbcTemplate(mockTemplate);
        dao.setSNPDAO(snpDAO);
        dao.setOntologyDAO(ontologyDAO);
        dao.init();
    }

    public void tearDown() {
        traitAssociation = null;
        dao = null;
    }

    public void testRetrieveAllTraitAssociations() {
        Collection<TraitAssociation> associations = dao.retrieveAllTraitAssociations();
        assertEquals(1, associations.size());
        TraitAssociation fetchedAssocation = associations.iterator().next();
        assertEquals(traitAssociation, fetchedAssocation);
    }
}
