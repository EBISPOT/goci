package uk.ac.ebi.fgpt.goci.dao;

import junit.framework.TestCase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.ebi.fgpt.goci.model.SingleNucleotidePolymorphism;

import java.util.Collection;
import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * Javadocs go here.
 *
 * @author Junit Generation Plugin for Maven, written by Tony Burdett
 * Date 26-01-2012
 */
public class TestSingleNucleotidePolymorphismDAO extends TestCase {
    private SingleNucleotidePolymorphismDAO dao;
    private SingleNucleotidePolymorphism snp;

    public void setUp() {
        snp = mock(SingleNucleotidePolymorphism.class);

        JdbcTemplate mockTemplate = mock(JdbcTemplate.class);
        when(mockTemplate.query(anyString(), isA(RowMapper.class))).thenReturn(Collections.singletonList(snp));

        dao = new SingleNucleotidePolymorphismDAO();
        dao.setJdbcTemplate(mockTemplate);
    }

    public void tearDown() {
        snp = null;
        dao = null;
    }

    public void testRetrieveAllSNPs() {
        Collection<SingleNucleotidePolymorphism> snps = dao.retrieveAllSNPs();
        assertEquals(1, snps.size());
        SingleNucleotidePolymorphism fetchedSnp = snps.iterator().next();
        assertEquals(snp, fetchedSnp);
    }
}
