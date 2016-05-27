package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.MappingDetails;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        Collection<Association> studyAssociations = associationRepository.findByStudyId(study.getId());

        if (studyAssociations != null && !studyAssociations.isEmpty()) {

            // Determine if we have more than one performer
            Set<String> allAssociationMappingPerformers = new HashSet<String>();
            for (Association association : studyAssociations) {
                allAssociationMappingPerformers.add(association.getLastMappingPerformedBy());
            }

            Map<String, String> mappingDateToPerformerMap = new HashMap<>();
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");

            // If only one performer we need to check dates to see mapping didn't happen at different times
            if (allAssociationMappingPerformers.size() == 1) {
                String performer = allAssociationMappingPerformers.iterator().next();

                // Only care about automated mapping
                if (performer != null) {
                    if (performer.equals("automatic_mapping_process")) {

                        // Go through all associations and store mapping performer and date
                        for (Association association : studyAssociations) {
                            String date = dt.format(association.getLastMappingDate());
                            mappingDateToPerformerMap.put(date, performer);
                        }
                    }
                }
            }

            // If its only been mapped by an automated process, all with same date
            if (mappingDateToPerformerMap.size() == 1) {
                for (String date : mappingDateToPerformerMap.keySet()) {
                    Date newDate = null;
                    try {
                        newDate = dt.parse(date);

                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mappingSummary.setMappingDate(newDate);
                    mappingSummary.setPerformer(mappingDateToPerformerMap.get(date));
                }
            }
        }
        return mappingSummary;
    }
}