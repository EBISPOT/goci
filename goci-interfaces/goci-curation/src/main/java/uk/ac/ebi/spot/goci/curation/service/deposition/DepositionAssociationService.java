package uk.ac.ebi.spot.goci.curation.service.deposition;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.curation.model.AssociationValidationView;
import uk.ac.ebi.spot.goci.curation.service.AssociationOperationsService;
import uk.ac.ebi.spot.goci.curation.service.SingleSnpMultiSnpAssociationService;
import uk.ac.ebi.spot.goci.exception.EnsemblMappingException;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.DepositionAssociationDto;
import uk.ac.ebi.spot.goci.repository.AssociationExtensionRepository;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.service.EnsemblRestTemplateService;
import uk.ac.ebi.spot.goci.service.LociAttributesService;
import uk.ac.ebi.spot.goci.service.MapCatalogService;
import uk.ac.ebi.spot.goci.service.ValidationService;
import uk.ac.ebi.spot.goci.utils.AssociationCalculationService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class DepositionAssociationService {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    SingleSnpMultiSnpAssociationService singleSnpMultiSnpAssociationService;
    @Autowired
    AssociationOperationsService associationOperationsService;
    @Autowired
    AssociationRepository associationRepository;
    @Autowired
    LociAttributesService lociService;
    @Autowired
    MapCatalogService mapCatalogService;
    @Autowired
    AssociationExtensionRepository extensionRepository;
    @Autowired
    AssociationCalculationService calculationService;
    @Autowired
    EnsemblRestTemplateService ensemblRestTemplateService;
    @Autowired
    ValidationService validationService;


    public DepositionAssociationService() {
    }

    public String saveAssociations(SecureUser currentUser, String studyTag, Study study,
                                   List<DepositionAssociationDto> associations) throws EnsemblMappingException {
        //find associations in study
        String eRelease = ensemblRestTemplateService.getRelease();
        StringBuffer studyNote = new StringBuffer();
        for (DepositionAssociationDto associationDto : associations) {
            if (associationDto.getStudyTag().equals(studyTag)) {
                Association association = new Association();
                association.setSnpInteraction(false);
                Collection<Locus> loci = new ArrayList<>();
                Locus locus = new Locus();
                association.setMultiSnpHaplotype(false);
                association.setSnpType("novel");
                locus.setDescription("Single variant");

                if (associationDto.getStandardError() != null) {
                    association.setStandardError(associationDto.getStandardError().floatValue());
                }
                String pValue = associationDto.getPValue();
                if (pValue != null && pValue.toLowerCase().contains("e")) {
                    String[] pValues = pValue.toLowerCase().split("e");
                    int exponent = Integer.valueOf(pValues[1]);
                    int mantissa = (int) Math.round(Double.valueOf(pValues[0]));
                    association.setPvalueExponent(exponent);
                    association.setPvalueMantissa(mantissa);
                }
                association.setPvalueDescription(associationDto.getPValueText());
                String rsID = associationDto.getVariantID();
                getLog().info("[IMPORT] Processing rdID: {}", rsID);
                if (StringUtils.isNotBlank(rsID)) {
                    SingleNucleotidePolymorphism snp = lociService.createSnp(rsID);
                    getLog().info("[IMPORT] SNP created: {}", snp.getId());

                    studyNote.append("added SNP " + rsID + "\n");//does this fail
                    RiskAllele riskAllele =
                            lociService.createRiskAllele(rsID + "-" + associationDto.getEffectAllele(), snp);
                    getLog().info("[IMPORT] Risk allele created: {}", riskAllele.getId());

                    if (StringUtils.isNotBlank(associationDto.getProxyVariant())) {
                        List<SingleNucleotidePolymorphism> proxySnps = new ArrayList<>();
                        proxySnps.add(lociService.createSnp(associationDto.getProxyVariant()));
                        riskAllele.setProxySnps(proxySnps);
                        getLog().info("[IMPORT] Created {} proxy SNPs: {}", proxySnps.size());
                    }
                    List<RiskAllele> alleleList = new ArrayList<>();
                    alleleList.add(riskAllele);
                    locus.setStrongestRiskAlleles(alleleList);
                    loci.add(locus);
                    association.setLoci(loci);
                } else {
                    throw new IllegalArgumentException("error, no rs_id found for " + associationDto.getStudyTag());
                }
                if (associationDto.getEffectAlleleFrequency() != null && associationDto.getEffectAlleleFrequency().intValue() != -1) {
                    association.setRiskFrequency(associationDto.getEffectAlleleFrequency().toString());
                } else {
                    association.setRiskFrequency("NR");
                }
                if (associationDto.getStandardError() != null) {
                    association.setStandardError(associationDto.getStandardError().floatValue());
                }
                String measurementType = "";
                if (associationDto.getOddsRatio() != null) {
                    association.setOrPerCopyNum(associationDto.getOddsRatio().floatValue());
                    measurementType = "or";
                }
                if (associationDto.getBeta() != null) {
                    Double betaValue = associationDto.getBeta();
                    if (betaValue < 0) {
                        association.setBetaDirection("decrease");
                    } else {
                        association.setBetaDirection("increase");
                    }
                    association.setBetaNum(Math.abs(betaValue.floatValue()));
                    measurementType = "beta";
                    association.setBetaUnit(associationDto.getBetaUnit());
                }
                if (associationDto.getCiLower() != null && associationDto.getCiUpper() != null) {
                    association.setRange("[" + associationDto.getCiLower() + "-" + associationDto.getCiUpper() + "]");
                } else {
                    if (associationDto.getOddsRatio() != null && associationDto.getStandardError() != null) {
                        association.setRange(calculationService
                                .setRange(associationDto.getStandardError(), Math.abs(associationDto.getOddsRatio())));
                        measurementType = "or";
                    } else if (associationDto.getBeta() != null && associationDto.getStandardError() != null) {
                        association.setRange(calculationService
                                .setRange(associationDto.getStandardError(), Math.abs(associationDto.getBeta())));
                        measurementType = "beta";
                    }
                }
                AssociationExtension associationExtension = new AssociationExtension();
                associationExtension.setAssociation(association);
                associationExtension.setEffectAllele(associationDto.getEffectAllele());
                if (StringUtils.isNotBlank(associationDto.getOtherAllele())) {
                    associationExtension.setOtherAllele(associationDto.getOtherAllele());
                }

                getLog().info("[IMPORT] Checking for association errors ...");
                List<AssociationValidationView> rowErrors =
                        associationOperationsService.checkSnpAssociationErrors(association,
                                measurementType);
                getLog().info("[IMPORT] Found {} association errors.", rowErrors.size());
                for (AssociationValidationView associationValidationView : rowErrors) {
                    getLog().error("[IMPORT] Assoc error: {} | {} | {}", associationValidationView.getWarning(), associationValidationView.getErrorMessage(), associationValidationView.getField());
                }

                if (rowErrors.isEmpty()) {
                    // Save and validate form
                    // Validate association
                    getLog().info("[IMPORT] Validating association ...");
                    Collection<ValidationError> associationValidationErrors =
                            validationService.runAssociationValidation(association, "full", eRelease);
                    getLog().info("[IMPORT] Found {} validation errors.", associationValidationErrors.size());
                    for (ValidationError validationError : associationValidationErrors) {
                        getLog().error("[IMPORT] Validation error: {} | {} | {}", validationError.getWarning(), validationError.getError(), validationError.getField());
                    }

                    associationOperationsService.saveAssociation(association, study, associationValidationErrors);
                    extensionRepository.save(associationExtension);
                    association.setAssociationExtension(associationExtension);
                    associationRepository.save(association);

                    Collection<AssociationValidationView> errors = null;
                    try {
                        getLog().info("[IMPORT] Saving association ...");
                        errors = associationOperationsService.saveAssociationCreatedFromForm(study,
                                association, currentUser, eRelease);
                        getLog().info("[IMPORT] Found {} errors on save.", errors.size());
                        for (AssociationValidationView associationValidationView : errors) {
                            getLog().error("[IMPORT] Save error: {} | {} | {}", associationValidationView.getWarning(), associationValidationView.getErrorMessage(), associationValidationView.getField());
                        }

                        mapCatalogService.mapCatalogContentsByAssociations(currentUser.getEmail(),
                                Collections.singleton(association));
                        studyNote.append("mapped associations" + "\n");
                    } catch (EnsemblMappingException e) {
                        getLog().error("[IMPORT] Ensembl mapping failure: {}", e.getMessage(), e);
                        return "ensembl_mapping_failure";
                    }
                } else {
                    rowErrors.forEach(s -> studyNote.append(s.getErrorMessage()));
                }
            }
        }
        return studyNote.toString();
    }
}
