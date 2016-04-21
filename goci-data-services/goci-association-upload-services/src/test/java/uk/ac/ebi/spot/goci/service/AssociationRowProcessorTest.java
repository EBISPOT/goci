package uk.ac.ebi.spot.goci.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.AssociationUploadRowBuilder;
import uk.ac.ebi.spot.goci.builder.GeneBuilder;
import uk.ac.ebi.spot.goci.builder.RiskAlleleBuilder;
import uk.ac.ebi.spot.goci.builder.SingleNucleotidePolymorphismBuilder;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.utils.AssociationCalculationService;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 14/04/2016.
 *
 * @author emma
 *         <p>
 *         Test for AssociationRowProcessor
 */
@RunWith(MockitoJUnitRunner.class)
public class AssociationRowProcessorTest {

    private AssociationRowProcessor associationRowProcessor;

    @Mock
    private AssociationCalculationService associationCalculationService;

    @Mock
    private AssociationAttributeService associationAttributeService;

    private static final AssociationUploadRow ROW_1 = new AssociationUploadRowBuilder().setRowNumber(1)
            .setSnp("rs123")
            .setAuthorReportedGene("SFRP1, ELF1")
            .setStrongestAllele("rs123-?")
            .setAssociationRiskFrequency("0.52")
            .setPvalueMantissa(2)
            .setPvalueExponent(-7)
            .setOrPerCopyNum((float) 1.22)
            .setRange("[0.82-0.92]")
            .setStandardError((float) 0.6)
            .build();


    private static final Gene GENE_01 = new GeneBuilder().setGeneName("SFRP1").build();

    private static final Gene GENE_02 = new GeneBuilder().setGeneName("ELF1").build();

    private static final SingleNucleotidePolymorphism SNP_01 =
            new SingleNucleotidePolymorphismBuilder().setRsId("rs123").build();

    private static final RiskAllele RA_01 = new RiskAlleleBuilder().setRiskAlleleName("rs123-?").build();

    @Before
    public void setUp() throws Exception {
        associationRowProcessor =
                new AssociationRowProcessor(associationAttributeService, associationCalculationService);
    }

    @Test
    public void testCreateAssociationFromUploadRow() throws Exception {

        // Stubbing mock object behaviour
        when(associationAttributeService.createLocusGenes(ROW_1.getAuthorReportedGene(), ",")).thenReturn(Arrays.asList(
                GENE_01,
                GENE_02));

        when(associationAttributeService.createSnp(ROW_1.getSnp())).thenReturn(SNP_01);

        when(associationAttributeService.createRiskAllele(ROW_1.getStrongestAllele(), SNP_01)).thenReturn(RA_01);

        Association association = associationRowProcessor.createAssociationFromUploadRow(ROW_1);

        verify(associationCalculationService, never()).reverseCI(ROW_1.getRange());
        verify(associationCalculationService, never()).setRange(ROW_1.getStandardError(),
                                                                ROW_1.getOrPerCopyNum());
        verify(associationAttributeService, never()).getEfoTraitsFromRepository(Collections.EMPTY_LIST);
        verify(associationAttributeService, atLeastOnce()).createLocusGenes(ROW_1.getAuthorReportedGene(), ",");
        verify(associationAttributeService, atLeastOnce()).createSnp(ROW_1.getSnp());
        verify(associationAttributeService, atLeastOnce()).createRiskAllele(ROW_1.getStrongestAllele(), SNP_01);

        assertThat(association).extracting("id", "riskFrequency",
                                           "pvalueDescription",
                                           "pvalueMantissa",
                                           "pvalueExponent",
                                           "multiSnpHaplotype",
                                           "snpInteraction",
                                           "snpApproved",
                                           "snpType",
                                           "standardError",
                                           "range",
                                           "description",
                                           "orPerCopyNum",
                                           "orPerCopyRecip",
                                           "orPerCopyRecipRange",
                                           "betaNum",
                                           "betaUnit",
                                           "betaDirection",
                                           "study",
                                           "associationReport",
                                           "lastMappingDate",
                                           "lastMappingPerformedBy",
                                           "lastUpdateDate")
                .containsExactly(null,
                                 "0.52",
                                 null,
                                 2,
                                 -7,
                                 false,
                                 false,
                                 false,
                                 null,
                                 (float) 0.6,
                                 "[0.82-0.92]",
                                 null,
                                 (float) 1.22,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null, null);
    }
}