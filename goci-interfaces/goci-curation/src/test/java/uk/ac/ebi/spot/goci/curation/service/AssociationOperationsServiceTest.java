package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.builder.AssociationReportBuilder;
import uk.ac.ebi.spot.goci.builder.CuratorBuilder;
import uk.ac.ebi.spot.goci.builder.HousekeepingBuilder;
import uk.ac.ebi.spot.goci.builder.SecureUserBuilder;
import uk.ac.ebi.spot.goci.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.builder.ValidationErrorBuilder;
import uk.ac.ebi.spot.goci.curation.builder.SnpAssociationInteractionFormBuilder;
import uk.ac.ebi.spot.goci.curation.builder.SnpAssociationStandardMultiFormBuilder;
import uk.ac.ebi.spot.goci.curation.builder.SnpFormColumnBuilder;
import uk.ac.ebi.spot.goci.curation.builder.SnpFormRowBuilder;
import uk.ac.ebi.spot.goci.curation.model.AssociationValidationView;
import uk.ac.ebi.spot.goci.curation.model.LastViewedAssociation;
import uk.ac.ebi.spot.goci.curation.model.MappingDetails;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationInteractionForm;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationStandardMultiForm;
import uk.ac.ebi.spot.goci.curation.model.SnpFormColumn;
import uk.ac.ebi.spot.goci.curation.model.SnpFormRow;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationReport;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.ValidationError;
import uk.ac.ebi.spot.goci.repository.AssociationReportRepository;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;
import uk.ac.ebi.spot.goci.service.AssociationTrackingOperationServiceImpl;
import uk.ac.ebi.spot.goci.service.ErrorCreationService;
import uk.ac.ebi.spot.goci.service.MappingService;
import uk.ac.ebi.spot.goci.service.ValidationService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 15/07/2016.
 *
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class AssociationOperationsServiceTest {

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
    private MappingService mappingService;

    @Mock
    private LocusRepository locusRepository;

    @Mock
    private LociAttributesService lociAttributesService;

    @Mock
    private AssociationValidationReportService associationValidationReportService;

    @Mock
    private ValidationService validationService;

    @Mock
    private ErrorCreationService errorCreationService;

    @Mock
    private AssociationTrackingOperationServiceImpl associationTrackingOperationService;

    @Mock
    private AssociationMappingErrorService associationMappingErrorService;

    private static final Association ASS_BETA =
            new AssociationBuilder().setId(600L).setBetaNum((float) 0.012).build();

    private static final Association ASS_OR =
            new AssociationBuilder().setId(601L).setOrPerCopyNum((float) 5.97).build();

    private static final Association ASS_NO_EFFECT_SIZE =
            new AssociationBuilder().setId(602L).build();

    private static final SnpFormRow SNP_FORM_ROW_ERROR_1 =
            new SnpFormRowBuilder().setSnp("").setStrongestRiskAllele("").build();

    private static final SnpFormRow SNP_FORM_ROW_ERROR_2 =
            new SnpFormRowBuilder().setSnp(null).setStrongestRiskAllele(null).build();

    private static final SnpFormRow SNP_FORM_ROW_ERROR_3 =
            new SnpFormRowBuilder().setSnp("rs123").setStrongestRiskAllele("rs123-?").build();

    private static final SnpFormRow SNP_FORM_ROW_ERROR_4 =
            new SnpFormRowBuilder().setSnp("rs456").setStrongestRiskAllele("rs456-?").build();

    private static final SnpFormColumn SNP_FORM_COLUMN_01 =
            new SnpFormColumnBuilder().setSnp("").setStrongestRiskAllele("").build();

    private static final SnpFormColumn SNP_FORM_COLUMN_02 =
            new SnpFormColumnBuilder().setSnp(null).setStrongestRiskAllele(null).build();

    private static final SnpFormColumn SNP_FORM_COLUMN_03 =
            new SnpFormColumnBuilder().setSnp("rs123").setStrongestRiskAllele("rs123-?").build();

    private static final SnpFormColumn SNP_FORM_COLUMN_04 =
            new SnpFormColumnBuilder().setSnp("rs456").setStrongestRiskAllele("rs456-?").build();


    private static final SnpAssociationStandardMultiForm SNP_ASSOCIATION_STANDARD_MULTI_FORM_WITH_ERRORS =
            new SnpAssociationStandardMultiFormBuilder().setSnpFormRows(Arrays.asList(SNP_FORM_ROW_ERROR_1,
                                                                                      SNP_FORM_ROW_ERROR_2))
                    .build();

    private static final SnpAssociationStandardMultiForm SNP_ASSOCIATION_STANDARD_MULTI_FORM =
            new SnpAssociationStandardMultiFormBuilder().setSnpFormRows(Arrays.asList(SNP_FORM_ROW_ERROR_3,
                                                                                      SNP_FORM_ROW_ERROR_4))
                    .build();

    private static final SnpAssociationInteractionForm SNP_ASSOCIATION_INTERACTION_FORM_WITH_ERRORS =
            new SnpAssociationInteractionFormBuilder().setSnpFormColumns(Arrays.asList(SNP_FORM_COLUMN_01,
                                                                                       SNP_FORM_COLUMN_02))
                    .build();

    private static final SnpAssociationInteractionForm SNP_ASSOCIATION_INTERACTION_FORM =
            new SnpAssociationInteractionFormBuilder().setSnpFormColumns(Arrays.asList(SNP_FORM_COLUMN_03,
                                                                                       SNP_FORM_COLUMN_04))
                    .build();

    private static final ValidationError SNP_ERROR =
            new ValidationErrorBuilder().setError("ERROR").setField("SNP").build();
    private static final ValidationError RISK_ALLELE_ERROR =
            new ValidationErrorBuilder().setError("ERROR").setField("SNP").build();
    private static final ValidationError OR_ERROR =
            new ValidationErrorBuilder().setError("ERROR").setField("OR").build();

    private static final ValidationError SNP_NO_ERROR =
            new ValidationErrorBuilder().setField("SNP").build();
    private static final ValidationError RISK_ALLELE_NO_ERROR =
            new ValidationErrorBuilder().setField("SNP").build();
    private static final ValidationError OR_NO_ERROR =
            new ValidationErrorBuilder().setField("OR").build();

    private static final ValidationError WARNING =
            new ValidationErrorBuilder().setField("SNP").setWarning(true).setError("WARNING").build();

    private static final Association ASS_MULTI = new AssociationBuilder().setMultiSnpHaplotype(true).build();

    private static final Association ASS_INTER =
            new AssociationBuilder().setId((long) 100)
                    .setSnpInteraction(true)
                    .setLastMappingPerformedBy("test")
                    .setLastMappingDate(new Date())
                    .build();

    private static final Association ASS_INTER_EDITED =
            new AssociationBuilder()
                    .setSnpInteraction(true)
                    .setLastMappingPerformedBy("test")
                    .setLastMappingDate(new Date())
                    .build();

    private static final AssociationReport ASSOCIATION_REPORT = new AssociationReportBuilder().build();

    private static final Association ASS_APPROVE_UNAPPROVE =
            new AssociationBuilder().setAssociationReport(ASSOCIATION_REPORT).build();

    private static final SecureUser USER = new SecureUserBuilder().build();

    private static final Curator CURATOR = new CuratorBuilder().setLastName("test").build();

    private static final Housekeeping HOUSEKEEPING = new HousekeepingBuilder().setCurator(CURATOR).build();

    private static final Study STUDY = new StudyBuilder().setHousekeeping(HOUSEKEEPING).build();

    @Before
    public void setUpMock() {
        associationOperationsService = new AssociationOperationsService(singleSnpMultiSnpAssociationService,
                                                                        snpInteractionAssociationService,
                                                                        associationReportRepository,
                                                                        associationRepository,
                                                                        locusRepository,
                                                                        mappingService,
                                                                        lociAttributesService,
                                                                        validationService,
                                                                        associationValidationReportService,
                                                                        errorCreationService,
                                                                        associationTrackingOperationService,
                                                                        associationMappingErrorService);
    }

    @Test
    public void checkSnpAssociationFormErrorsFormWithErrors() throws Exception {
        when(errorCreationService.checkSnpValueIsPresent(Matchers.anyString())).thenReturn(SNP_ERROR);
        when(errorCreationService.checkStrongestAlleleValueIsPresent(Matchers.anyString())).thenReturn(RISK_ALLELE_ERROR);
        when(errorCreationService.checkOrIsPresent(Matchers.anyFloat())).thenReturn(OR_ERROR);

        assertThat(associationOperationsService.checkSnpAssociationFormErrors(
                SNP_ASSOCIATION_STANDARD_MULTI_FORM_WITH_ERRORS,
                "or")).isInstanceOf(List.class)
                .hasOnlyElementsOfType(
                        AssociationValidationView.class)
                .hasSize(5);
    }

    @Test
    public void checkSnpAssociationFormErrorsFormWitNoErrors() throws Exception {
        when(errorCreationService.checkSnpValueIsPresent(Matchers.anyString())).thenReturn(SNP_NO_ERROR);
        when(errorCreationService.checkStrongestAlleleValueIsPresent(Matchers.anyString())).thenReturn(
                RISK_ALLELE_NO_ERROR);
        when(errorCreationService.checkOrIsPresent(Matchers.anyFloat())).thenReturn(OR_NO_ERROR);

        assertThat(associationOperationsService.checkSnpAssociationFormErrors(
                SNP_ASSOCIATION_STANDARD_MULTI_FORM,
                "or")).isInstanceOf(List.class)
                .hasOnlyElementsOfType(
                        AssociationValidationView.class)
                .hasSize(0);
    }

    @Test
    public void checkSnpAssociationInteractionFormErrorsWithErrors() throws Exception {
        when(errorCreationService.checkSnpValueIsPresent(Matchers.anyString())).thenReturn(SNP_ERROR);
        when(errorCreationService.checkStrongestAlleleValueIsPresent(Matchers.anyString())).thenReturn(RISK_ALLELE_ERROR);
        when(errorCreationService.checkOrIsPresent(Matchers.anyFloat())).thenReturn(OR_ERROR);

        assertThat(associationOperationsService.checkSnpAssociationInteractionFormErrors(
                SNP_ASSOCIATION_INTERACTION_FORM_WITH_ERRORS,
                "or")).isInstanceOf(List.class)
                .hasOnlyElementsOfType(
                        AssociationValidationView.class)
                .hasSize(5);
    }

    @Test
    public void checkSnpAssociationInteractionFormErrorsWithNoErrors() throws Exception {
        when(errorCreationService.checkSnpValueIsPresent(Matchers.anyString())).thenReturn(SNP_NO_ERROR);
        when(errorCreationService.checkStrongestAlleleValueIsPresent(Matchers.anyString())).thenReturn(
                RISK_ALLELE_NO_ERROR);
        when(errorCreationService.checkOrIsPresent(Matchers.anyFloat())).thenReturn(OR_NO_ERROR);

        assertThat(associationOperationsService.checkSnpAssociationInteractionFormErrors(
                SNP_ASSOCIATION_INTERACTION_FORM,
                "or")).isInstanceOf(List.class)
                .hasOnlyElementsOfType(
                        AssociationValidationView.class)
                .hasSize(0);
    }

    @Test
    public void saveAssociationCreatedFromFormAssociationWithErrors() throws Exception {
        when(validationService.runAssociationValidation(ASS_MULTI, "full")).thenReturn(Collections.singleton(OR_ERROR));
        assertThat(associationOperationsService.saveAssociationCreatedFromForm(STUDY, ASS_MULTI, USER)).isInstanceOf(
                List.class)
                .hasOnlyElementsOfType(
                        AssociationValidationView.class)
                .hasSize(1);

        verifyZeroInteractions(associationTrackingOperationService);
        verifyZeroInteractions(associationRepository);
        verifyZeroInteractions(associationValidationReportService);
        verifyZeroInteractions(mappingService);
    }

    @Test
    public void saveAssociationCreatedFromFormAssociationNoErrors() throws Exception {
        when(validationService.runAssociationValidation(ASS_INTER, "full")).thenReturn(Collections.singleton(WARNING));
        assertThat(associationOperationsService.saveAssociationCreatedFromForm(STUDY, ASS_INTER, USER)).isInstanceOf(
                List.class)
                .hasOnlyElementsOfType(
                        AssociationValidationView.class)
                .hasSize(1);

        verify(associationTrackingOperationService, times(1)).create(ASS_INTER, USER);
        verify(associationRepository, times(1)).save(ASS_INTER);
        verify(associationValidationReportService, times(1)).createAssociationValidationReport(Collections.singleton(
                WARNING), ASS_INTER.getId());
        verify(mappingService, times(1)).validateAndMapAssociation(ASS_INTER, CURATOR.getLastName(), USER);
    }

    @Test
    public void saveEditedAssociationFromFormAssociationWithErrors() throws Exception {
        when(validationService.runAssociationValidation(ASS_MULTI, "full")).thenReturn(Collections.singleton(OR_ERROR));
        assertThat(associationOperationsService.saveEditedAssociationFromForm(STUDY,
                                                                              ASS_MULTI,
                                                                              (long) 100,
                                                                              USER)).isInstanceOf(
                List.class)
                .hasOnlyElementsOfType(
                        AssociationValidationView.class)
                .hasSize(1);

        verifyZeroInteractions(lociAttributesService);
        verifyZeroInteractions(associationRepository);
        verifyZeroInteractions(associationTrackingOperationService);
        verifyZeroInteractions(associationValidationReportService);
        verifyZeroInteractions(mappingService);
    }

    @Test
    public void saveEditedAssociationFromFormAssociationNoErrors() throws Exception {
        when(validationService.runAssociationValidation(ASS_INTER_EDITED, "full")).thenReturn(Collections.singleton(
                WARNING));
        when(associationRepository.findOne(Matchers.anyLong())).thenReturn(ASS_INTER);

        assertThat(associationOperationsService.saveEditedAssociationFromForm(STUDY,
                                                                              ASS_INTER_EDITED,
                                                                              (long) 100,
                                                                              USER)).isInstanceOf(
                List.class)
                .hasOnlyElementsOfType(
                        AssociationValidationView.class)
                .hasSize(1);

        verify(associationRepository, times(1)).findOne((long) 100);
        verify(lociAttributesService, times(1)).deleteLocusAndRiskAlleles(ASS_INTER);
        verify(associationTrackingOperationService, times(1)).update(ASS_INTER_EDITED, USER,
                                                                     "ASSOCIATION_UPDATE");
        verify(associationRepository, times(1)).save(ASS_INTER_EDITED);
        verify(associationValidationReportService, times(1)).createAssociationValidationReport(Collections.singleton(
                WARNING), ASS_INTER_EDITED.getId());
        verify(mappingService, times(1)).validateAndMapAssociation(ASS_INTER_EDITED, CURATOR.getLastName(), USER);
    }

    @Test
    public void saveAssociation() throws Exception {
        associationOperationsService.saveAssociation(ASS_MULTI, STUDY, Collections.singletonList(WARNING));
        verify(associationRepository, times(1)).save(ASS_MULTI);
        verify(associationValidationReportService,
               times(1)).createAssociationValidationReport(Collections.singletonList(WARNING), ASS_MULTI.getId());
    }

    @Test
    public void runMapping() throws Exception {
        associationOperationsService.runMapping(CURATOR, ASS_MULTI, USER);
        verify(mappingService, times(1)).validateAndMapAssociation(ASS_MULTI, "test", USER);
    }

    @Test
    public void determineIfAssociationIsOrType() throws Exception {
        assertEquals("beta", associationOperationsService.determineIfAssociationIsOrType(ASS_BETA));
        assertEquals("or", associationOperationsService.determineIfAssociationIsOrType(ASS_OR));
        assertEquals("none", associationOperationsService.determineIfAssociationIsOrType(ASS_NO_EFFECT_SIZE));
    }

    @Test
    public void generateForm() throws Exception {
        when(singleSnpMultiSnpAssociationService.createForm(ASS_MULTI)).thenReturn(SNP_ASSOCIATION_STANDARD_MULTI_FORM);
        assertThat(associationOperationsService.generateForm(ASS_MULTI)).isInstanceOf(SnpAssociationStandardMultiForm.class);

        when(snpInteractionAssociationService.createForm(ASS_INTER)).thenReturn(SNP_ASSOCIATION_INTERACTION_FORM);
        assertThat(associationOperationsService.generateForm(ASS_INTER)).isInstanceOf(SnpAssociationInteractionForm.class);
    }

    @Test
    public void createMappingDetails() throws Exception {
        MappingDetails mappingDetails = associationOperationsService.createMappingDetails(ASS_INTER);
        assertThat(mappingDetails).isInstanceOf(MappingDetails.class);
        assertThat(mappingDetails.getMappingDate()).isToday();
        assertThat(mappingDetails.getPerformer()).isEqualTo("test");
    }

    @Test
    public void getLastViewedAssociation() throws Exception {
        assertThat(associationOperationsService.getLastViewedAssociation((long) 100)).isInstanceOf(
                LastViewedAssociation.class).extracting("id").containsOnly((long) 100);
    }

    @Test
    public void approveAssociation() throws Exception {
        associationOperationsService.approveAssociation(ASS_APPROVE_UNAPPROVE, USER);
        verify(associationTrackingOperationService, times(1)).update(ASS_APPROVE_UNAPPROVE,
                                                                     USER,
                                                                     "ASSOCIATION_APPROVED");
        verify(associationRepository, times(1)).save(ASS_APPROVE_UNAPPROVE);
    }

    @Test
    public void unapproveAssociation() throws Exception {
        associationOperationsService.unapproveAssociation(ASS_APPROVE_UNAPPROVE, USER);
        verify(associationTrackingOperationService, times(1)).update(ASS_APPROVE_UNAPPROVE,
                                                                     USER,
                                                                     "ASSOCIATION_UNAPPROVED");
        verify(associationRepository, times(1)).save(ASS_APPROVE_UNAPPROVE);
    }

    @Test
    public void createAssociationCreationEvent() throws Exception {
        associationOperationsService.createAssociationCreationEvent(ASS_MULTI, USER);
        verify(associationTrackingOperationService, times(1)).create(ASS_MULTI, USER);
    }
}