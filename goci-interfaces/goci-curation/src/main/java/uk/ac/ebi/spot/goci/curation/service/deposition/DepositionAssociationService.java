package uk.ac.ebi.spot.goci.curation.service.deposition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.service.AssociationOperationsService;
import uk.ac.ebi.spot.goci.exception.EnsemblMappingException;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.DepositionAssociationDto;
import uk.ac.ebi.spot.goci.repository.AssociationExtensionRepository;
import uk.ac.ebi.spot.goci.service.LociAttributesService;
import uk.ac.ebi.spot.goci.service.MapCatalogService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class DepositionAssociationService {

    @Autowired
    AssociationOperationsService associationOperationsService;
    @Autowired
    LociAttributesService lociService;
    @Autowired
    MapCatalogService mapCatalogService;
    @Autowired
    AssociationExtensionRepository extensionRepository;

    public DepositionAssociationService() {}

    public String saveAssociations(SecureUser currentUser, String studyTag, Study study,
                                 List<DepositionAssociationDto> associations) {
        //find associations in study
        StringBuffer studyNote = new StringBuffer();
        Collection<Association> associationList = new ArrayList<>();
        for (DepositionAssociationDto associationDto : associations) {
            if (associationDto.getStudyTag().equals(studyTag)) {
                Association association = new Association();
                if (associationDto.getStandardError() != null) {
                    association.setStandardError(associationDto.getStandardError().floatValue());
                }
                String pValue = associationDto.getPValue();
                if (pValue != null) {
                    String[] pValues = pValue.toLowerCase().split("e");
                    int exponent = Integer.valueOf(pValues[1]);
                    int mantissa = Double.valueOf(pValues[0]).intValue();
                    association.setPvalueExponent(exponent);
                    association.setPvalueMantissa(mantissa);
                }
                association.setPvalueDescription(associationDto.getPValueText());
                String rsID = associationDto.getVariantID();
                List<Locus> locusList = new ArrayList<>();
                Locus locus = new Locus();
                SingleNucleotidePolymorphism snp = lociService.createSnp(rsID);
                studyNote.append("added SNP " + rsID + "\n");
                RiskAllele riskAllele =
                        lociService.createRiskAllele(rsID + "-" + associationDto.getEffectAllele(), snp);
                List<RiskAllele> alleleList = new ArrayList<>();
                alleleList.add(riskAllele);
                locus.setStrongestRiskAlleles(alleleList);
                locusList.add(locus);
                association.setLoci(locusList);
                associationOperationsService.saveAssociation(association, study, new ArrayList<>());
                associationList.add(association);
                if(associationDto.getEffectAlleleFrequency() != null) {
                    association.setRiskFrequency(associationDto.getEffectAlleleFrequency().toString());
                }
                if(associationDto.getStandardError() != null) {
                    association.setStandardError(associationDto.getStandardError().floatValue());
                }
                if(associationDto.getOddsRatio() != null) {
                    association.setOrPerCopyNum(associationDto.getOddsRatio().floatValue());
                }
                if(associationDto.getBeta() != null) {
                    association.setBetaNum(associationDto.getBeta().floatValue());
                }
                if(associationDto.getBetaUnit() != null) {
                    association.setBetaUnit(associationDto.getBetaUnit());
                }
                if(associationDto.getCiLower() != null) {
                    association.setRange("[" + associationDto.getCiLower() + "-" + associationDto.getCiUpper() + "]");
                }
                association.setBetaDirection(associationDto.getBetaDirection());
                AssociationExtension associationExtension = new AssociationExtension();
                associationExtension.setAssociation(association);
                associationExtension.setEffectAllele(associationDto.getEffectAllele());
                associationExtension.setOtherAllele(associationDto.getOtherAllele());
                extensionRepository.save(associationExtension);
                association.setAssociationExtension(associationExtension);
                associationOperationsService.saveAssociation(association, study, new ArrayList<>());
            }
        }
        try {
            mapCatalogService.mapCatalogContentsByAssociations(currentUser.getEmail(), associationList);
            studyNote.append("mapped associations" + "\n");
        } catch (EnsemblMappingException e) {
            e.printStackTrace();
            studyNote.append("error mapping associations" + "\n");
        }
        return studyNote.toString();
    }
}
