package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.curation.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.curation.builder.EfoTraitBuilder;
import uk.ac.ebi.spot.goci.curation.builder.GeneBuilder;
import uk.ac.ebi.spot.goci.curation.builder.LocationBuilder;
import uk.ac.ebi.spot.goci.curation.builder.LocusBuilder;
import uk.ac.ebi.spot.goci.curation.builder.RegionBuilder;
import uk.ac.ebi.spot.goci.curation.builder.RiskAlleleBuilder;
import uk.ac.ebi.spot.goci.curation.builder.SingleNucleotidePolymorphismBuilder;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationStandardMultiForm;
import uk.ac.ebi.spot.goci.curation.model.SnpFormRow;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.Region;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.GenomicContextRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.Assert.assertNull;

/**
 * Created by emma on 06/04/2016.
 *
 * @author emma
 *         <p>
 *         Test for SingleSnpMultiSnpAssociationService.java
 */
@RunWith(MockitoJUnitRunner.class)
public class SingleSnpMultiSnpAssociationServiceTest {

    @Mock
    private AssociationRepository associationRepository;

    @Mock
    private LocusRepository locusRepository;

    @Mock
    private GenomicContextRepository genomicContextRepository;

    @Mock
    private LociAttributesService lociAttributesService;

    private SingleSnpMultiSnpAssociationService singleSnpMultiSnpAssociationService;

    // Entity objects
    private static final EfoTrait EFO_01 = new EfoTraitBuilder()
            .setId(988L)
            .setTrait("atrophic rhinitis")
            .setUri("http://www.ebi.ac.uk/efo/EFO_0007159")
            .build();

    private static final EfoTrait EFO_02 = new EfoTraitBuilder()
            .setId(989L)
            .setTrait("HeLa")
            .setUri("http://www.ebi.ac.uk/efo/EFO_0001185")
            .build();

    private static final Gene GENE_01 = new GeneBuilder().setId(112L).setGeneName("NEGR1").build();

    private static final Gene GENE_02 = new GeneBuilder().setId(113L).setGeneName("FRS2").build();

    private static final Gene GENE_03 = new GeneBuilder().setId(113L).setGeneName("ELF1").build();

    private static final Gene GENE_04 = new GeneBuilder().setId(113L).setGeneName("WBP4").build();

    private static final Gene GENE_05 = new GeneBuilder().setId(113L).setGeneName("SLC25A15").build();

    private static final Region REGION_01 = new RegionBuilder().setId(897L).setName("9q33.1").build();

    private static final Location LOCATION_01 =
            new LocationBuilder().setId(654L).setChromosomeName("1").setChromosomePosition("159001296").build();

    private static final SingleNucleotidePolymorphism
            PROXY_SNP_01 = new SingleNucleotidePolymorphismBuilder().setId(311L)
            .setLastUpdateDate(new Date())
            .setRsId("rs6538678")
            .build();

    private static final SingleNucleotidePolymorphism
            PROXY_SNP_02 = new SingleNucleotidePolymorphismBuilder().setId(311L)
            .setLastUpdateDate(new Date())
            .setRsId("rs7329174")
            .build();

    private static final SingleNucleotidePolymorphism
            PROXY_SNP_03 = new SingleNucleotidePolymorphismBuilder().setId(311L)
            .setLastUpdateDate(new Date())
            .setRsId("rs1234567")
            .build();

    private static final SingleNucleotidePolymorphism SNP_01 = new SingleNucleotidePolymorphismBuilder().setId(311L)
            .setLastUpdateDate(new Date())
            .setRsId("rs579459")
            .build();

    private static final SingleNucleotidePolymorphism SNP_02 = new SingleNucleotidePolymorphismBuilder().setId(311L)
            .setLastUpdateDate(new Date())
            .setRsId("rs9533090")
            .build();

    private static final SingleNucleotidePolymorphism SNP_03 = new SingleNucleotidePolymorphismBuilder().setId(311L)
            .setLastUpdateDate(new Date())
            .setRsId("rs114205691")
            .build();

