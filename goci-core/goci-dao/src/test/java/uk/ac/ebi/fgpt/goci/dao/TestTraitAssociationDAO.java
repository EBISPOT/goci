package uk.ac.ebi.fgpt.goci.dao;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.ebi.fgpt.goci.model.TraitAssociation;

import java.util.Collection;
import java.util.Collections;

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
        traitAssociation = Mockito.mock(TraitAssociation.class);

        JdbcTemplate mockTemplate = Mockito.mock(JdbcTemplate.class);
        Mockito.when(mockTemplate.query(Matchers.anyString(), Matchers.isA(RowMapper.class))).thenReturn(Collections.singletonList(traitAssociation));

        SingleNucleotidePolymorphismDAO snpDAO = Mockito.mock(SingleNucleotidePolymorphismDAO.class);
        OntologyDAO ontologyDAO = Mockito.mock(OntologyDAO.class);

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
        Assert.assertEquals(1, associations.size());
        TraitAssociation fetchedAssocation = associations.iterator().next();
        Assert.assertEquals(traitAssociation, fetchedAssocation);
    }
}
