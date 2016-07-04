package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.curation.validator.SnpFormColumnValidator;
import uk.ac.ebi.spot.goci.curation.validator.SnpFormRowValidator;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.repository.AssociationReportRepository;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;
import uk.ac.ebi.spot.goci.service.MappingService;
import uk.ac.ebi.spot.goci.service.ValidationService;

import static org.junit.Assert.assertEquals;

/**
 * Created by emma on 03/03/2016.
 *
 * @author emma
 *         <p>
 *         Test class for AssociationOperationsService.java
 */
@RunWith(MockitoJUnitRunner.class)
public class AssociationOperationsServiceTest {

    private static final Association ASS_BETA =
            new AssociationBuilder().setId(600L).setBetaNum((float) 0.012).build();

    private static final Association ASS_OR =
            new AssociationBuilder().setId(601L).setOrPerCopyNum((float) 5.97).build();

    private static final Association ASS_NO_EFFECT_SIZE =
            new AssociationBuilder().setId(602L).build();

    private AssociationOperationsService associationOperationsService;

    @Mock
    private SingleSnpMultiSnpAssociationService singleSnpMultiSnpAssociationService;

    @Mock
    private SnpInteractionAssociationService snpInteractionAssociationService;

    @Mock
    private AssociationReportRepository associationReportRepository;

    @Mock
    private AssociationRepository associationRepository;

    @Mock
    private SnpFormRowValidator snpFormRowValidator;

    @Mock
    private SnpFormColumnValidator snpFormColumnValidator;

    @Mock
    private MappingService mappingService;

    @Mock
    private LocusRepository locusRepository;

    @Mock
    private LociAttributesService lociAttributesService;

    @Mock
    private AssociationValidationReportService associationValidationReportService;

    @Mock
    private ValidationService validationService;


    @Before
    public void setUpMock() {
        associationOperationsService = new AssociationOperationsService(singleSnpMultiSnpAssociationService,
                                                                        snpInteractionAssociationService,
                                                                        associationReportRepository,
                                                                        associationRepository,
                                                                        locusRepository,
                                                                        snpFormRowValidator,
                                                                        snpFormColumnValidator,
                                                                        mappingService,
                                                                        lociAttributesService,
                                                                        validationService,
                                                                        associationValidationReportService);
    }

    @Test
    public void testDetermineIfAssociationIsOrType() {
        assertEquals("beta", associationOperationsService.determineIfAssociationIsOrType(ASS_BETA));
        assertEquals("or", associationOperationsService.determineIfAssociationIsOrType(ASS_OR));
        assertEquals("none", associationOperationsService.determineIfAssociationIsOrType(ASS_NO_EFFECT_SIZE));
    }
}