    private static final SingleNucleotidePolymorphism SNP_04 = new SingleNucleotidePolymorphismBuilder().setId(311L)
            .setLastUpdateDate(new Date())
            .setRsId("rs9788721")
            .build();

    private static final SingleNucleotidePolymorphism SNP_05 = new SingleNucleotidePolymorphismBuilder().setId(311L)
            .setLastUpdateDate(new Date())
            .setRsId("rs8042374")
            .build();


    private static final RiskAllele RISK_ALLELE_01 = new RiskAlleleBuilder().setId(411L)
            .setRiskAlleleName("rs579459-?")
            .build();

    private static final RiskAllele RISK_ALLELE_02 = new RiskAlleleBuilder().setId(412L)
            .setRiskAlleleName("rs9533090-?")
            .build();

    private static final RiskAllele RISK_ALLELE_03 = new RiskAlleleBuilder().setId(413L)
            .setRiskAlleleName("rs114205691-?")
            .build();

    private static final RiskAllele RISK_ALLELE_04 = new RiskAlleleBuilder().setId(414L)
            .setRiskAlleleName("rs9788721-?")
            .build();

    private static final RiskAllele RISK_ALLELE_05 = new RiskAlleleBuilder().setId(451L)
            .setRiskAlleleName("rs8042374-?")
            .build();


    private static final Locus LOCUS_01 =
            new LocusBuilder().setId(111L)
                    .setDescription("Single variant")
                    .build();

    private static final Locus LOCUS_02 =
            new LocusBuilder().setId(111L)
                    .setDescription("2-SNP haplotype")
                    .build();

    private static final Locus LOCUS_03 =
            new LocusBuilder().setId(111L)
                    .setDescription("SNP x SNP interaction")
                    .build();

    private static final Locus LOCUS_04 =
            new LocusBuilder().setId(111L)
                    .setDescription("SNP x SNP interaction")
                    .build();

    private static final Association BETA_SINGLE_ASSOCIATION =
            new AssociationBuilder().setId((long) 100)
                    .setBetaDirection("decrease")
                    .setBetaUnit("mm Hg")
                    .setBetaNum((float) 1.06)
                    .setSnpType("novel")
                    .setMultiSnpHaplotype(false)
                    .setSnpInteraction(false)
                    .setSnpApproved(false)
                    .setPvalueExponent(-8)
                    .setPvalueMantissa(1)
                    .setStandardError((float) 6.24)
                    .setRange("[14.1-38.56]")
                    .setPvalueDescription("(ferritin)")
                    .setRiskFrequency(String.valueOf(0.93))
                    .setEfoTraits(Arrays.asList(EFO_01, EFO_02))
                    .setDescription("this is a test")
                    .build();

    private static final Association OR_MULTI_ASSOCIATION =
            new AssociationBuilder().setId((long) 101)
                    .setSnpType("novel")
                    .setMultiSnpHaplotype(true)
                    .setSnpInteraction(false)
                    .setSnpApproved(false)
                    .setPvalueExponent(-8)
                    .setPvalueMantissa(1)
                    .setStandardError((float) 6.24)
                    .setRange("[14.1-38.56]")
                    .setOrPerCopyNum((float) 1.89)
                    .setOrPerCopyRecip((float) 0.99)
                    .setOrPerCopyRecipRange("[1.0-8.0]")
                    .setPvalueDescription("(ferritin)")
                    .setRiskFrequency(String.valueOf(0.12))
                    .setEfoTraits(Arrays.asList(EFO_01, EFO_02))
                    .setDescription("this is a test")
                    .build();

    private static final Association OR_INTERACTION_ASSOCIATION =
            new AssociationBuilder().setId((long) 103)
                    .setSnpType("novel")
                    .setMultiSnpHaplotype(false)
                    .setSnpInteraction(true)
                    .setSnpApproved(false)
                    .setPvalueExponent(-8)
                    .setPvalueMantissa(1)
                    .setStandardError((float) 6.24)
                    .setRange("[14.1-38.56]")
                    .setOrPerCopyNum((float) 1.89)
                    .setOrPerCopyRecip((float) 0.99)
                    .setOrPerCopyRecipRange("[1.0-8.0]")
                    .setPvalueDescription("(ferritin)")
                    .setRiskFrequency(String.valueOf(0.12))
                    .setEfoTraits(Arrays.asList(EFO_01, EFO_02))
                    .setDescription("this is a test")
                    .build();


