package uk.ac.ebi.spot.goci.curation.service.deposition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.service.AssociationOperationsService;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.DepositionAssociationDto;
import uk.ac.ebi.spot.goci.model.deposition.DepositionStudyDto;
import uk.ac.ebi.spot.goci.model.deposition.SubmissionImportStudy;
import uk.ac.ebi.spot.goci.service.EnsemblRestTemplateService;
import uk.ac.ebi.spot.goci.service.LociAttributesService;
import uk.ac.ebi.spot.goci.service.MapCatalogService;
import uk.ac.ebi.spot.goci.service.ValidationService;
import uk.ac.ebi.spot.goci.utils.AssociationCalculationService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Service
public class AssociationValidationService {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    private AssociationOperationsService associationOperationsService;

    @Autowired
    private LociAttributesService lociService;

    @Autowired
    private MapCatalogService mapCatalogService;

    @Autowired
    private AssociationCalculationService calculationService;

    @Autowired
    private EnsemblRestTemplateService ensemblRestTemplateService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private DepositionStudiesImportService depositionStudiesImportService;

    public List<String> validateAssociations(String submissionId) {
        List<String> errorList = new ArrayList<>();
        Stream<SubmissionImportStudy> submissionImportStudyStream = depositionStudiesImportService.streamBySubmissionId(submissionId);
        submissionImportStudyStream.forEach(submissionImportStudy -> validate(submissionImportStudy, errorList));
        submissionImportStudyStream.close();
        return errorList;
    }

    private void validate(SubmissionImportStudy submissionImportStudy, List<String> errorList) {
        submissionImportStudy = depositionStudiesImportService.enrich(submissionImportStudy);
        if (submissionImportStudy.getDepositionStudyDto() != null) {
            DepositionStudyDto depositionStudyDto = submissionImportStudy.getDepositionStudyDto();
            if (depositionStudyDto.getAssociations() != null) {
                String eRelease = ensemblRestTemplateService.getRelease();
                String studyTag = depositionStudyDto.getStudyTag();
                String accessionId = depositionStudyDto.getAccession();

                for (DepositionAssociationDto associationDto : depositionStudyDto.getAssociations()) {
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
                        if (mantissa == 10) {
                            mantissa = 1;
                            exponent = exponent + 1;
                        }
                        association.setPvalueExponent(exponent);
                        association.setPvalueMantissa(mantissa);
                    }
                    association.setPvalueDescription(associationDto.getPValueText());
                    String rsID = associationDto.getVariantID();
                    getLog().info("Processing rdID: {}", studyTag, accessionId, rsID);
                    if (rsID != null) {
                        SingleNucleotidePolymorphism snp = lociService.createSnp(rsID);
                        RiskAllele riskAllele =
                                lociService.createRiskAllele(rsID + "-" + associationDto.getEffectAllele(), snp);

                        if (associationDto.getProxyVariant() != null) {
                            List<SingleNucleotidePolymorphism> proxySnps = new ArrayList<>();
                            proxySnps.add(lociService.createSnp(associationDto.getProxyVariant()));
                            riskAllele.setProxySnps(proxySnps);
                        }
                        List<RiskAllele> alleleList = new ArrayList<>();
                        alleleList.add(riskAllele);
                        locus.setStrongestRiskAlleles(alleleList);
                        loci.add(locus);
                        association.setLoci(loci);
                    } else {
                        errorList.add("No rsId found for association related to: " + associationDto.getStudyTag() + " | " + accessionId);
                        continue;
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
                    associationExtension.setOtherAllele(associationDto.getOtherAllele());

                    getLog().info("[{} | {}] Checking for SNP validation errors ...", studyTag, accessionId);
                    Collection<ValidationError> rowErrors =
                            associationOperationsService.checkSnpAssociationErrors(association,
                                    measurementType);
                    getLog().info("[{} | {}] Found {} SNP validation errors.", studyTag, accessionId, rowErrors.size());
                    for (ValidationError validationError : rowErrors) {
                        getLog().error("SNP validation error: {} | {} | {} | {}", validationError.getField(), validationError.getTypeError(), validationError.getError(), validationError.getWarning());
                        errorList.add("[" + accessionId + " | " + associationDto.getStudyTag() + "] SNP validation error: " + validationError.getField() + " [" + validationError.getTypeError() + "]: " + validationError.getError() + " | " + validationError.getWarning());
                    }

                    if (rowErrors.isEmpty()) {
                        // Save and validate form
                        // Validate association
                        getLog().info("[{} | {}] Performing full validation ...", studyTag, accessionId);
                        Collection<ValidationError> associationValidationErrors = validationService.runAssociationValidation(association, "full", eRelease);
                        getLog().info("[{} | {}] Found {} full validation errors.", studyTag, accessionId, associationValidationErrors.size());
                        for (ValidationError validationError : associationValidationErrors) {
                            getLog().error("Full validation error: {} | {} | {} | {}", validationError.getField(), validationError.getTypeError(), validationError.getError(), validationError.getWarning());
                            errorList.add("[" + accessionId + " | " + associationDto.getStudyTag() + "] Full validation error: " + validationError.getField() + " [" + validationError.getTypeError() + "]: " + validationError.getError() + " | " + validationError.getWarning());
                        }
                        if (!associationValidationErrors.isEmpty()) {
                            continue;
                        }

                        List<String> list = mapCatalogService.validateMappingForAssociation(association, rsID);
                        for (String error : list) {
                            errorList.add("[" + accessionId + " | " + associationDto.getStudyTag() + "] :: " + error);
                        }
                    }
                }
            }

        }
    }
}
