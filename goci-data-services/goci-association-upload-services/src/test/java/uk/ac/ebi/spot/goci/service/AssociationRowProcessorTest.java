package uk.ac.ebi.spot.goci.service;

import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.AssociationUploadRowBuilder;
import uk.ac.ebi.spot.goci.builder.GeneBuilder;
import uk.ac.ebi.spot.goci.builder.RiskAlleleBuilder;
import uk.ac.ebi.spot.goci.builder.SingleNucleotidePolymorphismBuilder;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.utils.AssociationCalculationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

    private static final AssociationUploadRow STANDARD_ROW = new AssociationUploadRowBuilder().setRowNumber(1)
            .setSnp("rs123")
            .setProxy("rs99")
            .setAuthorReportedGene("SFRP1, ELF1")
            .setStrongestAllele("rs123-?")
            .setPvalueMantissa(2)
            .setPvalueExponent(-7)
            .setPvalueDescription("(some pvalue description)")
            .setOrPerCopyNum((float) 1.22)
            .setRange("[0.82-0.92]")
            .setStandardError((float) 0.6)
            .setAssociationRiskFrequency("0.52")
            .build();

    private static final AssociationUploadRow SNP_INTERACTION_ROW = new AssociationUploadRowBuilder().setRowNumber(2)
            .setSnp("rs2562796 x rs16832404")
            .setAuthorReportedGene("PMS1 x HIBCH")
            .setStrongestAllele("rs2562796-T x rs16832404-G")
            .setAssociationRiskFrequency("0.52")
            .setRiskFrequency("0.3 x 0.4")
            .setPvalueMantissa(2)
            .setPvalueExponent(-7)
            .setOrPerCopyNum((float) 1.22)
            .setRange("[0.82-0.92]")
            .setStandardError((float) 0.6)
            .setSnpInteraction("Y")
            .build();

    private static final AssociationUploadRow HAPLOTYPE_ROW = new AssociationUploadRowBuilder().setRowNumber(2)
            .setSnp("rs456; rs678")
            .setStrongestAllele("rs456-T; rs678-?")
            .setPvalueMantissa(2)
            .setPvalueExponent(-7)
            .setMultiSnpHaplotype("Y")
            .setPvalueDescription("description")
            .build();

    private static final AssociationUploadRow ROW_THAT_SHOULD_FAIL = new AssociationUploadRowBuilder().setRowNumber(2)
            .setPvalueMantissa(2)
            .setPvalueExponent(-7)
            .build();

    private static final Gene GENE_01 = new GeneBuilder().setGeneName("SFRP1").build();

    private static final Gene GENE_02 = new GeneBuilder().setGeneName("ELF1").build();

    private static final Gene GENE_03 = new GeneBuilder().setGeneName("PMS1").build();

    private static final Gene GENE_04 = new GeneBuilder().setGeneName("HIBCH").build();

    private static final SingleNucleotidePolymorphism PROXY =
            new SingleNucleotidePolymorphismBuilder().setRsId("rs99").build();

    private static final SingleNucleotidePolymorphism SNP_01 =
            new SingleNucleotidePolymorphismBuilder().setRsId("rs123").build();

    private static final SingleNucleotidePolymorphism SNP_02 =
            new SingleNucleotidePolymorphismBuilder().setRsId("rs2562796").build();

    private static final SingleNucleotidePolymorphism SNP_03 =
            new SingleNucleotidePolymorphismBuilder().setRsId("rs16832404").build();

    private static final SingleNucleotidePolymorphism SNP_04 =
            new SingleNucleotidePolymorphismBuilder().setRsId("rs456").build();

    private static final SingleNucleotidePolymorphism SNP_05 =
            new SingleNucleotidePolymorphismBuilder().setRsId("rs678").build();

    private static final RiskAllele RA_01 = new RiskAlleleBuilder().setRiskAlleleName("rs123-?")
            .setSnp(SNP_01)
            .build();

    private static final RiskAllele RA_02 =
            new RiskAlleleBuilder().setRiskAlleleName("rs2562796-T").setSnp(SNP_02).build();

    private static final RiskAllele RA_03 =
            new RiskAlleleBuilder().setRiskAlleleName("rs16832404-G").setSnp(SNP_03).build();

    private static final RiskAllele RA_04 = new RiskAlleleBuilder().setRiskAlleleName("rs456-T").setSnp(SNP_04).build();

    private static final RiskAllele RA_05 = new RiskAlleleBuilder().setRiskAlleleName("rs678-?").setSnp(SNP_05).build();

    @Before
    public void setUp() throws Exception {
        associationRowProcessor =
                new AssociationRowProcessor(associationAttributeService, associationCalculationService);
    }

    @Test
    public void testCreateAssociationFromUploadRow() throws Exception {

        // Stubbing mock object behaviour
        when(associationAttributeService.createLocusGenes(STANDARD_ROW.getAuthorReportedGene(),
                                                          ",")).thenReturn(Arrays.asList(
                GENE_01,
                GENE_02));
        when(associationAttributeService.createSnp(STANDARD_ROW.getSnp())).thenReturn(SNP_01);
        when(associationAttributeService.createRiskAllele(STANDARD_ROW.getStrongestAllele(), SNP_01)).thenReturn(RA_01);
        when(associationAttributeService.createSnp(STANDARD_ROW.getProxy())).thenReturn(PROXY);

        Association association = associationRowProcessor.createAssociationFromUploadRow(STANDARD_ROW);

        verify(associationCalculationService, never()).reverseCI(STANDARD_ROW.getRange());
        verify(associationCalculationService, never()).setRange(STANDARD_ROW.getStandardError(),
                                                                STANDARD_ROW.getOrPerCopyNum());
        verify(associationAttributeService, never()).getEfoTraitsFromRepository(Collections.EMPTY_LIST);
        verify(associationAttributeService, times(1)).createLocusGenes(STANDARD_ROW.getAuthorReportedGene(), ",");
        verify(associationAttributeService, times(1)).createSnp(STANDARD_ROW.getSnp());
        verify(associationAttributeService, times(1)).createSnp(STANDARD_ROW.getProxy());
        verify(associationAttributeService, times(1)).createRiskAllele(STANDARD_ROW.getStrongestAllele(), SNP_01);

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
                                 "(some pvalue description)",
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

        assertThat(association.getEfoTraits()).isEmpty();
        assertThat(association.getEvents()).isEmpty();
        assertThat(association.getStudy()).isNull();
        assertThat(association.getLoci()).hasSize(1);

        // Check locus attributes
        Collection<Gene> locusGenes = new ArrayList<>();
        association.getLoci().stream().forEach(locus -> {
                                                   locusGenes.addAll(locus.getAuthorReportedGenes());
                                               }
        );


        Collection<RiskAllele> locusRiskAlleles = new ArrayList<>();
        association.getLoci().stream().forEach(locus -> {
                                                   locusRiskAlleles.addAll(locus.getStrongestRiskAlleles());
                                               }
        );

        Collection<SingleNucleotidePolymorphism> proxies = new ArrayList<>();
        locusRiskAlleles.stream().forEach(riskAllele -> {
            proxies.addAll(riskAllele.getProxySnps());
        });

        assertThat(association.getLoci()).extracting(Locus::getDescription).containsOnly("Single variant");
        assertThat(locusGenes).hasSize(2).contains(GENE_01, GENE_02);
        assertThat(locusRiskAlleles).hasSize(1).contains(RA_01);
        assertThat(locusRiskAlleles).extracting("riskAlleleName", "riskFrequency", "snp.rsId" )
                .contains(tuple("rs123-?","0.52","rs123"));
        assertThat(locusRiskAlleles).extracting(RiskAllele::getSnp).contains(SNP_01);
        assertThat(proxies).contains(PROXY);
        assertThat(proxies).extracting(SingleNucleotidePolymorphism::getRsId).containsExactly("rs99");
    }

    @Test
    public void testCreateAssociationFromUploadRowSnpInteraction() throws Exception {

        // Stubbing mock object behaviour
        when(associationAttributeService.createLocusGenes("PMS1 ", ",")).thenReturn(Arrays.asList(GENE_03));
        when(associationAttributeService.createLocusGenes(" HIBCH", ",")).thenReturn(Arrays.asList(GENE_04));
        when(associationAttributeService.createSnp("rs2562796")).thenReturn(SNP_02);
        when(associationAttributeService.createSnp("rs16832404")).thenReturn(SNP_03);
        when(associationAttributeService.createRiskAllele("rs2562796-T", SNP_02)).thenReturn(RA_02);
        when(associationAttributeService.createRiskAllele("rs16832404-G", SNP_03)).thenReturn(RA_03);

        Association association = associationRowProcessor.createAssociationFromUploadRow(SNP_INTERACTION_ROW);

        verify(associationCalculationService, never()).reverseCI(SNP_INTERACTION_ROW.getRange());
        verify(associationCalculationService, never()).setRange(SNP_INTERACTION_ROW.getStandardError(),
                                                                SNP_INTERACTION_ROW.getOrPerCopyNum());
        verify(associationAttributeService, never()).getEfoTraitsFromRepository(Collections.EMPTY_LIST);

        verify(associationAttributeService, times(1)).createLocusGenes("PMS1 ", ",");
        verify(associationAttributeService, times(1)).createLocusGenes(" HIBCH", ",");
        verify(associationAttributeService, times(1)).createSnp("rs2562796");
        verify(associationAttributeService, times(1)).createSnp("rs16832404");
        verify(associationAttributeService, times(1)).createRiskAllele("rs2562796-T", SNP_02);
        verify(associationAttributeService, times(1)).createRiskAllele("rs16832404-G", SNP_03);

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
                                 true,
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

        assertThat(association.getEfoTraits()).isEmpty();
        assertThat(association.getEvents()).isEmpty();
        assertThat(association.getStudy()).isNull();
        assertThat(association.getLoci()).hasSize(2);

        // Check locus attributes
        Collection<Gene> locusGenes = new ArrayList<>();
        association.getLoci().stream().forEach(locus -> {
                                                   locusGenes.addAll(locus.getAuthorReportedGenes());
                                               }
        );


        Collection<RiskAllele> locusRiskAlleles = new ArrayList<>();
        association.getLoci().stream().forEach(locus -> {
                                                   locusRiskAlleles.addAll(locus.getStrongestRiskAlleles());
                                               }
        );

        assertThat(association.getLoci()).extracting(Locus::getDescription).containsOnly("SNP x SNP interaction");
        assertThat(locusGenes).hasSize(2).contains(GENE_03, GENE_04);
        assertThat(locusRiskAlleles).hasSize(2).contains(RA_02, RA_03);
        assertThat(locusRiskAlleles).extracting("riskAlleleName", "riskFrequency", "snp.rsId" )
                .contains(tuple("rs2562796-T","0.3","rs2562796"),tuple("rs16832404-G","0.4","rs16832404"));
        assertThat(locusRiskAlleles).extracting(RiskAllele::getSnp).containsExactly(SNP_02, SNP_03);
    }

    @Test
    public void testCreateAssociationFromUploadRowHaplotype() throws Exception {

        // Stubbing mock object behaviour
        when(associationAttributeService.createSnp("rs456")).thenReturn(SNP_04);
        when(associationAttributeService.createSnp("rs678")).thenReturn(SNP_05);
        when(associationAttributeService.createRiskAllele("rs456-T", SNP_04)).thenReturn(RA_04);
        when(associationAttributeService.createRiskAllele("rs678-?", SNP_05)).thenReturn(RA_05);

        Association association = associationRowProcessor.createAssociationFromUploadRow(HAPLOTYPE_ROW);

        verify(associationCalculationService, never()).reverseCI(Matchers.anyString());
        verify(associationCalculationService, never()).setRange(Matchers.anyDouble(),
                                                                Matchers.anyDouble());
        verify(associationAttributeService, never()).getEfoTraitsFromRepository(Collections.EMPTY_LIST);
        verify(associationAttributeService, never()).createLocusGenes(Matchers.anyString(), Matchers.anyString());


        verify(associationAttributeService, times(1)).createSnp("rs456");
        verify(associationAttributeService, times(1)).createSnp("rs678");
        verify(associationAttributeService, times(1)).createRiskAllele("rs456-T", SNP_04);
        verify(associationAttributeService, times(1)).createRiskAllele("rs678-?", SNP_05);

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
                                 "NR",
                                 "(description)",
                                 2,
                                 -7,
                                 true,
                                 false,
                                 false,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null, null);

        assertThat(association.getEfoTraits()).isEmpty();
        assertThat(association.getEvents()).isEmpty();
        assertThat(association.getStudy()).isNull();
        assertThat(association.getLoci()).hasSize(1);


        Collection<RiskAllele> locusRiskAlleles = new ArrayList<>();
        association.getLoci().stream().forEach(locus -> {
                                                   locusRiskAlleles.addAll(locus.getStrongestRiskAlleles());
                                               }
        );

        // Check locus attributes
        Collection<Gene> locusGenes = new ArrayList<>();
        association.getLoci().stream().forEach(locus -> {
                                                   locusGenes.addAll(locus.getAuthorReportedGenes());
                                               }
        );

        assertThat(association.getLoci()).extracting(Locus::getDescription).containsOnly("2-SNP haplotype");
        assertThat(locusGenes).isEmpty();
        assertThat(locusRiskAlleles).hasSize(2).contains(RA_04, RA_05);
        assertThat(locusRiskAlleles).extracting(RiskAllele::getRiskFrequency).containsNull();
        assertThat(locusRiskAlleles).extracting("riskAlleleName", "riskFrequency", "snp.rsId" )
                .contains(tuple("rs456-T",null,"rs456"),tuple("rs678-?",null,"rs678"));
        assertThat(locusRiskAlleles).extracting(RiskAllele::getSnp).containsExactly(SNP_04, SNP_05);
    }

    @Test
    public void testCreateAssociationFromUploadRowErrorRow() throws Exception {

        Association association = associationRowProcessor.createAssociationFromUploadRow(ROW_THAT_SHOULD_FAIL);

        verify(associationCalculationService, never()).reverseCI(Matchers.anyString());
        verify(associationCalculationService, never()).setRange(Matchers.anyDouble(),
                                                                Matchers.anyDouble());
        verify(associationAttributeService, never()).getEfoTraitsFromRepository(Collections.EMPTY_LIST);
        verify(associationAttributeService, never()).createLocusGenes(Matchers.anyString(), Matchers.anyString());
        verify(associationAttributeService, never()).createSnp(Matchers.anyString());
        verify(associationAttributeService, never()).createRiskAllele(Matchers.anyString(),
                                                                      Matchers.any(SingleNucleotidePolymorphism.class));

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
                                 "NR",
                                 null,
                                 2,
                                 -7,
                                 false,
                                 false,
                                 false,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null, null);

        assertThat(association.getEfoTraits()).isEmpty();
        assertThat(association.getEvents()).isEmpty();
        assertThat(association.getStudy()).isNull();
        assertThat(association.getLoci()).isEmpty();
    }
}