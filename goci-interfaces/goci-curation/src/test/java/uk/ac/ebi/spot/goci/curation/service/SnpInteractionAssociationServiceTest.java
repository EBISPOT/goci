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
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationInteractionForm;
import uk.ac.ebi.spot.goci.curation.model.SnpFormColumn;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.Assert.assertNull;

/**
 * Created by emma on 08/04/2016.
 *
 * @author emma
 *         <p>
 *         Test for SnpInteractionAssociationServiceTest
 */
@RunWith(MockitoJUnitRunner.class)
public class SnpInteractionAssociationServiceTest {

    @Mock
    private AssociationRepository associationRepository;

    @Mock
    private LocusRepository locusRepository;

    @Mock
    private GenomicContextRepository genomicContextRepository;

    @Mock
    private LociAttributesService lociAttributesService;

    private SnpAssociationFormService snpAssociationFormService;

    // Entity objects
    private static EfoTrait EFO_01 = new EfoTraitBuilder()
            .setId(1001L)
            .setTrait("atrophic rhinitis")
            .setUri("http://www.ebi.ac.uk/efo/EFO_0007159")
            .build();

    private static EfoTrait EFO_02 = new EfoTraitBuilder()
            .setId(1002L)
            .setTrait("HeLa")
            .setUri("http://www.ebi.ac.uk/efo/EFO_0001185")
            .build();

    private static Gene GENE_04 = new GeneBuilder().setId(113L).setGeneName("WBP4").build();

    private static Gene GENE_05 = new GeneBuilder().setId(114L).setGeneName("SLC25A15").build();

    private static Region REGION_01 = new RegionBuilder().setId(655L).setName("9q33.1").build();

    private static Location LOCATION_01 =
            new LocationBuilder().setId(654L).setChromosomeName("1").setChromosomePosition("159001296").build();

    private static SingleNucleotidePolymorphism SNP_04 = new SingleNucleotidePolymorphismBuilder().setId(378L)
            .setLastUpdateDate(new Date())
            .setRsId("rs9788721")
            .build();

    private static SingleNucleotidePolymorphism SNP_05 = new SingleNucleotidePolymorphismBuilder().setId(356L)
            .setLastUpdateDate(new Date())
            .setRsId("rs8042374")
            .build();

    private static RiskAllele RISK_ALLELE_04 = new RiskAlleleBuilder().setId(414L)
            .setRiskAlleleName("rs9788721-?")
            .setGenomeWide(true)
            .setRiskFrequency(String.valueOf(0.6))
            .build();

    private static RiskAllele RISK_ALLELE_05 = new RiskAlleleBuilder().setId(451L)
            .setRiskAlleleName("rs8042374-?")
            .setGenomeWide(true)
            .setRiskFrequency(String.valueOf(0.4))
            .build();

    private static Locus LOCUS_03 =
            new LocusBuilder().setId(121L)
                    .setDescription("SNP x SNP interaction")
                    .build();

    private static Locus LOCUS_04 =
            new LocusBuilder().setId(131L)
                    .setDescription("SNP x SNP interaction")
                    .build();

    private static Association OR_INTERACTION_ASSOCIATION =
            new AssociationBuilder().setId((long) 103)
                    .setSnpType("novel")
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
        SNP_04.setLocations(Collections.singletonList(LOCATION_01));
        SNP_05.setLocations(Collections.singletonList(LOCATION_01));

        // Set snp risk alleles
        SNP_04.setRiskAlleles(Collections.singletonList(RISK_ALLELE_04));
        SNP_05.setRiskAlleles(Collections.singletonList(RISK_ALLELE_05));

        // Set risk allele snp
        RISK_ALLELE_04.setSnp(SNP_04);
        RISK_ALLELE_05.setSnp(SNP_05);

        // Set locus risk allele
        LOCUS_03.setStrongestRiskAlleles(Collections.singletonList(RISK_ALLELE_04));
        LOCUS_04.setStrongestRiskAlleles(Collections.singletonList(RISK_ALLELE_05));

