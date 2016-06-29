package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.builder.EfoTraitBuilder;
import uk.ac.ebi.spot.goci.builder.GeneBuilder;
import uk.ac.ebi.spot.goci.builder.LocationBuilder;
import uk.ac.ebi.spot.goci.builder.LocusBuilder;
import uk.ac.ebi.spot.goci.builder.RegionBuilder;
import uk.ac.ebi.spot.goci.builder.RiskAlleleBuilder;
import uk.ac.ebi.spot.goci.builder.SingleNucleotidePolymorphismBuilder;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

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
    private GenomicContextRepository genomicContextRepository;

    @Mock
    private LociAttributesService lociAttributesService;

    private SnpAssociationFormService snpAssociationFormService;

    // Entity objects
    private static EfoTrait EFO_01 = new EfoTraitBuilder()
            .setId(988L)
            .setTrait("atrophic rhinitis")
            .setUri("http://www.ebi.ac.uk/efo/EFO_0007159")
            .build();

    private static EfoTrait EFO_02 = new EfoTraitBuilder()
            .setId(989L)
            .setTrait("HeLa")
            .setUri("http://www.ebi.ac.uk/efo/EFO_0001185")
            .build();

    private static Gene GENE_01 = new GeneBuilder().setId(112L).setGeneName("NEGR1").build();

    private static Gene GENE_02 = new GeneBuilder().setId(113L).setGeneName("FRS2").build();

    private static Gene GENE_03 = new GeneBuilder().setId(113L).setGeneName("ELF1").build();

    private static Region REGION_01 = new RegionBuilder().setId(897L).setName("9q33.1").build();

    private static Location LOCATION_01 =
            new LocationBuilder().setId(654L).setChromosomeName("1").setChromosomePosition("159001296").build();

    private static SingleNucleotidePolymorphism
            PROXY_SNP_01 = new SingleNucleotidePolymorphismBuilder().setId(311L)
            .setLastUpdateDate(new Date())
            .setRsId("rs6538678")
            .build();

    private static SingleNucleotidePolymorphism
            PROXY_SNP_02 = new SingleNucleotidePolymorphismBuilder().setId(311L)
            .setLastUpdateDate(new Date())
            .setRsId("rs7329174")
            .build();

    private static SingleNucleotidePolymorphism
            PROXY_SNP_03 = new SingleNucleotidePolymorphismBuilder().setId(311L)
            .setLastUpdateDate(new Date())
            .setRsId("rs1234567")
            .build();

    private static SingleNucleotidePolymorphism SNP_01 = new SingleNucleotidePolymorphismBuilder().setId(311L)
            .setLastUpdateDate(new Date())
            .setRsId("rs579459")
            .build();

    private static SingleNucleotidePolymorphism SNP_02 = new SingleNucleotidePolymorphismBuilder().setId(321L)
            .setLastUpdateDate(new Date())
            .setRsId("rs9533090")
            .build();

    private static SingleNucleotidePolymorphism SNP_03 = new SingleNucleotidePolymorphismBuilder().setId(391L)
            .setLastUpdateDate(new Date())
            .setRsId("rs114205691")
            .build();

    private static RiskAllele RISK_ALLELE_01 = new RiskAlleleBuilder().setId(411L)
            .setRiskAlleleName("rs579459-?")
            .build();

    private static RiskAllele RISK_ALLELE_02 = new RiskAlleleBuilder().setId(412L)
            .setRiskAlleleName("rs9533090-?")
            .build();

    private static RiskAllele RISK_ALLELE_03 = new RiskAlleleBuilder().setId(413L)
            .setRiskAlleleName("rs114205691-?")
            .build();

    private static Locus LOCUS_01 =
            new LocusBuilder().setId(111L)
                    .setDescription("Single variant")
                    .build();

    private static Locus LOCUS_02 =
            new LocusBuilder().setId(121L)
                    .setDescription("2-SNP haplotype")
                    .setHaplotypeSnpCount(2)
                    .build();

    private static Association BETA_SINGLE_ASSOCIATION =
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

        // Set risk allele snp
        RISK_ALLELE_01.setSnp(SNP_01);
        RISK_ALLELE_02.setSnp(SNP_02);
        RISK_ALLELE_03.setSnp(SNP_03);

        // Set risk allele proxy snp
        RISK_ALLELE_01.setProxySnps(Collections.singletonList(PROXY_SNP_01));
        RISK_ALLELE_02.setProxySnps(Collections.singletonList(PROXY_SNP_02));
        RISK_ALLELE_03.setProxySnps(Collections.singletonList(PROXY_SNP_03));

        // Set locus risk allele
        LOCUS_01.setStrongestRiskAlleles(Collections.singletonList(RISK_ALLELE_01));
        LOCUS_02.setStrongestRiskAlleles(Arrays.asList(RISK_ALLELE_02, RISK_ALLELE_03));

        // Set Locus genes
        LOCUS_01.setAuthorReportedGenes(Arrays.asList(GENE_01, GENE_02));
        LOCUS_02.setAuthorReportedGenes(Collections.singletonList(GENE_03));

        // Build association links
        BETA_SINGLE_ASSOCIATION.setLoci(Collections.singletonList(LOCUS_01));
        OR_MULTI_ASSOCIATION.setLoci(Collections.singletonList(LOCUS_02));
    }

    @Before
    public void setUp() throws Exception {
        snpAssociationFormService = new SingleSnpMultiSnpAssociationService(
                                                                            genomicContextRepository,
                                                                            lociAttributesService);
    }

    @Test
    public void testCreateSingleForm() throws Exception {
        assertThat(snpAssociationFormService.createForm(BETA_SINGLE_ASSOCIATION)).isInstanceOf(
                SnpAssociationStandardMultiForm.class);

        SnpAssociationStandardMultiForm form =
                (SnpAssociationStandardMultiForm) snpAssociationFormService.createForm(BETA_SINGLE_ASSOCIATION);

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
        assertThat(rows).extracting("snp", "strongestRiskAllele", "proxySnps")
                .containsExactly(tuple("rs579459", "rs579459-?", Collections.singletonList("rs6538678")));
    }

    @Test
    public void testCreateMultiForm() throws Exception {
        assertThat(snpAssociationFormService.createForm(OR_MULTI_ASSOCIATION)).isInstanceOf(
                SnpAssociationStandardMultiForm.class);

        SnpAssociationStandardMultiForm form =
                (SnpAssociationStandardMultiForm) snpAssociationFormService.createForm(OR_MULTI_ASSOCIATION);

        // Check values we would expect in form
        assertThat(form.getAssociationId()).as("Check form ID").isEqualTo(OR_MULTI_ASSOCIATION.getId());
        assertThat(form.getSnpType()).as("Check form SNP TYPE").isEqualTo(OR_MULTI_ASSOCIATION.getSnpType());
        assertThat(form.getMultiSnpHaplotype()).as("Check form MULTI SNP HAPLOTYPE")
                .isEqualTo(OR_MULTI_ASSOCIATION.getMultiSnpHaplotype());
        assertThat(form.getSnpApproved()).as("Check form SNP APPROVED")
                .isEqualTo(OR_MULTI_ASSOCIATION.getSnpApproved());
        assertThat(form.getPvalueExponent()).as("Check form PVALUE EXPONENT")
                .isEqualTo(OR_MULTI_ASSOCIATION.getPvalueExponent());
        assertThat(form.getPvalueMantissa()).as("Check form PVALUE MANTISSA")
                .isEqualTo(OR_MULTI_ASSOCIATION.getPvalueMantissa());
        assertThat(form.getStandardError()).as("Check form STANDARD ERROR")
                .isEqualTo(OR_MULTI_ASSOCIATION.getStandardError());
        assertThat(form.getRange()).as("Check form RANGE").isEqualTo(OR_MULTI_ASSOCIATION.getRange());
        assertThat(form.getPvalueDescription()).as("Check form PVALUE DESCRIPTION")
                .isEqualTo(OR_MULTI_ASSOCIATION.getPvalueDescription());
        assertThat(form.getRiskFrequency()).as("Check form RISK FREQUENCY")
                .isEqualTo(OR_MULTI_ASSOCIATION.getRiskFrequency());
        assertThat(form.getDescription()).as("Check form DESCRIPTION")
                .isEqualTo(OR_MULTI_ASSOCIATION.getDescription());

        // Check EFO traits
        assertThat(form.getEfoTraits()).extracting("id", "trait", "uri")
                .contains(tuple(988L, "atrophic rhinitis", "http://www.ebi.ac.uk/efo/EFO_0007159"),
                          tuple(989L, "HeLa", "http://www.ebi.ac.uk/efo/EFO_0001185"));

        // Check null values
        assertNull(form.getBetaDirection());
        assertNull(form.getBetaNum());
        assertNull(form.getBetaUnit());

        // Test locus attributes
        assertThat(form.getMultiSnpHaplotypeDescr()).as("Check form MULTI HAPLOTYPE DESCRIPTION")
                .isEqualTo("2-SNP haplotype");
        assertThat(form.getMultiSnpHaplotypeNum()).as("Check form MULTI HAPLOTYPE NUMBER")
                .isEqualTo(2);
        assertThat(form.getAuthorReportedGenes()).isInstanceOf(Collection.class);
        assertThat(form.getAuthorReportedGenes()).containsOnly("ELF1");

        // Test the row values
        Collection<SnpFormRow> rows = form.getSnpFormRows();
        assertThat(rows).hasSize(2);
        assertThat(rows).extracting("snp",
                                    "strongestRiskAllele",
                                    "proxySnps")
                .contains(tuple("rs9533090",
                                "rs9533090-?",
                                Collections.singletonList("rs7329174")),
                          tuple("rs114205691",
                                "rs114205691-?",
                                Collections.singletonList("rs1234567")));
    }
}