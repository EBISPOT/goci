package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;

import java.util.Date;

/**
 * Created by emma on 07/08/2015.
 *
 * @author emma
 *         <p>
 *         Service that updates an associations lastMappedDate and lastMappingPerformedBy information.
 */
@Service
public class MappingRecordService {

    private AssociationRepository associationRepository;

    // Constructor
    @Autowired
    public MappingRecordService(AssociationRepository associationRepository) {
        this.associationRepository = associationRepository;
    }

    /**
     * Update mapping record for an association
     *
     * @param association Association to update
     * @param mappingDate Date of mapping pipeline run
     * @param mappedBy    Name of curator or process that ran mapping
     */
    public void updateAssociationMappingRecord(Association association, Date mappingDate, String mappedBy) {
        association.setLastMappingDate(mappingDate);
        association.setLastMappingPerformedBy(mappedBy);
        associationRepository.save(association);
    }

}
