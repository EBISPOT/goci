package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.repository.StudyTrackingViewRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.CuratorService;
import uk.ac.ebi.spot.goci.service.CuratorTrackingService;
import uk.ac.ebi.spot.goci.service.SQLDateUtilityService;
import uk.ac.ebi.spot.goci.service.WeeklyTrackingService;


import java.sql.Date;
import java.util.*;
import java.util.List;

/**
 * Created by cinzia on 23/11/2016.
 */

@Service
public class StudyTrackingViewService {
    private StudyTrackingViewRepository studyTrackingViewRepository;
    private StudyRepository studyRepository;
    private WeeklyTrackingService weeklyTrackingService;
    private CuratorTrackingService curatorTrackingService;
    private CuratorService curatorService;
    private SQLDateUtilityService sqlDateUtilityService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public StudyTrackingViewService (StudyTrackingViewRepository studyTrackingViewRepository,
                                     StudyRepository studyRepository,
                                     WeeklyTrackingService weeklyTrackingService,
                                     CuratorService curatorService,
                                     CuratorTrackingService curatorTrackingService,
                                     SQLDateUtilityService sqlDateUtilityService) {
        this.studyTrackingViewRepository = studyTrackingViewRepository;
        this.studyRepository = studyRepository;
        this.weeklyTrackingService = weeklyTrackingService;
        this.curatorService = curatorService;
        this.curatorTrackingService = curatorTrackingService;
        this.sqlDateUtilityService = sqlDateUtilityService;
    }

    protected Logger getLog() {
        return log;
    }



    // The event table was introduced around mid July 2016.
    // This method tries to create/ to simulate some events.
    private void simulateQueueForStudyPreEvent(StudyTrackingView studyTracked ,Study study) {
        Date eventDateCreation;
        Date eventDateLevel1Queue = null;
        Date eventDateLevel2Queue = null;
        Date eventDatePublished = null;
        WeeklyTracking newEntryPublished = null;
        WeeklyTracking newEntryCreation = null;

        eventDateCreation = studyTracked.getStudyAddedDate();

        if (studyTracked.getIsPublished() == 1) {
            eventDatePublished = studyTracked.getCatalogPublishDate();
            newEntryPublished = weeklyTrackingService.createAndSaveWeeklyTracking(studyTracked, study,
                                eventDatePublished, sqlDateUtilityService.getCalendarDate(eventDatePublished),
                                "Publication Study");
        }

        if (studyTracked.getCatalogPublishDate() != null) {
            if (studyTracked.getCatalogPublishDate().toLocalDate().isBefore(studyTracked.getStudyAddedDate().toLocalDate())) {
                eventDateLevel1Queue = sqlDateUtilityService.getXWeekBefore(studyTracked.getCatalogPublishDate(), -14);
                eventDateCreation = eventDateLevel1Queue;
                eventDateLevel2Queue = sqlDateUtilityService.getXWeekBefore(studyTracked.getCatalogPublishDate(), -7);
            }
        }

        newEntryCreation = weeklyTrackingService.createAndSaveWeeklyTracking(studyTracked, study, eventDateCreation,
                           sqlDateUtilityService.getCalendarDate(eventDateCreation), "Creation Study");

        if (eventDateLevel1Queue != null) {
            weeklyTrackingService.createAndSaveWeeklyTracking(studyTracked, study,
                                        eventDateLevel1Queue, sqlDateUtilityService.getCalendarDate(eventDateLevel1Queue),
                                        "In level 1 queue");
            weeklyTrackingService.createAndSaveWeeklyTracking(studyTracked, study,
                                        eventDateLevel2Queue, sqlDateUtilityService.getCalendarDate(eventDateLevel2Queue),
                                        "In level 2 queue");
        }
        else {
            if (newEntryPublished != null) {
                if (newEntryCreation.beforeWeekYear(newEntryPublished)) {
                    weeklyTrackingService.createAndSaveWeeklyTracking(studyTracked, study,
                            eventDateCreation, sqlDateUtilityService.getCalendarDate(eventDateCreation),
                            "In level 1 queue");
                    // Fuzzy logic.
                    long daysBeetweenDates =  sqlDateUtilityService.daysBetweenTwoDates(eventDateCreation, eventDatePublished);
                    if (daysBeetweenDates > 13) {
                        eventDateLevel2Queue = sqlDateUtilityService.getXWeekBefore(studyTracked.getCatalogPublishDate(), -7);
                        weeklyTrackingService.createAndSaveWeeklyTracking(studyTracked, study,
                                eventDateLevel2Queue, sqlDateUtilityService.getCalendarDate(eventDateLevel2Queue),
                                "In level 2 queue");
                    }
                }
                else {
                    //System.out.println("Same date: " + eventDatePublished.toString());
                }
            }
            else {
                WeeklyTracking queueEntry = weeklyTrackingService.createAndSaveWeeklyTracking(studyTracked, study,
                        eventDateCreation, sqlDateUtilityService.getCalendarDate(eventDateCreation),
                        "In level 1 queue");
            }

        }

    }


