package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.curation.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.curation.builder.EfoTraitBuilder;
import uk.ac.ebi.spot.goci.curation.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.GenomicContextRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

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


    private static final EfoTrait EFO1 = new EfoTraitBuilder()
            .setId(988L)
            .setTrait("atrophic rhinitis")
            .setUri("http://www.ebi.ac.uk/efo/EFO_0007159")
            .build();

    private static final EfoTrait EFO2 = new EfoTraitBuilder()
            .setId(989L)
            .setTrait("HeLa")
            .setUri("http://www.ebi.ac.uk/efo/EFO_0001185")
            .build();

    private static final Study STUDY =
            new StudyBuilder().setId(802L)
                    .setEfoTraits(Arrays.asList(EFO1, EFO2))
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
                    .setAssociationReport(null)
                    .setEfoTraits(Arrays.asList(EFO1, EFO2))
                    .setStudy(STUDY)
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

    }

    @Test
    public void testCreateAssociation() throws Exception {

    }
}