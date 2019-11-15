package uk.ac.ebi.spot.goci.curation.service.deposition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.service.AssociationOperationsService;
import uk.ac.ebi.spot.goci.exception.EnsemblMappingException;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.DepositionAssociationDto;
import uk.ac.ebi.spot.goci.service.LociAttributesService;
import uk.ac.ebi.spot.goci.service.MapCatalogService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class DepositionAssociationService {

    @Autowired
    AssociationOperationsService associationOperationsService;
    @Autowired
    LociAttributesService lociService;
    @Autowired
    MapCatalogService mapCatalogService;

    public DepositionAssociationService() {}

    public void saveAssociations(SecureUser currentUser, String studyTag, Study study,
                                 List<DepositionAssociationDto> associations) {
        //find associations in study
        Collection<Association> associationList = new ArrayList<>();
        for (DepositionAssociationDto associationDto : associations) {
            if (associationDto.getStudyTag().equals(studyTag)) {
                Association association = new Association();
                if (associationDto.getStandardError() != null) {
                    association.setStandardError(associationDto.getStandardError().floatValue());
                }
                BigDecimal pValue = associationDto.getPValue();
                if (pValue != null) {
                    int exponent = pValue.precision() - pValue.scale() - 1;
                    association.setPvalueExponent(exponent);
                    association.setPvalueMantissa(pValue.unscaledValue().intValue());
                }
                String rsID = associationDto.getVariantID();
                List<Locus> locusList = new ArrayList<>();
                Locus locus = new Locus();
                SingleNucleotidePolymorphism snp = lociService.createSnp(rsID);
                RiskAllele riskAllele =
                        lociService.createRiskAllele(rsID + "-" + associationDto.getEffectAllele(), snp);
                List<RiskAllele> alleleList = new ArrayList<>();
                alleleList.add(riskAllele);
                locus.setStrongestRiskAlleles(alleleList);
                locusList.add(locus);
                association.setLoci(locusList);
                associationOperationsService.saveAssociation(association, study, new ArrayList<>());
                associationList.add(association);
            }
        }
        try {
            mapCatalogService.mapCatalogContentsByAssociations(currentUser.getEmail(), associationList);
        } catch (EnsemblMappingException e) {
            e.printStackTrace();
        }

    }
}
