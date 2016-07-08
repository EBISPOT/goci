package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import service.TrackingOperationService;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.DeletedAssociation;
import uk.ac.ebi.spot.goci.model.Event;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.DeletedAssociationRepository;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 06/07/2016.
 *
 * @author emma
 *         <p>
 *         Service to delete an assocaiation
 */
@Service
public class AssociationDeletionService {

    private AssociationRepository associationRepository;
    private LociAttributesService lociAttributesService;
    private TrackingOperationService trackingOperationService;
    private DeletedAssociationRepository deletedAssociationRepository;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public AssociationDeletionService(AssociationRepository associationRepository,
                                      LociAttributesService lociAttributesService,
                                      @Qualifier("associationTrackingOperationServiceImpl") TrackingOperationService trackingOperationService,
                                      DeletedAssociationRepository deletedAssociationRepository) {
        this.associationRepository = associationRepository;
        this.lociAttributesService = lociAttributesService;
        this.trackingOperationService = trackingOperationService;
        this.deletedAssociationRepository = deletedAssociationRepository;
    }

    public void deleteAssociation(Association association, SecureUser user) {

        getLog().info("Deleting association ".concat(String.valueOf(association.getId())));

        // For each association get the loci
        Collection<Locus> loci = new ArrayList<Locus>();
        loci.addAll(association.getLoci());

        // Delete each locus and risk allele,
        // which in turn deletes link to genes via author_reported_gene table.
        // SNPs are not deleted as they may be used in other associations.
        for (Locus locus : loci) {
            Collection<RiskAllele> locusRiskAlleles = locus.getStrongestRiskAlleles();
            locus.setStrongestRiskAlleles(new ArrayList<>());
            for (RiskAllele riskAllele : locusRiskAlleles) {
                lociAttributesService.deleteRiskAllele(riskAllele);
            }
            lociAttributesService.deleteLocus(locus);
        }

        // Add deletion event
        trackingOperationService.delete(association, user);
        DeletedAssociation deletedAssociation = createDeletedAssociation(association);

        // Delete associations
        associationRepository.delete(association);

        // Save deleted association
        getLog().info("Saving details of deleted association: ".concat(String.valueOf(deletedAssociation.getId())));
        deletedAssociationRepository.save(deletedAssociation);
    }

    private DeletedAssociation createDeletedAssociation(Association association) {
        Collection<Event> events = association.getEvents();
        Long id = association.getId();
        Long studyId = association.getStudy().getId();
        return new DeletedAssociation(id, studyId, events);
    }
}