    /** README
     *  A study can be followed if it has a "creation" event created.
     *  TrackerLevels saves the first event recorded where a curation was set to "done" (x level)
     *  If the curation was already done, the algorithm hold the first value.
     *  The procedure insertEventTracked checks and define the criteria.
     */
    @Transactional(readOnly = false)
    public void generateReport() {
        List<StudyTrackingView> resultSet = studyTrackingViewRepository.findAll();

        for(StudyTrackingView studyTracked : resultSet){
            if ((studyTracked.getStatus().equals("Unpublished from catalog")) | (studyTracked.getStatus().equals("CNV Paper")) |
                    (studyTracked.getStudyAddedDate() == null)) {
                //System.out.print("Study skipped");
            }
            else {
                Study study = studyRepository.findOne(studyTracked.getStudyId());
                //System.out.println(study.getId().toString());
                if (study.getHousekeeping().getCurationStatus().getStatus().compareTo("Curation Abandoned") != 0) {
                    Collection<Event> eventi = study.getEvents();

                    if (eventi.isEmpty()) {
                        simulateQueueForStudyPreEvent(studyTracked, study);
                    } else {
                        Hashtable<String, WeeklyTracking> TrackerLevels = new Hashtable<String, WeeklyTracking>();
                        Boolean hasStudyCreationEvent = false;
                        Boolean is_unpublished = false;
                        Boolean is_abandoned = false;
                        Date eventDateCreation = this.getCreationDateDefault(studyTracked);
                        Date eventDatePublished = null;
                        if (studyTracked.getIsPublished() == 1) {
                            eventDatePublished = studyTracked.getCatalogPublishDate();
                        }
                        Date eventDateUnpublished;
                        Curator curator = null;
                        Boolean hasStatusPublished = false;

                        Iterator<Event> eventsIterator = eventi.iterator();

                        // if the study is unpublished the stats are already done.
                        while ((!is_unpublished) && (eventsIterator.hasNext())) {
                            Event event = eventsIterator.next();
                            String eventType = event.getEventType();
                            if (eventType.toLowerCase().contains("STUDY_CURATOR_ASSIGNMENT_".toLowerCase())) {
                                eventType = "STUDY_CURATOR_ASSIGNMENT_";
                            }

                            switch (eventType) {
                                case "STUDY_CREATION":
                                    eventDateCreation = event.getEventDate();
                                    TrackerLevels.put("0", createWeeklyEntry(studyTracked, study,eventDateCreation,
                                            "In level 1 queue"));
                                    hasStudyCreationEvent = true;
                                    break;
                                case "STUDY_STATUS_CHANGE_PUBLISH_STUDY":
                                    if (!hasStatusPublished) {
                                        eventDatePublished = event.getEventDate();
                                        insertCuratorStats(studyTracked, study, event, curator, "Published");
                                        hasStatusPublished = true;
                                    }
                                    break;
                                case "STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE":
                                    if (!TrackerLevels.containsKey("1")) {
                                        TrackerLevels.put("1", createWeeklyEntry(studyTracked, study,
                                                event.getEventDate(), "In level 2 queue"));
                                        insertCuratorStats(studyTracked, study, event, curator, "Level_1");
                                    }
                                    break;
                                case "STUDY_STATUS_CHANGE_LEVEL_2_CURATION_DONE":
                                    if (!TrackerLevels.containsKey("2")) {
                                        TrackerLevels.put("2", createWeeklyEntry(studyTracked, study,
                                                event.getEventDate(), "In level 3 queue"));
                                        insertCuratorStats(studyTracked, study, event, curator, "Level_2");
                                    }
                                    break;
                                case "STUDY_STATUS_CHANGE_LEVEL_3_CURATION_DONE":
                                    if (!TrackerLevels.containsKey("3")) {
                                        TrackerLevels.put("3", createWeeklyEntry(studyTracked, study,
                                                event.getEventDate(), "Publication queue"));
                                        insertCuratorStats(studyTracked, study, event, curator, "Level_3");
                                    }
                                    break;
                                case "STUDY_STATUS_CHANGE_UNPUBLISHED_FROM_CATALOG":
                                    eventDateUnpublished = event.getEventDate();
                                    is_unpublished = true;
                                    break;
                                case "STUDY_CURATOR_ASSIGNMENT_":
                                    String nameCurator = event.getEventType().replace("STUDY_CURATOR_ASSIGNMENT_", "");
                                    curator = curatorService.findByLastNameIgnoreCase(nameCurator);
                                    break;
                                case "STUDY_STATUS_CHANGE_CURATION_ABANDONED":
                                    is_abandoned = true;
                                    break;
                                default:
                                    //System.out.println("Event not managed");
                                    break;
                            }

                        }

                        // Analisys Events complete. Filter by date and week/year
                        if (!is_abandoned) {
                            insertEventTracked(studyTracked, study, eventDateCreation, eventDatePublished, TrackerLevels, hasStudyCreationEvent);
                        }
                    }
                }
                //else { System.out.println("Abandoned!");}
            }
            // Unpublished from catalog or CNV Paper
        }
        //System.out.println("generate Report Done");
    }



