package service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.EventType;

/**
 * Created by emma on 23/05/2016.
 *
 * @author emma
 *         <p>
 *         Service class to determine that type of event based on user selection
 */
@Service
public class EventTypeService {

    /**
     * Determine event type based on status
     *
     * @param status curation status to determine event type from
     * @return eventType
     */
    public EventType determineEventTypeFromStatus(CurationStatus status) {
        EventType eventType = null;
        switch (status.getStatus()) {
            case "Level 1 ancestry done":
                eventType = EventType.STUDY_STATUS_CHANGE_LEVEL_1_ANCESTRY_DONE;
                break;
            case "Level 2 ancestry done":
                eventType = EventType.STUDY_STATUS_CHANGE_LEVEL_2_ANCESTRY_DONE;
                break;
            case "Level 1 curation done":
                eventType = EventType.STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE;
                break;
            case "Level 2 curation done":
                eventType = EventType.STUDY_STATUS_CHANGE_LEVEL_2_CURATION_DONE;
                break;
            case "Publish study":
                eventType = EventType.STUDY_STATUS_CHANGE_PUBLISH_STUDY;
                break;
            case "Awaiting Curation":
                eventType = EventType.STUDY_STATUS_CHANGE_AWAITING_CURATION;
                break;
            case "Outstanding Query":
                eventType = EventType.STUDY_STATUS_CHANGE_OUTSTANDING_QUERY;
                break;
            case "CNV Paper":
                eventType = EventType.STUDY_STATUS_CHANGE_CNV_PAPER;
                break;
            case "Curation Abandoned":
                eventType = EventType.STUDY_STATUS_CHANGE_CURATION_ABANDONED;
                break;
            case "Conversion Problem":
                eventType = EventType.STUDY_STATUS_CHANGE_CONVERSION_PROBLEM;
                break;
            case "Unpublished from catalog":
                eventType = EventType.STUDY_STATUS_CHANGE_UNPUBLISHED_FROM_CATALOG;
                break;
            case "Pending author query":
                eventType = EventType.STUDY_STATUS_CHANGE_PENDING_AUTHOR_QUERY;
                break;
            case "Awaiting EFO assignment":
                eventType = EventType.STUDY_STATUS_CHANGE_AWAITING_EFO_ASSIGNMENT;
                break;
            default:
                eventType = EventType.STUDY_STATUS_CHANGE_UNKNOWN;
                break;
        }
        return eventType;
    }

    /**
     * Determine event type based on curator
     *
     * @param curator curator to determine event type from
     * @return eventType
     */
    public EventType determineEventTypeFromCurator(Curator curator) {
        EventType eventType = null;

        switch (curator.getLastName()) {
            case "Morales":
                eventType = EventType.STUDY_CURATOR_ASSIGNMENT_MORALES;
                break;
            case "MacArthur":
                eventType = EventType.STUDY_CURATOR_ASSIGNMENT_MACARTHUR;
                break;
            case "Hindorff":
                eventType = EventType.STUDY_CURATOR_ASSIGNMENT_HINDORFF;
                break;
            case "Junkins":
                eventType = EventType.STUDY_CURATOR_ASSIGNMENT_JUNKINS;
                break;
            case "Hall":
                eventType = EventType.STUDY_CURATOR_ASSIGNMENT_HALL;
                break;
            case "Welter":
                eventType = EventType.STUDY_CURATOR_ASSIGNMENT_WELTER;
                break;
            case "Cerezo":
                eventType = EventType.STUDY_CURATOR_ASSIGNMENT_CEREZO;
                break;
            case "Milano":
                eventType = EventType.STUDY_CURATOR_ASSIGNMENT_MILANO;
                break;
            case "McMahon":
                eventType = EventType.STUDY_CURATOR_ASSIGNMENT_MCMAHON;
                break;
            case "Unassigned":
                eventType = EventType.STUDY_CURATOR_ASSIGNMENT_UNASSIGNED;
                break;
            case "GWAS Catalog":
                eventType = EventType.STUDY_CURATOR_ASSIGNMENT_GWAS_CATALOG;
                break;
            case "Level 2 Curator":
                eventType = EventType.STUDY_CURATOR_ASSIGNMENT_LEVEL_2_CURATOR;
                break;
            case "Level 1 Curator":
                eventType = EventType.STUDY_CURATOR_ASSIGNMENT_LEVEL_1_CURATOR;
                break;
            case "Level 1 Ethnicity Curator":
                eventType = EventType.STUDY_CURATOR_ASSIGNMENT_LEVEL_1_ETHNICITY_CURATOR;
                break;
            default:
                eventType = EventType.STUDY_CURATOR_ASSIGNMENT_UNKNOWN;
                break;
        }
        return eventType;
    }
}
