package uk.ac.ebi.spot.goci.curation.service;

import uk.ac.ebi.spot.goci.curation.model.EventView;
import uk.ac.ebi.spot.goci.model.EventType;

import java.util.List;

/**
 * Created by emma on 22/07/2016.
 *
 * @author emma
 *         <p>
 *         Interface used to create a human readable version of event tracking that can be displayed via curation
 *         interface
 */
public interface EventsViewService {

    List<EventView> createViews(Long trackableId);

    /**
     * Default method to translate EventTypes into human readable form
     *
     * @param eventType
     */
    default String translateEventEnum(EventType eventType) {
        String translatedEvent = "";
        switch (eventType) {
            case STUDY_CREATION:
                translatedEvent = "Study created";
                break;
            case STUDY_STATUS_CHANGE_LEVEL_1_ANCESTRY_DONE:
                translatedEvent = "Study status changed to level 1 ancestry done";
                break;
            case STUDY_STATUS_CHANGE_LEVEL_2_ANCESTRY_DONE:
                translatedEvent = "Study status changed to level 2 ancestry done";
                break;
            case STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE:
                translatedEvent = "Study status changed to level 1 curation done";
                break;
            case STUDY_STATUS_CHANGE_LEVEL_2_CURATION_DONE:
                translatedEvent = "Study status changed to level 2 curation done";
                break;
            case STUDY_STATUS_CHANGE_PUBLISH_STUDY:
                translatedEvent = "Study status changed to publish study";
                break;
            case STUDY_STATUS_CHANGE_AWAITING_CURATION:
                translatedEvent = "Study status changed to awaiting curation";
                break;
            case STUDY_STATUS_CHANGE_OUTSTANDING_QUERY:
                translatedEvent = "Study status changed to outstanding query";
                break;
            case STUDY_STATUS_CHANGE_CNV_PAPER:
                translatedEvent = "Study status changed to cnv paper";
                break;
            case STUDY_STATUS_CHANGE_CURATION_ABANDONED:
                translatedEvent = "Study status changed to curation abandoned";
                break;
            case STUDY_STATUS_CHANGE_CONVERSION_PROBLEM:
                translatedEvent = "Study status changed to conversion problem";
                break;
            case STUDY_STATUS_CHANGE_UNPUBLISHED_FROM_CATALOG:
                translatedEvent = "Study status changed to unpublished from catalog";
                break;
            case STUDY_STATUS_CHANGE_PENDING_AUTHOR_QUERY:
                translatedEvent = "Study status changed to pending author query";
                break;
            case STUDY_STATUS_CHANGE_AWAITING_EFO_ASSIGNMENT:
                translatedEvent = "Study status changed to awaiting efo assignment";
                break;
            case STUDY_STATUS_CHANGE_UNKNOWN:
                translatedEvent = "Study status changed to unknown";
                break;
            case STUDY_CURATOR_ASSIGNMENT_MORALES:
                translatedEvent = "Study curator set to Morales";
                break;
            case STUDY_CURATOR_ASSIGNMENT_MACARTHUR:
                translatedEvent = "Study curator set to MacArthur";
                break;
            case STUDY_CURATOR_ASSIGNMENT_HINDORFF:
                translatedEvent = "Study curator set to  Hindorff";
                break;
            case STUDY_CURATOR_ASSIGNMENT_JUNKINS:
                translatedEvent = "Study curator set to Junkins";
                break;
            case STUDY_CURATOR_ASSIGNMENT_HALL:
                translatedEvent = "Study curator set to Hall";
                break;
            case STUDY_CURATOR_ASSIGNMENT_WELTER:
                translatedEvent = "Study curator set to Welter";
                break;
            case STUDY_CURATOR_ASSIGNMENT_UNASSIGNED:
                translatedEvent = "Study curator set to Unassigned";
                break;
            case STUDY_CURATOR_ASSIGNMENT_GWAS_CATALOG:
                translatedEvent = "Study curator set to Gwas Catalog";
                break;
            case STUDY_CURATOR_ASSIGNMENT_LEVEL_2_CURATOR:
                translatedEvent = "Study curator set to level 2 curator";
                break;
            case STUDY_CURATOR_ASSIGNMENT_LEVEL_1_CURATOR:
                translatedEvent = "Study curator set to level 1 curator";
                break;
            case STUDY_CURATOR_ASSIGNMENT_LEVEL_1_ETHNICITY_CURATOR:
                translatedEvent = "Study curator set to level 1 ethnicity curator";
                break;
            case STUDY_CURATOR_ASSIGNMENT_CEREZO:
                translatedEvent = "Study curator set to Cerezo";
                break;
            case STUDY_CURATOR_ASSIGNMENT_MILANO:
                translatedEvent = "Study curator set to Milano";
                break;
            case STUDY_CURATOR_ASSIGNMENT_MCMAHON:
                translatedEvent = "Study curator set to McMahon";
                break;
            case STUDY_CURATOR_ASSIGNMENT_UNKNOWN:
                translatedEvent = "Study curator assignment unknown";
                break;
            case STUDY_FILE_UPLOAD:
                translatedEvent = "File uploaded";
                break;
            case STUDY_UPDATE:
                translatedEvent = "Study updated";
                break;
            case STUDY_DELETION:
                translatedEvent = "Study deleted";
                break;
            case STUDY_DUPLICATION:
                translatedEvent = "Study duplicated";
                break;
            case ASSOCIATION_CREATION:
                translatedEvent = "Association created";
                break;
            case ASSOCIATION_UPDATE:
                translatedEvent = "Association updated";
                break;
            case ASSOCIATION_MAPPING:
                translatedEvent = "Association mapped";
                break;
            case ASSOCIATION_APPROVED:
                translatedEvent = "Association approved";
                break;
            case ASSOCIATION_UNAPPROVED:
                translatedEvent = "Association unapproved";
                break;
            case ASSOCIATION_DELETION:
                translatedEvent = "Association deleted";
                break;
        }
        return translatedEvent;
    }
}