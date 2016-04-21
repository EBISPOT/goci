package uk.ac.ebi.spot.goci.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by emma on 21/04/2016.
 *
 * @author emma
 *         <p>
 *         Test for AssociationAttributeService
 */
public class AssociationAttributeServiceTest {

    private AssociationAttributeService associationAttributeService;

    @Mock
    private EfoTraitRepository efoTraitRepository;

    @Before
    public void setUp() throws Exception {
        associationAttributeService = new AssociationAttributeService(efoTraitRepository);
    }

    @Test
    public void testCreateLocusGenes() throws Exception {

        // Test with collection of comma separated genes
        assertThat(associationAttributeService.createLocusGenes("SFRP1,SFRP2", ",")).isInstanceOf(Collection.class);
        assertThat(associationAttributeService.createLocusGenes("SFRP1,SFRP2", ",")).hasSize(2);
        assertThat(associationAttributeService.createLocusGenes("SFRP1,SFRP2", ",")).hasOnlyElementsOfType(Gene.class);
        assertThat(associationAttributeService.createLocusGenes("SFRP1,SFRP2", ",")).extracting("geneName")
                .contains("SFRP1", "SFRP2");

        // Test null gene
        assertThat(associationAttributeService.createLocusGenes(null, ",")).isInstanceOf(Collection.class);
        assertThat(associationAttributeService.createLocusGenes(null, ",")).hasSize(0);
    }

    @Test
    public void testCreateGene() throws Exception {

    }

    @Test
    public void testCreateRiskAllele() throws Exception {

    }

    @Test
    public void testCreateSnp() throws Exception {

    }
}