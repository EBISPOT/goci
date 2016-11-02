package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;

import java.util.Collection;

/**
 * Created by emma on 14/08/2015.
 *
 * @author emma
 *         <p>
 *         Based on classes in  goci-core/goci-service/src/main/java/uk/ac/ebi/spot/goci/service/
 *         <p>
 *         A facade service around a {@link uk.ac.ebi.spot.goci.repository.AssociationRepository} that retrieves all
 *         associations, and then within the same datasource transaction additionally loads other objects referenced by
 *         this association like Loci.
 *         <p>
 *         Use this when you know you will need deep information about a association and do not have an open session
 *         that can be used to lazy load extra data.
 */
@Service
public class AssociationQueryService {

    // Repositories
    private AssociationRepository associationRepository;

    @Autowired
    public AssociationQueryService(AssociationRepository associationRepository) {
        this.associationRepository = associationRepository;
    }

    @Transactional(readOnly = true)
    public Collection<Association> findAllAssociations() {
        Collection<Association> allAssociations = associationRepository.findAll(sortByIdDesc());
        allAssociations.forEach(this::loadAssociatedData);
        return allAssociations;
    }

    public void loadAssociatedData(Association association) {
        association.getLoci();
        association.getLoci().forEach(
                locus -> {
                    locus.getAuthorReportedGenes().size();
                    locus.getStrongestRiskAlleles().size();
                });

        association.getEvents();

    }

    // Sort options
    private Sort sortByIdDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "id"));
    }
}
