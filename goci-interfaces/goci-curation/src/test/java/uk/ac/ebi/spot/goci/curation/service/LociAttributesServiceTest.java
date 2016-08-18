package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.builder.GeneBuilder;
import uk.ac.ebi.spot.goci.builder.LocusBuilder;
import uk.ac.ebi.spot.goci.builder.RiskAlleleBuilder;
import uk.ac.ebi.spot.goci.builder.SingleNucleotidePolymorphismBuilder;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.GeneRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;
import uk.ac.ebi.spot.goci.repository.RiskAlleleRepository;
import uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 15/08/2016.
 *
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class LociAttributesServiceTest {

    @Mock
    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;

    @Mock
    private GeneRepository geneRepository;

    @Mock
    private RiskAlleleRepository riskAlleleRepository;

    @Mock
    private LocusRepository locusRepository;

    private LociAttributesService lociAttributesService;

    private static final Locus LOCUS = new LocusBuilder().setId((long) 100).build();

    private static final RiskAllele RISK_ALLELE = new RiskAlleleBuilder().setId((long) 120).build();

    private static final Gene GENE = new GeneBuilder().setId(113L).setGeneName("WBP4").build();

    private static final Gene GENE_NOT_IN_DB = new GeneBuilder().setGeneName("TEST").build();

    private static final SingleNucleotidePolymorphism SNP_01 =
            new SingleNucleotidePolymorphismBuilder().setRsId("rs579459")
                    .build();

    private static final SingleNucleotidePolymorphism PROXY_SNP_01 =
            new SingleNucleotidePolymorphismBuilder().setRsId("rs6538678")
                    .build();

    private static final SingleNucleotidePolymorphism PROXY_SNP_02 =
            new SingleNucleotidePolymorphismBuilder().setRsId("rs7329174")
                    .build();

    private static final Locus ASS_LOCUS =
            new LocusBuilder().setId(111L)
                    .setDescription("Single variant").setStrongestRiskAlleles(Collections.singletonList(RISK_ALLELE))
                    .build();

    private static final Association ASSOCIATION =
            new AssociationBuilder().setLoci(Collections.singletonList(ASS_LOCUS)).build();

    @Before
    public void setUp() throws Exception {
        lociAttributesService = new LociAttributesService(singleNucleotidePolymorphismRepository,
                                                          geneRepository,
                                                          riskAlleleRepository,
                                                          locusRepository);
    }

    @Test
    public void saveGeneWithGeneAlreadyInDatabase() throws Exception {
        when(geneRepository.findByGeneName(GENE.getGeneName())).thenReturn(GENE);
        lociAttributesService.saveGene(Collections.singleton(GENE));
        verify(geneRepository, times(1)).findByGeneName(GENE.getGeneName());
        verify(geneRepository, never()).save(GENE);
    }

    @Test
    public void saveGeneNotInDatabase() throws Exception {
        when(geneRepository.findByGeneName(GENE_NOT_IN_DB.getGeneName())).thenReturn(null);
        lociAttributesService.saveGene(Collections.singleton(GENE_NOT_IN_DB));
        verify(geneRepository, times(1)).findByGeneName(GENE_NOT_IN_DB.getGeneName());
        verify(geneRepository, times(1)).save(GENE_NOT_IN_DB);
    }

    @Test
    public void createGene() throws Exception {
        Collection<String> genes = new ArrayList<>();
        genes.add("SFRP1  ");
        genes.add("Intergenic");

        assertThat(lociAttributesService.createGene(genes)).extracting(Gene::getGeneName)
                .containsExactly("SFRP1", "intergenic");
    }

    @Test
    public void saveRiskAllele() throws Exception {

        RiskAllele NEW_RISK_ALLELE = new RiskAlleleBuilder().setSnp(SNP_01)
                .setRiskAlleleName("rs579459-?")
                .setProxySnps(Arrays.asList(PROXY_SNP_01, PROXY_SNP_02))
                .build();

        // Stubbing
        when(singleNucleotidePolymorphismRepository.findByRsIdIgnoreCase(NEW_RISK_ALLELE.getSnp()
                                                                                 .getRsId())).thenReturn(
                null);
        when(singleNucleotidePolymorphismRepository.findByRsIdIgnoreCase(PROXY_SNP_01.getRsId())).thenReturn(
                null);
        when(singleNucleotidePolymorphismRepository.findByRsIdIgnoreCase(PROXY_SNP_02.getRsId())).thenReturn(
                null);
        when(singleNucleotidePolymorphismRepository.save(NEW_RISK_ALLELE.getSnp())).thenReturn(SNP_01);
        when(singleNucleotidePolymorphismRepository.save(PROXY_SNP_01)).thenReturn(PROXY_SNP_01);
        when(singleNucleotidePolymorphismRepository.save(PROXY_SNP_02)).thenReturn(PROXY_SNP_02);

        lociAttributesService.saveRiskAlleles(Collections.singletonList(NEW_RISK_ALLELE));

        verify(singleNucleotidePolymorphismRepository, times(1)).findByRsIdIgnoreCase(NEW_RISK_ALLELE.getSnp()
                                                                                              .getRsId());
        verify(singleNucleotidePolymorphismRepository, times(1)).save(NEW_RISK_ALLELE.getSnp());

        verify(singleNucleotidePolymorphismRepository, times(1)).findByRsIdIgnoreCase(PROXY_SNP_01.getRsId());
        verify(singleNucleotidePolymorphismRepository, times(1)).save(PROXY_SNP_01);

        verify(singleNucleotidePolymorphismRepository, times(1)).findByRsIdIgnoreCase(PROXY_SNP_02.getRsId());
        verify(singleNucleotidePolymorphismRepository, times(1)).save(PROXY_SNP_02);

        verify(riskAlleleRepository, times(1)).save(NEW_RISK_ALLELE);
    }


    @Test
    public void createRiskAllele() throws Exception {
        assertThat(lociAttributesService.createRiskAllele("Rs579459-?",
                                                          SNP_01)).extracting(riskAllele -> riskAllele.getRiskAlleleName())
                .containsExactly("rs579459-?");
        assertThat(lociAttributesService.createRiskAllele("Rs579459-?",
                                                          SNP_01)).extracting(riskAllele -> riskAllele.getSnp()
                .getRsId())
                .containsExactly("rs579459");
    }

    @Test
    public void deleteRiskAllele() throws Exception {
        lociAttributesService.deleteRiskAllele(RISK_ALLELE);
        verify(riskAlleleRepository, times(1)).delete(RISK_ALLELE);
    }

    @Test
    public void deleteLocus() throws Exception {
        lociAttributesService.deleteLocus(LOCUS);
        verify(locusRepository, times(1)).delete(LOCUS);
    }

    @Test
    public void createSnp() throws Exception {
        assertThat(lociAttributesService.createSnp("rs123")).isInstanceOf(SingleNucleotidePolymorphism.class)
                .extracting(singleNucleotidePolymorphism -> singleNucleotidePolymorphism.getRsId())
                .containsExactly("rs123");
    }

    @Test
    public void deleteLocusAndRiskAlleles() throws Exception {
        lociAttributesService.deleteLocusAndRiskAlleles(ASSOCIATION);
        verify(locusRepository, times(1)).delete(ASS_LOCUS);
        verify(riskAlleleRepository, times(1)).delete(RISK_ALLELE);
    }
}