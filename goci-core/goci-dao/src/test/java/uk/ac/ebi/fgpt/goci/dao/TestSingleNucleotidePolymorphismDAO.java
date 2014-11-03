package uk.ac.ebi.fgpt.goci.dao;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.ebi.fgpt.goci.model.SingleNucleotidePolymorphism;

import java.util.Collection;
import java.util.Collections;

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
        snp = Mockito.mock(SingleNucleotidePolymorphism.class);

        JdbcTemplate mockTemplate = Mockito.mock(JdbcTemplate.class);
        Mockito.when(mockTemplate.query(Matchers.anyString(), Matchers.isA(RowMapper.class))).thenReturn(Collections.singletonList(snp));

        dao = new SingleNucleotidePolymorphismDAO();
        dao.setJdbcTemplate(mockTemplate);
    }

    public void tearDown() {
        snp = null;
        dao = null;
    }

    public void testRetrieveAllSNPs() {
        Collection<SingleNucleotidePolymorphism> snps = dao.retrieveAllSNPs();
        Assert.assertEquals(1, snps.size());
        SingleNucleotidePolymorphism fetchedSnp = snps.iterator().next();
        Assert.assertEquals(snp, fetchedSnp);
    }
}