    @BeforeClass
    public static void setUpModelObjects() throws Exception {

        // Create the links between all our objects
        REGION_01.setLocations(Collections.singletonList(LOCATION_01));
        LOCATION_01.setRegion(REGION_01);

        // For testing all SNPs can have the same locations
        SNP_01.setLocations(Collections.singletonList(LOCATION_01));
        SNP_02.setLocations(Collections.singletonList(LOCATION_01));
        SNP_03.setLocations(Collections.singletonList(LOCATION_01));

        // Set snp risk alleles
        SNP_01.setRiskAlleles(Collections.singletonList(RISK_ALLELE_01));
        SNP_02.setRiskAlleles(Collections.singletonList(RISK_ALLELE_02));
        SNP_03.setRiskAlleles(Collections.singletonList(RISK_ALLELE_03));
        SNP_04.setRiskAlleles(Collections.singletonList(RISK_ALLELE_04));
        SNP_05.setRiskAlleles(Collections.singletonList(RISK_ALLELE_05));

        // Set risk allele snp
        RISK_ALLELE_01.setSnp(SNP_01);
        RISK_ALLELE_02.setSnp(SNP_02);
        RISK_ALLELE_03.setSnp(SNP_03);
        RISK_ALLELE_04.setSnp(SNP_04);
        RISK_ALLELE_05.setSnp(SNP_05);

        // Set risk allele proxy snp
        RISK_ALLELE_01.setProxySnps(Collections.singletonList(PROXY_SNP_01));
        RISK_ALLELE_02.setProxySnps(Collections.singletonList(PROXY_SNP_02));
        RISK_ALLELE_03.setProxySnps(Collections.singletonList(PROXY_SNP_03));

        // Set locus risk allele
        LOCUS_01.setStrongestRiskAlleles(Collections.singletonList(RISK_ALLELE_01));
        LOCUS_02.setStrongestRiskAlleles(Collections.singletonList(RISK_ALLELE_02));
        LOCUS_02.setStrongestRiskAlleles(Collections.singletonList(RISK_ALLELE_03));
        LOCUS_03.setStrongestRiskAlleles(Collections.singletonList(RISK_ALLELE_04));
        LOCUS_04.setStrongestRiskAlleles(Collections.singletonList(RISK_ALLELE_05));

        // Set Locus genes
        LOCUS_01.setAuthorReportedGenes(Arrays.asList(GENE_01, GENE_02));
        LOCUS_02.setAuthorReportedGenes(Collections.singletonList(GENE_03));
        LOCUS_03.setAuthorReportedGenes(Collections.singletonList(GENE_04));
        LOCUS_04.setAuthorReportedGenes(Collections.singletonList(GENE_05));

        // Build association links
        BETA_SINGLE_ASSOCIATION.setLoci(Collections.singletonList(LOCUS_01));
        OR_MULTI_ASSOCIATION.setLoci(Collections.singletonList(LOCUS_02));
        OR_INTERACTION_ASSOCIATION.setLoci(Arrays.asList(LOCUS_03, LOCUS_04));
    }

    @Before
    public void setUp() throws Exception {
        singleSnpMultiSnpAssociationService = new SingleSnpMultiSnpAssociationService(associationRepository,
                                                                                      locusRepository,
                                                                                      genomicContextRepository,
                                                                                      lociAttributesService);
    }