    // The old studies have creation and publication dates with strange values.
    // This procedure tries to give valid dates for the old studies.
    protected Date getCreationDateDefault(StudyTrackingView studyTracked) {
        Date publication_date;

        publication_date = studyTracked.getStudyAddedDate();

        if (studyTracked.getCatalogPublishDate() != null) {
            if (studyTracked.getCatalogPublishDate().toLocalDate().isBefore(studyTracked.getStudyAddedDate().toLocalDate())) {
                publication_date = studyTracked.getCatalogPublishDate();
            }
        }

        return publication_date;
    };


    public WeeklyTracking createWeeklyEntry(StudyTrackingView studyTracked, Study study, Date dateEvent, String status) {

        WeeklyTracking entry = weeklyTrackingService.createWeeklyTracking(studyTracked, study,
                dateEvent, sqlDateUtilityService.getCalendarDate(dateEvent), status);

        return entry;
    }


    /*
       If TrackersLevels is empty and the study has a creation event, the study goes to "waiting level curation 1".
    */
    private void insertEventTracked(StudyTrackingView studyTracked,Study study, Date eventDateCreation, Date eventDatePublished,Hashtable<String, WeeklyTracking> TrackerLevels, Boolean hasCreationEvent ) {
        Integer weekPublication = 0;
        Integer yearPublication = 0;
        WeeklyTracking newEntry = weeklyTrackingService.createAndSaveWeeklyTracking(studyTracked, study,
                eventDateCreation, sqlDateUtilityService.getCalendarDate(eventDateCreation), "Creation Study");
        WeeklyTracking newEntryPublished = null;

        if ((!hasCreationEvent) && (TrackerLevels.get(0) == null)) {
            WeeklyTracking newEventQueue1 = weeklyTrackingService.createWeeklyTracking(studyTracked, study,
                    eventDateCreation, sqlDateUtilityService.getCalendarDate(eventDateCreation), "In level 1 queue");
            TrackerLevels.put("0", newEventQueue1);
        }


        if (studyTracked.getIsPublished() == 1) {
            newEntryPublished = weeklyTrackingService.createAndSaveWeeklyTracking(studyTracked, study,
                    eventDatePublished, sqlDateUtilityService.getCalendarDate(eventDatePublished), "Publication Study");
        }

        if (TrackerLevels.isEmpty()) {
            WeeklyTracking newEntryLevel1Queue = weeklyTrackingService.createWeeklyTracking(studyTracked, study,
                    eventDateCreation, sqlDateUtilityService.getCalendarDate(eventDateCreation),"In level 1 queue");
            // Check that the study was not published the same week. Priority Publication
            if ((weekPublication != newEntryLevel1Queue.getWeek()) & (yearPublication != newEntryLevel1Queue.getYear())) {
                weeklyTrackingService.save(newEntryLevel1Queue);
            }
        }
        else {
            /* Split the logic business: we considere just the studies with all the events (Creation) */
                removeCollapseWeek(TrackerLevels, newEntryPublished);
                Enumeration e = TrackerLevels.keys();
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    WeeklyTracking record = TrackerLevels.get(key);
                    weeklyTrackingService.save(record);

                }
            }
        }

    /*
       The hash (TrackerLevels) could have 3 entries.
       If two or more events happened in the same week/year the priority is the following:
       Published, level 3, level 2 , level 1
    */
    private void removeCollapseWeek(Hashtable<String, WeeklyTracking> TrackerLevels, WeeklyTracking newEntryPublished) {
        WeeklyTracking weekToCompare = null;

        if (newEntryPublished != null) {
            weekToCompare = newEntryPublished;
        }

        Integer level = 3;
        while (0 <= level) {
            String key = level.toString();
            if (TrackerLevels.containsKey(key)) {
                //Compare that the week are not the same.
                WeeklyTracking TrackerLevelsBelow = TrackerLevels.get(key);
                if (weekToCompare == null) {
                    weekToCompare= TrackerLevels.get(key);
                }
                else {
                    if (!TrackerLevelsBelow.beforeWeekYear(weekToCompare)) {
                        // Conflict of week
                        TrackerLevels.remove(key);
                    } else {
                        // different week
                        weekToCompare = TrackerLevels.get(key);
                    }
                }
            }
            level--;
        }

    }


    // Try to figure out who is the curator. Secure_user and curator are two different table.
    // To improve: it might be enough to check the event creator (refactor secure_user/curator)
    private void insertCuratorStats(StudyTrackingView studyTracked, Study study, Event event, Curator curator, String levelCuration) {
        String curatorLastName = null;
        Curator curatorByEmail = curatorService.getCuratorIdByEmail(event.getUser().getEmail());
        if ((curatorByEmail != null) && (curator != null)) { curatorLastName = curatorByEmail.getLastName(); }

        if (curatorByEmail == null) {
            if (curator != null) { curatorLastName = curator.getLastName(); }
        }
        else { curatorLastName = curatorByEmail.getLastName(); }

        // Curator found
        if (curatorLastName != null) {
            curatorTrackingService.createCuratorTracking(studyTracked, study, event.getEventDate(),
                    curatorLastName, levelCuration);
        }
        else { System.out.println("Impossible to retrieve the curator"); }

    }


}


