package uk.ac.ebi.spot.goci.curation.service.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.reports.PublicationWeeklyProgressStatus;
import uk.ac.ebi.spot.goci.curation.model.reports.ReportsWeeklyProgressView;
import uk.ac.ebi.spot.goci.model.WeeklyProgressView;
import uk.ac.ebi.spot.goci.repository.WeeklyProgressViewRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Map;

/**
 * Created by emma on 08/06/2016.
 *
 * @author emma
 *         <p>
 *         Service to create weekly progress views
 */
@Lazy
@Service
public class WeeklyProgressService {

    private WeeklyProgressViewRepository weeklyProgressViewRepository;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public WeeklyProgressService(WeeklyProgressViewRepository weeklyProgressViewRepository) {
        this.weeklyProgressViewRepository = weeklyProgressViewRepository;
    }

    public List<ReportsWeeklyProgressView> processWeeklyView() {

        getLog().info("Creating weekly progress view");

        Set<Long> previouslyPublished_Studies = new HashSet<>();
        Set<Long> previousLevel1_CurationDone_Studies = new HashSet<>();
        Set<Long> previousLevel2_CurationDone_Studies = new HashSet<>();

        // Get all details from database
        List<WeeklyProgressView> weeklyProgressViews = weeklyProgressViewRepository.findAll();

        // Create a list of views that will be returned to the controller and passed to the view
        List<ReportsWeeklyProgressView> reportsWeeklyProgressViews = new ArrayList<>();

        // As a convenience get a unique set of week start dates -> Replaced w/Custom Query, see "uniqueWeekStartDate"
//        Set<Date> uniqueWeekSet = weeklyProgressViews.stream()
//                .map(WeeklyProgressView::getWeekStartDay)
//                .distinct()
//                .collect(Collectors.toSet());
//        uniqueWeekSet.forEach(date -> {

        // Get map of all publications and their studies as a Custom Query
        List<Map.Entry> publicationToStudies = weeklyProgressViewRepository.getAllPublicationToStudyMappings();

        // Create map of pmid to PublicationWeeklyProgressStatus objects
        HashMap<String, PublicationWeeklyProgressStatus> pwpsHM = new HashMap<>();

        // Iterate through publicationToStudies to create individual PublicationWeeklyProgressStatus objects
        for (java.util.Map.Entry entry: publicationToStudies){
            String pmid = entry.getKey().toString();
            List studyIds = new ArrayList<>(Arrays.asList(entry.getValue().toString().split("\\s*,\\s*")));

            // Create PublicationWeeklyProgressStatus object with pmid and studyIds
            // to track when all studies for a given publication have reached the status of interest
            PublicationWeeklyProgressStatus publicationWeeklyProgressStatus = new PublicationWeeklyProgressStatus(pmid, studyIds);

            // Add to map
            pwpsHM.put(pmid, publicationWeeklyProgressStatus);
        }

        // Get all unique start week dates as Custom Query, order by WEEK_START_DAY ASC
        List<Date> uniqueWeekStartDate = weeklyProgressViewRepository.getAllWeekStartDates();

        uniqueWeekStartDate.forEach((Date date) -> {
            System.out.println("\n\n** Week Start Date: " + date);

            // For each week start date create a view object
            ReportsWeeklyProgressView reportsWeeklyProgressView = new ReportsWeeklyProgressView(date);


            /**
             * Get a unique set of studyIDs set for the first time at Created and
             * PubMedIDs where all of it's studies have also been set at Created at some time.
             */
            Map<String, List<WeeklyProgressView>> collect = weeklyProgressViews.stream()
                    .filter(weeklyProgressView -> weeklyProgressView.getWeekStartDay().equals(date))
                    .filter(weeklyProgressView -> weeklyProgressView.getEventType().equals(
                            "STUDY_CREATION"))
                    .collect(Collectors.groupingBy(WeeklyProgressView::getPubmedId));

            // Iterate over Map
            Iterator<Map.Entry<String, List<WeeklyProgressView>>> it = collect.entrySet().iterator();
            Set<Long> studiesCreatedThatWeek = new HashSet<>();
            Set<String> publicationsCreatedThatWeek = new HashSet<>();

            while (it.hasNext()) {
                Map.Entry<String, List<WeeklyProgressView>> pair = it.next();
                String pmid = pair.getKey().toString();

                int counter = 0;
                for (WeeklyProgressView item : pair.getValue()) {

                    if (!studiesCreatedThatWeek.contains(item.getStudyId())) {
                        counter++;
                        studiesCreatedThatWeek.add((item.getStudyId()));
                    }
                    pwpsHM.get(pmid).setCount_Created(pwpsHM.get(pmid).getCount_Created() + counter);
                }

                // Check if total number of created studies equals number of all studies for publication
                Long totalNumStudies = pwpsHM.get(pmid).getTotalStudyCount();
                Long totalStudiesCreatedToDate = pwpsHM.get(pmid).getCount_Created();

                if (totalStudiesCreatedToDate.equals(totalNumStudies)) {
                    if (!publicationsCreatedThatWeek.contains(pmid)) {
                        publicationsCreatedThatWeek.add(pwpsHM.get(pmid).getPubmedId());
                    }
                }
            }


            /**
             * Get a unique set of studyIDs set for the first time at Published and
             * PubMedIDs where all of it's studies have also been set at Published at some time.
             */
            Map<String, List<WeeklyProgressView>> collectPublished = weeklyProgressViews.stream()
                    .filter(weeklyProgressView -> weeklyProgressView.getWeekStartDay().equals(date))
                    .filter(weeklyProgressView -> weeklyProgressView.getEventType().equals(
                            "STUDY_STATUS_CHANGE_PUBLISH_STUDY"))
                    .collect(Collectors.groupingBy(WeeklyProgressView::getPubmedId));

            // Iterate over Map
            Iterator<Map.Entry<String, List<WeeklyProgressView>>> itLevelPublished = collectPublished.entrySet().iterator();
            Set<Long> studiesPublishedThatWeek = new HashSet<>();
            Set<String> publicationsPublishedThatWeek = new HashSet<>();

            while (itLevelPublished.hasNext()) {
                Map.Entry<String, List<WeeklyProgressView>> pairPublished = itLevelPublished.next();
                String pmidPublished = pairPublished.getKey().toString();

                Long totalNumStudies = pwpsHM.get(pmidPublished).getTotalStudyCount();

                for (WeeklyProgressView itemPublished : pairPublished.getValue()) {

                    // Check that StudyId was never set to this status before
                    if (!previouslyPublished_Studies.contains(itemPublished.getStudyId())) {
                        studiesPublishedThatWeek.add((itemPublished.getStudyId()));

                        pwpsHM.get(pmidPublished).setCount_Published(pwpsHM.get(pmidPublished).getCount_Published()+ 1L);

                        // Check if total number of published studies equals number of all studies for publication
                        if(pwpsHM.get(pmidPublished).getCount_Published().equals(totalNumStudies)) {

                            if (!publicationsPublishedThatWeek.contains(pmidPublished)) {
                                publicationsPublishedThatWeek.add(pwpsHM.get(pmidPublished).getPubmedId());
                            }
                        }

                        // Add previously seen StudyIds that are Published to "previouslyPublished_Studies"
                        previouslyPublished_Studies.add(itemPublished.getStudyId());
                    }
                }
            }


            /**
             * Get a unique set of studyIDs set for the first time at Level 1 and
             * PubMedIDs where all of it's studies have also been set at Level 1 at some time.
             */
            Map<String, List<WeeklyProgressView>> collectLevel1 = weeklyProgressViews.stream()
                    .filter(weeklyProgressView -> weeklyProgressView.getWeekStartDay().equals(date))
                    .filter(weeklyProgressView -> weeklyProgressView.getEventType().equals(
                            "STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE"))
                    .collect(Collectors.groupingBy(WeeklyProgressView::getPubmedId));

            // Iterate over Map
            Iterator<Map.Entry<String, List<WeeklyProgressView>>> itLevel1 = collectLevel1.entrySet().iterator();
            Set<Long> studiesWithLevel1Completed = new HashSet<>();
            Set<String> publicationsWithLevel1Completed = new HashSet<>();

            while (itLevel1.hasNext()) {
                Map.Entry<String, List<WeeklyProgressView>> pairLevel1 = itLevel1.next();
                String pmidLevel1 = pairLevel1.getKey().toString();

                Long totalNumStudies = pwpsHM.get(pmidLevel1).getTotalStudyCount();

                for (WeeklyProgressView itemLevel1 : pairLevel1.getValue()) {

                    // Check that StudyId was never set to this status before
                    if (!previousLevel1_CurationDone_Studies.contains(itemLevel1.getStudyId())) {
                        studiesWithLevel1Completed.add((itemLevel1.getStudyId()));

                        pwpsHM.get(pmidLevel1).setCount_Level1_CurationDone(pwpsHM.get(pmidLevel1).getCount_Level1_CurationDone()+ 1L);

                        // Check if total number of created studies equals number of all studies for publication
                        if(pwpsHM.get(pmidLevel1).getCount_Level1_CurationDone().equals(totalNumStudies)) {

                            if (!publicationsWithLevel1Completed.contains(pmidLevel1)) {
                                publicationsWithLevel1Completed.add(pwpsHM.get(pmidLevel1).getPubmedId());
                            }
                        }
                        // Add previously seen StudyIds at Level 1 to "previousLevel1_CurationDone_Studies"
                        previousLevel1_CurationDone_Studies.add(itemLevel1.getStudyId());
                    }
                }
            }



            /**
             * Get a unique set of studyIDs set for the first time at Level 2 and
             * PubMedIDs where all of it's studies have also been set at Level 2 at some time.
             */
            Map<String, List<WeeklyProgressView>> collectLevel2 = weeklyProgressViews.stream()
                    .filter(weeklyProgressView -> weeklyProgressView.getWeekStartDay().equals(date))
                    .filter(weeklyProgressView -> weeklyProgressView.getEventType().equals(
                            "STUDY_STATUS_CHANGE_LEVEL_2_CURATION_DONE"))
                    .collect(Collectors.groupingBy(WeeklyProgressView::getPubmedId));

            // Iterate over Map
            Iterator<Map.Entry<String, List<WeeklyProgressView>>> itLevel2 = collectLevel2.entrySet().iterator();
            Set<Long> studiesWithLevel2Completed = new HashSet<>();
            Set<String> publicationsWithLevel2Completed = new HashSet<>();

            while (itLevel2.hasNext()) {
                Map.Entry<String, List<WeeklyProgressView>> pairLevel2 = itLevel2.next();
                String pmidLevel2 = pairLevel2.getKey().toString();

                Long totalNumStudies = pwpsHM.get(pmidLevel2).getTotalStudyCount();

                for (WeeklyProgressView itemLevel2 : pairLevel2.getValue()) {

                    // Check that StudyId was never set to this status before
                    if (!previousLevel2_CurationDone_Studies.contains(itemLevel2.getStudyId())) {
                        studiesWithLevel2Completed.add((itemLevel2.getStudyId()));

                        pwpsHM.get(pmidLevel2).setCount_Level2_CurationDone(pwpsHM.get(pmidLevel2).getCount_Level2_CurationDone()+ 1L);

                        // Check if total number of created studies equals number of all studies for publication
                        if(pwpsHM.get(pmidLevel2).getCount_Level2_CurationDone().equals(totalNumStudies)) {

                            if (!publicationsWithLevel2Completed.contains(pmidLevel2)) {
                                publicationsWithLevel2Completed.add(pwpsHM.get(pmidLevel2).getPubmedId());
                            }
                        }
                        // Add previously seen StudyIds at Level 2 to "previousLevel2_CurationDone_Studies"
                        previousLevel2_CurationDone_Studies.add(itemLevel2.getStudyId());
                    }
                }
            }


            // Add sets to view object
            reportsWeeklyProgressView.setStudiesCreated(studiesCreatedThatWeek);
            reportsWeeklyProgressView.setStudiesPublished(studiesPublishedThatWeek);
            reportsWeeklyProgressView.setStudiesLevel1Completed(studiesWithLevel1Completed);
            reportsWeeklyProgressView.setStudiesLevel2Completed(studiesWithLevel2Completed);

            reportsWeeklyProgressView.setPublicationsCreated(publicationsCreatedThatWeek);
            reportsWeeklyProgressView.setPublicationsLevel1Completed(publicationsWithLevel1Completed);
            reportsWeeklyProgressView.setPublicationsLevel2Completed(publicationsWithLevel2Completed);
            reportsWeeklyProgressView.setPublicationsPublished(publicationsPublishedThatWeek);

            reportsWeeklyProgressViews.add(reportsWeeklyProgressView);
        });
        return reportsWeeklyProgressViews;
    }
}