    @Test
    public void testCreateSingleForm() throws Exception {
        assertThat(singleSnpMultiSnpAssociationService.createForm(BETA_SINGLE_ASSOCIATION)).isInstanceOf(
                SnpAssociationStandardMultiForm.class);

        SnpAssociationStandardMultiForm form =
                (SnpAssociationStandardMultiForm) singleSnpMultiSnpAssociationService.createForm(BETA_SINGLE_ASSOCIATION);

        // Check values we would expect in form
        assertThat(form.getAssociationId()).as("Check form ID").isEqualTo(BETA_SINGLE_ASSOCIATION.getId());
        assertThat(form.getBetaDirection()).as("Check form BETA DIRECTION")
                .isEqualTo(BETA_SINGLE_ASSOCIATION.getBetaDirection());
        assertThat(form.getBetaUnit()).as("Check form BETA UNIT").isEqualTo(BETA_SINGLE_ASSOCIATION.getBetaUnit());
        assertThat(form.getBetaNum()).as("Check form BETA NUM").isEqualTo(BETA_SINGLE_ASSOCIATION.getBetaNum());
        assertThat(form.getSnpType()).as("Check form SNP TYPE").isEqualTo(BETA_SINGLE_ASSOCIATION.getSnpType());
        assertThat(form.getMultiSnpHaplotype()).as("Check form MULTI SNP HAPLOTYPE")
                .isEqualTo(BETA_SINGLE_ASSOCIATION.getMultiSnpHaplotype());
        assertThat(form.getSnpApproved()).as("Check form SNP APPROVED")
                .isEqualTo(BETA_SINGLE_ASSOCIATION.getSnpApproved());
        assertThat(form.getPvalueExponent()).as("Check form PVALUE EXPONENT")
                .isEqualTo(BETA_SINGLE_ASSOCIATION.getPvalueExponent());
        assertThat(form.getPvalueMantissa()).as("Check form PVALUE MANTISSA")
                .isEqualTo(BETA_SINGLE_ASSOCIATION.getPvalueMantissa());
        assertThat(form.getStandardError()).as("Check form STANDARD ERROR")
                .isEqualTo(BETA_SINGLE_ASSOCIATION.getStandardError());
        assertThat(form.getRange()).as("Check form RANGE").isEqualTo(BETA_SINGLE_ASSOCIATION.getRange());
        assertThat(form.getPvalueDescription()).as("Check form PVALUE DESCRIPTION")
                .isEqualTo(BETA_SINGLE_ASSOCIATION.getPvalueDescription());
        assertThat(form.getRiskFrequency()).as("Check form RISK FREQUENCY")
                .isEqualTo(BETA_SINGLE_ASSOCIATION.getRiskFrequency());
        assertThat(form.getDescription()).as("Check form DESCRIPTION")
                .isEqualTo(BETA_SINGLE_ASSOCIATION.getDescription());

        // Check EFO traits
        assertThat(form.getEfoTraits()).extracting("id", "trait", "uri")
                .contains(tuple(988L, "atrophic rhinitis", "http://www.ebi.ac.uk/efo/EFO_0007159"),
                          tuple(989L, "HeLa", "http://www.ebi.ac.uk/efo/EFO_0001185"));

        // Check null values
        assertNull(form.getOrPerCopyNum());
        assertNull(form.getOrPerCopyRecip());
        assertNull(form.getOrPerCopyRecipRange());
        assertNull(form.getMultiSnpHaplotypeNum());

        // Test locus attributes
        assertThat(form.getMultiSnpHaplotypeDescr()).as("Check form MULTI HAPLOTYPE DESCRIPTION")
                .isEqualTo("Single variant");
        assertThat(form.getAuthorReportedGenes()).isInstanceOf(Collection.class);
        assertThat(form.getAuthorReportedGenes()).contains("NEGR1", "FRS2");


        // Test the row values
        Collection<SnpFormRow> rows = form.getSnpFormRows();
        assertThat(rows).hasSize(1);
        assertThat(rows).extracting("snp", "strongestRiskAllele")
                .containsExactly(tuple("rs579459", "rs579459-?"));

        assertThat(rows).extracting("proxySnps").isNotEmpty();
        List<String> proxyNames = new ArrayList<String>();
        for (SnpFormRow row : rows) {
            proxyNames.addAll(row.getProxySnps());
        }

        assertThat(proxyNames).containsOnlyOnce("rs6538678");
    }
}