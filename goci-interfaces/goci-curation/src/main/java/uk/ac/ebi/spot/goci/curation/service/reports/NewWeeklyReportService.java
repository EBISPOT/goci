package uk.ac.ebi.spot.goci.curation.service.reports;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.reports.PublicationWeeklyProgressStatus;
import uk.ac.ebi.spot.goci.curation.model.reports.ReportsWeeklyProgressView;
import uk.ac.ebi.spot.goci.curation.service.mail.MailService;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.WeeklyProgressView;
import uk.ac.ebi.spot.goci.model.reports.WeeklyReport;
import uk.ac.ebi.spot.goci.repository.PublicationRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.repository.WeeklyProgressViewRepository;
import uk.ac.ebi.spot.goci.repository.WeeklyReportRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NewWeeklyReportService {

    private static final String ALL = "ALL";

    private static final String OPEN_TARGETS = "OPEN_TARGETS";

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    private WeeklyProgressViewRepository weeklyProgressViewRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private WeeklyReportRepository weeklyReportRepository;

    @Autowired
    private MailService mailService;

    @Scheduled(cron = "0 30 0 * * SUN")
    public void createWeeklyReports() {
        log.info("Creating weekly reports ...");
        double sTime = System.currentTimeMillis();

        List<String> pmids = publicationRepository.findAllPubmedIds();
        Map<String, List<String>> allPublications = new HashMap<>();
        Map<String, List<String>> openTargetsPublications = new HashMap<>();

        for (String pmid : pmids) {
            boolean isOT = false;
            Collection<Study> studies = studyRepository.findTop10ByPublicationIdPubmedId(pmid);
            List<String> ids = new ArrayList<>();

            for (Study study : studies) {
                if (study.getOpenTargets() != null) {
                    if (study.getOpenTargets().booleanValue()) {
                        isOT = true;
                    }
                }
                ids.add(Long.toString(study.getId()));
            }

            allPublications.put(pmid, ids);
            if (isOT) {
                openTargetsPublications.put(pmid, ids);
            }
        }

        Calendar todayCalendar = Calendar.getInstance();
        Date today = todayCalendar.getTime();
        int weekCode = todayCalendar.get(Calendar.WEEK_OF_YEAR);

        try {
            createSpecificWeeklyReports(allPublications, ALL, today, weekCode);
            createSpecificWeeklyReports(openTargetsPublications, OPEN_TARGETS, today, weekCode);
        } catch (Exception e) {
            log.error("ERROR: Unable to create weekly reports: {}", e.getMessage(), e);
            mailService.sendWeeklyReportErrorCreationEmail(StringUtils.join(Arrays.asList(e.getStackTrace()), "\n"));
        }

        double eTime = System.currentTimeMillis();
        double total = (eTime - sTime) / 1000;
        log.info("Done creating weekly reports: {}s", total);
    }

    private void createSpecificWeeklyReports(Map<String, List<String>> publicationToStudies, String type, Date timestamp, long weekCode) {
        List<ReportsWeeklyProgressView> reportsWeeklyProgressViews = new ArrayList<>();

        Set<Long> previouslyPublished_Studies = new HashSet<>();
        Set<Long> previousLevel1_CurationDone_Studies = new HashSet<>();
        Set<Long> previousLevel2_CurationDone_Studies = new HashSet<>();

        // Get all details from database
        List<WeeklyProgressView> weeklyProgressViews = weeklyProgressViewRepository.findAll();

        // Create map of pmid to PublicationWeeklyProgressStatus objects
        HashMap<String, PublicationWeeklyProgressStatus> publicationWeeklyProgressStatusData = new HashMap<>();
        List<String> allPMIDs = new ArrayList<>();

        // Iterate through publicationToStudies to create individual PublicationWeeklyProgressStatus objects
        for (String pmid : publicationToStudies.keySet()) {
            List studyIds = publicationToStudies.get(pmid);

            // Create list of all PMIDs in the data set to analyze, e.g. use with data filtered in query (targeted arrays)
            allPMIDs.add(pmid);

            // Create PublicationWeeklyProgressStatus object with pmid and studyIds
            // to track when all studies for a given publication have reached the status of interest
            PublicationWeeklyProgressStatus publicationWeeklyProgressStatus = new PublicationWeeklyProgressStatus(pmid, studyIds);

            // Add to map
            publicationWeeklyProgressStatusData.put(pmid, publicationWeeklyProgressStatus);
        }


        // Get all unique start week dates as Custom Query, order by WEEK_START_DAY ASC
        List<Date> uniqueWeekStartDate = weeklyProgressViewRepository.getAllWeekStartDates();

        uniqueWeekStartDate.forEach((Date date) -> {

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
                String pmid = pair.getKey();

                if (allPMIDs.contains(pmid)) {

                    int counter = 0;
                    for (WeeklyProgressView item : pair.getValue()) {

                        if (!studiesCreatedThatWeek.contains(item.getStudyId())) {
                            counter++;
                            studiesCreatedThatWeek.add((item.getStudyId()));
                        }
                        publicationWeeklyProgressStatusData.get(pmid).setCount_Created(publicationWeeklyProgressStatusData.get(pmid).getCount_Created() + counter);
                    }

                    // Check if total number of created studies equals number of all studies for publication
                    Long totalNumStudies = publicationWeeklyProgressStatusData.get(pmid).getTotalStudyCount();
                    Long totalStudiesCreatedToDate = publicationWeeklyProgressStatusData.get(pmid).getCount_Created();

                    if (totalStudiesCreatedToDate.equals(totalNumStudies)) {
                        if (!publicationsCreatedThatWeek.contains(pmid)) {
                            publicationsCreatedThatWeek.add(publicationWeeklyProgressStatusData.get(pmid).getPubmedId());
                        }
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
                String pmidPublished = pairPublished.getKey();

                // Check if PMID in data set
                if (allPMIDs.contains(pmidPublished)) {

                    Long totalNumStudies = publicationWeeklyProgressStatusData.get(pmidPublished).getTotalStudyCount();

                    for (WeeklyProgressView itemPublished : pairPublished.getValue()) {

                        // Check that StudyId was never set to this status before
                        if (!previouslyPublished_Studies.contains(itemPublished.getStudyId())) {
                            studiesPublishedThatWeek.add((itemPublished.getStudyId()));

                            publicationWeeklyProgressStatusData.get(pmidPublished).setCount_Published(publicationWeeklyProgressStatusData.get(pmidPublished).getCount_Published() + 1L);

                            // Check if total number of published studies equals number of all studies for publication
                            if (publicationWeeklyProgressStatusData.get(pmidPublished).getCount_Published().equals(totalNumStudies)) {

                                if (!publicationsPublishedThatWeek.contains(pmidPublished)) {
                                    publicationsPublishedThatWeek.add(publicationWeeklyProgressStatusData.get(pmidPublished).getPubmedId());
                                }
                            }

                            // Add previously seen StudyIds that are Published to "previouslyPublished_Studies"
                            previouslyPublished_Studies.add(itemPublished.getStudyId());
                        }
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

                if (allPMIDs.contains(pmidLevel1)) {
                    Long totalNumStudies = publicationWeeklyProgressStatusData.get(pmidLevel1).getTotalStudyCount();

                    for (WeeklyProgressView itemLevel1 : pairLevel1.getValue()) {

                        // Check that StudyId was never set to this status before
                        if (!previousLevel1_CurationDone_Studies.contains(itemLevel1.getStudyId())) {
                            studiesWithLevel1Completed.add((itemLevel1.getStudyId()));

                            publicationWeeklyProgressStatusData.get(pmidLevel1).setCount_Level1_CurationDone(publicationWeeklyProgressStatusData.get(pmidLevel1).getCount_Level1_CurationDone() + 1L);

                            // Check if total number of created studies equals number of all studies for publication
                            if (publicationWeeklyProgressStatusData.get(pmidLevel1).getCount_Level1_CurationDone().equals(totalNumStudies)) {

                                if (!publicationsWithLevel1Completed.contains(pmidLevel1)) {
                                    publicationsWithLevel1Completed.add(publicationWeeklyProgressStatusData.get(pmidLevel1).getPubmedId());
                                }
                            }
                            // Add previously seen StudyIds at Level 1 to "previousLevel1_CurationDone_Studies"
                            previousLevel1_CurationDone_Studies.add(itemLevel1.getStudyId());
                        }
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

                if (allPMIDs.contains(pmidLevel2)) {

                    Long totalNumStudies = publicationWeeklyProgressStatusData.get(pmidLevel2).getTotalStudyCount();

                    for (WeeklyProgressView itemLevel2 : pairLevel2.getValue()) {

                        // Check that StudyId was never set to this status before
                        if (!previousLevel2_CurationDone_Studies.contains(itemLevel2.getStudyId())) {
                            studiesWithLevel2Completed.add((itemLevel2.getStudyId()));

                            publicationWeeklyProgressStatusData.get(pmidLevel2).setCount_Level2_CurationDone(publicationWeeklyProgressStatusData.get(pmidLevel2).getCount_Level2_CurationDone() + 1L);

                            // Check if total number of created studies equals number of all studies for publication
                            if (publicationWeeklyProgressStatusData.get(pmidLevel2).getCount_Level2_CurationDone().equals(totalNumStudies)) {

                                if (!publicationsWithLevel2Completed.contains(pmidLevel2)) {
                                    publicationsWithLevel2Completed.add(publicationWeeklyProgressStatusData.get(pmidLevel2).getPubmedId());
                                }
                            }
                            // Add previously seen StudyIds at Level 2 to "previousLevel2_CurationDone_Studies"
                            previousLevel2_CurationDone_Studies.add(itemLevel2.getStudyId());
                        }
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

        store(reportsWeeklyProgressViews, type, timestamp, weekCode);
    }

    private void store(List<ReportsWeeklyProgressView> reportsWeeklyProgressViews, String type, Date timestamp, long weekCode) {

        for (ReportsWeeklyProgressView reportsWeeklyProgressView : reportsWeeklyProgressViews) {
            WeeklyReport weeklyReport = new WeeklyReport();

            weeklyReport.setPublicationsCreated(StringUtils.join(reportsWeeklyProgressView.getPublicationsCreated() != null ?
                    reportsWeeklyProgressView.getPublicationsCreated() : new ArrayList<>(), ","));
            weeklyReport.setPublicationsLevel1Completed(StringUtils.join(reportsWeeklyProgressView.getPublicationsLevel1Completed() != null ?
                    reportsWeeklyProgressView.getPublicationsLevel1Completed() : new ArrayList<>(), ","));
            weeklyReport.setPublicationsLevel2Completed(StringUtils.join(reportsWeeklyProgressView.getPublicationsLevel2Completed() != null ?
                    reportsWeeklyProgressView.getPublicationsLevel2Completed() : new ArrayList<>(), ","));
            weeklyReport.setPublicationsPublished(StringUtils.join(reportsWeeklyProgressView.getPublicationsPublished() != null ?
                    reportsWeeklyProgressView.getPublicationsPublished() : new ArrayList<>(), ","));

            weeklyReport.setStudiesCreated(StringUtils.join(reportsWeeklyProgressView.getStudiesCreated() != null ?
                    reportsWeeklyProgressView.getStudiesCreated() : new ArrayList<>(), ","));
            weeklyReport.setStudiesLevel1Completed(StringUtils.join(reportsWeeklyProgressView.getStudiesLevel1Completed() != null ?
                    reportsWeeklyProgressView.getStudiesLevel1Completed() : new ArrayList<>(), ","));
            weeklyReport.setStudiesLevel2Completed(StringUtils.join(reportsWeeklyProgressView.getStudiesLevel2Completed() != null ?
                    reportsWeeklyProgressView.getStudiesLevel2Completed() : new ArrayList<>(), ","));
            weeklyReport.setStudiesPublished(StringUtils.join(reportsWeeklyProgressView.getStudiesPublished() != null ?
                    reportsWeeklyProgressView.getStudiesPublished() : new ArrayList<>(), ","));

            weeklyReport.setTimestamp(timestamp);
            weeklyReport.setType(type);
            weeklyReport.setWeekCode(weekCode);
            weeklyReport.setWeekDate(reportsWeeklyProgressView.getWeekDate());

            weeklyReportRepository.save(weeklyReport);
        }
    }

    public List<ReportsWeeklyProgressView> getLatestWeeklyReportOT() {
        return this.getLatestWeeklyReport(OPEN_TARGETS);
    }

    public List<ReportsWeeklyProgressView> getLatestWeeklyReportAll() {
        return this.getLatestWeeklyReport(ALL);
    }

    private List<ReportsWeeklyProgressView> getLatestWeeklyReport(String type) {
        List<ReportsWeeklyProgressView> result = new ArrayList<>();

        Calendar todayCalendar = Calendar.getInstance();
        int weekCode = todayCalendar.get(Calendar.WEEK_OF_YEAR);
        log.info("Retrieving weekly report for type and weekcode: {} | {}", type, weekCode);
        List<WeeklyReport> weeklyReports = weeklyReportRepository.findByTypeAndWeekCode(type, weekCode);
        log.info("Found {} entries", weeklyReports.size());

        for (WeeklyReport weeklyReport : weeklyReports) {
            ReportsWeeklyProgressView reportsWeeklyProgressView = new ReportsWeeklyProgressView(weeklyReport.getWeekDate());

            reportsWeeklyProgressView.setPublicationsCreated(weeklyReport.getPublicationsCreated() != null ?
                    new HashSet<>(Arrays.asList(weeklyReport.getPublicationsCreated().split(","))) : new HashSet<>());
            reportsWeeklyProgressView.setPublicationsLevel1Completed(weeklyReport.getPublicationsLevel1Completed() != null ?
                    new HashSet<>(Arrays.asList(weeklyReport.getPublicationsLevel1Completed().split(","))) : new HashSet<>());
            reportsWeeklyProgressView.setPublicationsLevel2Completed(weeklyReport.getPublicationsLevel2Completed() != null ?
                    new HashSet<>(Arrays.asList(weeklyReport.getPublicationsLevel2Completed().split(","))) : new HashSet<>());
            reportsWeeklyProgressView.setPublicationsPublished(weeklyReport.getPublicationsPublished() != null ?
                    new HashSet<>(Arrays.asList(weeklyReport.getPublicationsPublished().split(","))) : new HashSet<>());

            reportsWeeklyProgressView.setStudiesCreated(weeklyReport.getStudiesCreated() != null ?
                    fromString(weeklyReport.getStudiesCreated()) : new HashSet<>());
            reportsWeeklyProgressView.setStudiesLevel1Completed(weeklyReport.getStudiesLevel1Completed() != null ?
                    fromString(weeklyReport.getStudiesLevel1Completed()) : new HashSet<>());
            reportsWeeklyProgressView.setStudiesLevel2Completed(weeklyReport.getStudiesLevel2Completed() != null ?
                    fromString(weeklyReport.getStudiesLevel2Completed()) : new HashSet<>());
            reportsWeeklyProgressView.setStudiesPublished(weeklyReport.getStudiesPublished() != null ?
                    fromString(weeklyReport.getStudiesPublished()) : new HashSet<>());

            result.add(reportsWeeklyProgressView);
        }

        return result;
    }

    private Set<Long> fromString(String s) {
        List<String> list = Arrays.asList(s.split(","));
        Set<Long> result = new HashSet<>();
        for (String ss : list) {
            result.add(Long.valueOf(ss));
        }
        return result;
    }

}
