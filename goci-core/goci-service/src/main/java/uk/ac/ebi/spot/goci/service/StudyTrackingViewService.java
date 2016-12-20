package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.repository.StudyTrackingViewRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.repository.WeeklyTrackingRepository;


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


    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public StudyTrackingViewService (StudyTrackingViewRepository studyTrackingViewRepository,
                                     StudyRepository studyRepository,
                                     WeeklyTrackingService weeklyTrackingService,
                                     CuratorService curatorService,
                                     CuratorTrackingService curatorTrackingService) {
        this.studyTrackingViewRepository = studyTrackingViewRepository;
        this.studyRepository = studyRepository;
        this.weeklyTrackingService = weeklyTrackingService;
        this.curatorService = curatorService;
        this.curatorTrackingService = curatorTrackingService;
    }

    protected Logger getLog() {
        return log;
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
        //weeklyTrackingService.deleteAll();

        for(StudyTrackingView studyTracked : resultSet){
            if ((studyTracked.getStatus().equals("Unpublished from catalog")) | (studyTracked.getStatus().equals("CNV Paper")) |
                    (studyTracked.getStudyAddedDate() == null)) {
                //System.out.print("S");
            }
            else {
                Study study = studyRepository.findOne(studyTracked.getStudyId());
                Collection<Event> eventi = study.getEvents();

                if (eventi.isEmpty()) {
                    //System.out.println("No events associated");
                    Date eventDate = this.getCreationDateDefault(studyTracked);
                    WeeklyTracking newEntry = weeklyTrackingService.createAndSaveWeeklyTracking(studyTracked, study, eventDate, "Creation Study");

                    if (studyTracked.getIsPublished() == 1) {
                        Date eventDatePublished = studyTracked.getCatalogPublishDate();
                        WeeklyTracking newEntryPublished = weeklyTrackingService.createAndSaveWeeklyTracking(studyTracked, study, eventDatePublished, "Publication Study");
                    }
                }
                else {
                    Hashtable<String, WeeklyTracking> TrackerLevels = new Hashtable<String, WeeklyTracking>();
                    Boolean hasStudyCreationEvent = false;
                    Boolean is_unpublished = false;
                    Date eventDateCreation = this.getCreationDateDefault(studyTracked);
                    Date  eventDatePublished = null;
                    if (studyTracked.getIsPublished() == 1) { eventDatePublished = studyTracked.getCatalogPublishDate(); }
                    Date eventDateUnpublished;
                    Curator curator = null;

                    Iterator<Event> eventsIterator = eventi.iterator();

                    // if the study is unpublished the stats are already done.
                    while ((!is_unpublished) && (eventsIterator.hasNext())) {
                        Event event = eventsIterator.next();
                        //System.out.println(event.weekOfYear());
                        String eventType = event.getEventType();
                        if(eventType.toLowerCase().contains("STUDY_CURATOR_ASSIGNMENT_".toLowerCase())) { eventType = "STUDY_CURATOR_ASSIGNMENT_";}

                        switch (eventType) {
                            case "STUDY_CREATION":
                                eventDateCreation = event.getEventDate();
                                WeeklyTracking newEventQueue1 = weeklyTrackingService.createWeeklyTracking(studyTracked, study, eventDateCreation, "In level 1 queue");
                                TrackerLevels.put("0", newEventQueue1);
                                hasStudyCreationEvent = true;
                                break;
                            case "STUDY_STATUS_CHANGE_PUBLISH_STUDY":
                                eventDatePublished = event.getEventDate();
                                insertCuratorStats(studyTracked, study, event, curator, "Published");
                                break;
                            case "STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE":
                                if (hasStudyCreationEvent) {
                                    if (!TrackerLevels.containsKey("1")) {
                                        WeeklyTracking newEvent = weeklyTrackingService.createWeeklyTracking(studyTracked, study, event.getEventDate(), "In level 2 queue");
                                        TrackerLevels.put("1", newEvent);
                                        insertCuratorStats(studyTracked, study, event, curator, "Level_1");
                                    }
                                }
                                break;
                            case "STUDY_STATUS_CHANGE_LEVEL_2_CURATION_DONE":
                                if (hasStudyCreationEvent) {
                                    if (!TrackerLevels.containsKey("2")) {
                                        WeeklyTracking newEventLevel2 = weeklyTrackingService.createWeeklyTracking(studyTracked, study, event.getEventDate(), "In level 3 queue");
                                        TrackerLevels.put("2", newEventLevel2);
                                        insertCuratorStats(studyTracked, study, event, curator, "Level_2");
                                    }
                                }
                                break;
                            case "STUDY_STATUS_CHANGE_LEVEL_3_CURATION_DONE":
                                if (hasStudyCreationEvent) {
                                    if (!TrackerLevels.containsKey("3")) {
                                        WeeklyTracking newEventLevel3 = weeklyTrackingService.createWeeklyTracking(studyTracked, study, event.getEventDate(), "Publication queue");
                                        TrackerLevels.put("3", newEventLevel3);
                                        insertCuratorStats(studyTracked, study, event, curator, "Level_3");
                                    }
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
                            default:
                                //System.out.println("Event not managed");
                                break;
                        }


                    }

                    // Analisys Events complete. Filter by date and week/year
                    insertEventTracked(studyTracked,study,eventDateCreation,eventDatePublished,TrackerLevels,hasStudyCreationEvent);
                }

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



    /*
       If TrackersLevels is empty and the study has a creation event, the study goes to "waiting level curation 1".

    */
    private void insertEventTracked(StudyTrackingView studyTracked,Study study, Date eventDate_Creation, Date eventDatePublished,Hashtable<String, WeeklyTracking> TrackerLevels, Boolean hasCreationEvent ) {
        Integer weekPublication = 0;
        Integer yearPublication = 0;
        WeeklyTracking newEntry = weeklyTrackingService.createAndSaveWeeklyTracking(studyTracked, study, eventDate_Creation, "Creation Study");
        WeeklyTracking newEntryPublished = null;
        if (studyTracked.getIsPublished() == 1) {
            newEntryPublished = weeklyTrackingService.createAndSaveWeeklyTracking(studyTracked, study,eventDatePublished, "Publication Study");
        }

        if (TrackerLevels.isEmpty()) {
            if (hasCreationEvent) {
                WeeklyTracking newEntryLevel1Queue = weeklyTrackingService.createWeeklyTracking(studyTracked, study, eventDate_Creation, "In level 1 queue");
                // Check that the study was not published the same week. Priority Publication
                if ((weekPublication != newEntryLevel1Queue.getWeek()) & (yearPublication != newEntryLevel1Queue.getYear())) {
                    weeklyTrackingService.save(newEntryLevel1Queue);
                }
            }
        }
        else {
            /* We considere just the studies with all the events (Creation) */
            if (hasCreationEvent) {
                // Important procedure
                removeCollapseWeek(TrackerLevels, newEntryPublished);
                Enumeration e = TrackerLevels.keys();
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    WeeklyTracking record = TrackerLevels.get(key);
                    weeklyTrackingService.save(record);

                }
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


    // Try to figure out is the curator.
    // To improve: it might be enough to check the event creator.
    private void insertCuratorStats(StudyTrackingView studyTracked, Study study, Event event, Curator curator, String levelCuration) {
        String curatorLastName = null;
        Curator curatorByEmail = curatorService.getCuratorIdByEmail(event.getUser().getEmail());
        if ((curatorByEmail != null) && (curator != null)) {
            curatorLastName = curatorByEmail.getLastName();
            //(!curatorByEmail.getLastName().toLowerCase().equals(curator.getLastName().toLowerCase()))
        }

        if (curatorByEmail == null) {
            if (curator != null) {
                curatorLastName = curator.getLastName();
            }
        }
        else {
            curatorLastName = curatorByEmail.getLastName();
        }

        // I found a curator!
        if (curatorLastName != null) {
            curatorTrackingService.createCuratorTracking(studyTracked, study, event.getEventDate(), curatorLastName, levelCuration);
        }
        else {
            System.out.println("Impossible to retrieve the curator");
        }

    }


}