        // Set Locus genes
        LOCUS_03.setAuthorReportedGenes(Collections.singletonList(GENE_04));
        LOCUS_04.setAuthorReportedGenes(Collections.singletonList(GENE_05));

        // Build association links
        OR_INTERACTION_ASSOCIATION.setLoci(Arrays.asList(LOCUS_03, LOCUS_04));
    }

    @Before
    public void setUp() throws Exception {
        snpAssociationFormService = new SnpInteractionAssociationService(associationRepository,
                                                                         genomicContextRepository,
                                                                         lociAttributesService);
    }

    @Test
    public void testCreateInteractionForm() throws Exception {

        assertThat(snpAssociationFormService.createForm(OR_INTERACTION_ASSOCIATION)).isInstanceOf(
                SnpAssociationInteractionForm.class);

        SnpAssociationInteractionForm form =
                (SnpAssociationInteractionForm) snpAssociationFormService.createForm(OR_INTERACTION_ASSOCIATION);

        // Check values we would expect in form
        assertThat(form.getAssociationId()).as("Check form ID").isEqualTo(OR_INTERACTION_ASSOCIATION.getId());
        assertThat(form.getSnpType()).as("Check form SNP TYPE").isEqualTo(OR_INTERACTION_ASSOCIATION.getSnpType());
        assertThat(form.getSnpApproved()).as("Check form SNP APPROVED")
                .isEqualTo(OR_INTERACTION_ASSOCIATION.getSnpApproved());
        assertThat(form.getPvalueExponent()).as("Check form PVALUE EXPONENT")
                .isEqualTo(OR_INTERACTION_ASSOCIATION.getPvalueExponent());
        assertThat(form.getPvalueMantissa()).as("Check form PVALUE MANTISSA")
                .isEqualTo(OR_INTERACTION_ASSOCIATION.getPvalueMantissa());
        assertThat(form.getStandardError()).as("Check form STANDARD ERROR")
                .isEqualTo(OR_INTERACTION_ASSOCIATION.getStandardError());
        assertThat(form.getRange()).as("Check form RANGE").isEqualTo(OR_INTERACTION_ASSOCIATION.getRange());
        assertThat(form.getPvalueDescription()).as("Check form PVALUE DESCRIPTION")
                .isEqualTo(OR_INTERACTION_ASSOCIATION.getPvalueDescription());
        assertThat(form.getRiskFrequency()).as("Check form RISK FREQUENCY")
                .isEqualTo(OR_INTERACTION_ASSOCIATION.getRiskFrequency());
        assertThat(form.getDescription()).as("Check form DESCRIPTION")
                .isEqualTo(OR_INTERACTION_ASSOCIATION.getDescription());

        // Check EFO traits
        assertThat(form.getEfoTraits()).extracting("id", "trait", "uri")
                .contains(tuple(1001L, "atrophic rhinitis", "http://www.ebi.ac.uk/efo/EFO_0007159"),
                          tuple(1002L, "HeLa", "http://www.ebi.ac.uk/efo/EFO_0001185"));

        // Check null values
        assertNull(form.getBetaNum());
        assertNull(form.getBetaUnit());
        assertNull(form.getBetaDirection());

        // Test locus attributes
        assertThat(form.getNumOfInteractions()).as("Check form NUMBER OF INTERACTIONS")
                .isEqualTo(2);

        // Test the column values
        Collection<SnpFormColumn> columns = form.getSnpFormColumns();
        assertThat(columns).hasSize(2);
        assertThat(columns).extracting("snp",
                                       "strongestRiskAllele",
                                       "riskFrequency",
                                       "authorReportedGenes",
                                       "proxySnps",
                                       "genomeWide",
                                       "limitedList")
                .contains(tuple("rs9788721",
                                "rs9788721-?",
                                "0.6",
                                Collections.singletonList("WBP4"),
                                Collections.emptyList(),
                                true,
                                false),
                          tuple("rs8042374",
                                "rs8042374-?",
                                "0.4",
                                Collections.singletonList("SLC25A15"),
                                Collections.emptyList(),
                                true,
                                false));
    }
}