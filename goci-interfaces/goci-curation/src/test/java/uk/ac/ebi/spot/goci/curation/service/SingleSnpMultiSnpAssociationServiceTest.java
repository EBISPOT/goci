package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.curation.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.curation.builder.EfoTraitBuilder;
import uk.ac.ebi.spot.goci.curation.builder.GeneBuilder;
import uk.ac.ebi.spot.goci.curation.builder.LocusBuilder;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationStandardMultiForm;
import uk.ac.ebi.spot.goci.curation.model.batchloader.BatchUploadRow;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.GenomicContextRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static com.google.common.collect.Lists.newLinkedList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.util.Lists.newArrayList;
import static org.junit.Assert.assertNotNull;
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

    private static final Locus LOCUS_01 =
            new LocusBuilder().setId(111L)
                    .setDescription("Single variant")
                    .setAuthorReportedGenes(Arrays.asList(GENE_01, GENE_02))
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
                    .setOrPerCopyNum(null)
                    .setOrPerCopyRecip(null)
                    .setOrPerCopyRecipRange(null)
                    .setPvalueDescription("(ferritin)")
                    .setRiskFrequency(String.valueOf(0.93))
                    .setEfoTraits(Arrays.asList(EFO_01, EFO_02))
                    .setDescription("this is a test")
                    .setLoci(Collections.singletonList(LOCUS_01))
                    .build();

    @Before
    public void setUp() throws Exception {
        singleSnpMultiSnpAssociationService = new SingleSnpMultiSnpAssociationService(associationRepository,
                                                                                      locusRepository,
                                                                                      genomicContextRepository,
                                                                                      lociAttributesService);
    }

    @Test
    public void testMocks() {
        // Test mock creation
        assertNotNull(associationRepository);
        assertNotNull(locusRepository);
        assertNotNull(genomicContextRepository);
        assertNotNull(lociAttributesService);
    }

    @Test
    public void testCreateForm() throws Exception {
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

        //todo test how SNP, genes and risk allele appear in form
        // Test locus attributes
        assertThat(form.getMultiSnpHaplotypeDescr()).as("Check form MULTI HAPLOTYPE DESCRIPTION")
                .isEqualTo("Single variant");


        assertThat(form.getAuthorReportedGenes()).isInstanceOf(Collection.class);
        assertThat(form.getAuthorReportedGenes()).contains("NEGR1", "FRS2");

    }

    @Test
    public void testCreateAssociation() throws Exception {

    }
}