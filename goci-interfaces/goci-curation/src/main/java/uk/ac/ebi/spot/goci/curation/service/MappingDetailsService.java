package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.MappingDetails;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by emma on 08/03/2016.
 *
 * @author emma
 *         <p>
 *         Creates details of study mapping
 */
@Service
public class MappingDetailsService {

    private AssociationRepository associationRepository;

    @Autowired
    public MappingDetailsService(AssociationRepository associationRepository) {
        this.associationRepository = associationRepository;
    }

    /**
     * An additional date should be added to the "Curator information" tab called "Last automated mapping date". This
     * should record the last date all SNPs were mapped using the automated mapping pipeline i.e. when there has been an
     * Ensembl release. This should be left blank for studies where SNPs have different mapping dates or were mapped by
     * a curator, indicating that the curator should check the "SNP associations page" for last mapping info.
     *
     * @param study Study with mapping details
     */
    public MappingDetails createMappingSummary(Study study) {

        MappingDetails mappingSummary = new MappingDetails();
        // GOCI-2267 hotfix. Reduce massively the performance. The previous approach was tedious.
        // The curator needs to know the last date mapping (automated mapping)
        Optional<Timestamp> mappingDate= associationRepository.findLastMappingDateByStudyId(study.getId());
        if (mappingDate.isPresent()){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date newDate = null;
            try {
                Timestamp mappingDateInfo= mappingDate.get();
                newDate=new Date(mappingDateInfo.getTime());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            mappingSummary.setMappingDate(newDate);
            mappingSummary.setPerformer("automatic_mapping_process");
        }

        return mappingSummary;
    }
}