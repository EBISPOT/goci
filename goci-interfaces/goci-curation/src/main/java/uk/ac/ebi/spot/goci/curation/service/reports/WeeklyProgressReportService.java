package uk.ac.ebi.spot.goci.curation.service.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.WeeklyTracking;
import uk.ac.ebi.spot.goci.service.SQLDateUtilityService;
import uk.ac.ebi.spot.goci.service.WeeklyTrackingService;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.collect;


/**
 * Created by cinzia on 15/12/2016.
 */
@Lazy
@Service
public class WeeklyProgressReportService {

    private WeeklyTrackingService weeklyTrackingService;
    private SQLDateUtilityService sqlDateUtilityService;

    @Autowired
    public WeeklyProgressReportService(WeeklyTrackingService weeklyTrackingService,
                                       SQLDateUtilityService sqlDateUtilityService) {
        this.weeklyTrackingService = weeklyTrackingService;
        this.sqlDateUtilityService = sqlDateUtilityService;
    }



    public Set<Long> filterWeeklyTracking(List<WeeklyTracking>  weeklyTrackingAll, int week, int year, String status) {

        Set<Long> queueFiltered  = weeklyTrackingAll.stream()
                .filter(weeklyTracking -> weeklyTracking.getWeek().equals(week))
                .filter(weeklyTracking -> weeklyTracking.getYear().equals(year))
                .filter(weeklyTracking -> weeklyTracking.getStatus().equals(
                        status))
                .map(WeeklyTracking::getStudyId)
                .collect(Collectors.toSet());

        return queueFiltered;
    }

    // The queue starts from 1 jan 2016. Retrieve events by e-mail.
    public ArrayList<Integer[]> calculateProgressiveQueues() {
        ArrayList<Integer[]> progressiveQueues = new ArrayList<Integer[]>();
        int year = 2016;
        int week = 1;
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, 1);

        Integer maxYear = calendar.get(Calendar.YEAR);
        Integer maxWeek = calendar.get(Calendar.WEEK_OF_YEAR);

        List<WeeklyTracking>  weeklyTrackingAll = weeklyTrackingService.findAll();
        List<WeeklyTracking>  weeklyTrackingQueue1 = weeklyTrackingService.find2016QueueLevel1();
        List<WeeklyTracking>  weeklyTrackingQueue2 = weeklyTrackingService.find2016QueueLevel2();

        Set<Long> queueLevel1  = weeklyTrackingQueue1.stream()
                .map(WeeklyTracking::getStudyId)
                .collect(Collectors.toSet());

        Set<Long>  queueLevel2  = weeklyTrackingQueue2.stream()
                .map(WeeklyTracking::getStudyId)
                .collect(Collectors.toSet());

        Set<Long> queueLevel3  = new HashSet<Long>();


        Integer row2[]={2015, 52, queueLevel1.size(), queueLevel2.size(), 0,0};
        progressiveQueues.add(row2);

        while (!((year == maxYear) && (week == maxWeek))) {
            //HashSet<Long>  queueLevel1_week = weeklyTrackingService.findStudyByStatusAndYearAndWeek("In level 1 queue", year,week);
            Integer weekQuery = new Integer(week);
            Integer yearQuery = new Integer(year);

            Set<Long> queueLevel1_week  = filterWeeklyTracking(weeklyTrackingAll, weekQuery, yearQuery,"In level 1 queue");
            Set<Long> queueLevel2_week  = filterWeeklyTracking(weeklyTrackingAll, weekQuery, yearQuery,"In level 2 queue");
            Set<Long> queueLevel3_week  = filterWeeklyTracking(weeklyTrackingAll, weekQuery, yearQuery,"In level 3 queue");
            Set<Long> queuePublication_week  = filterWeeklyTracking(weeklyTrackingAll, weekQuery, yearQuery,"Publication Study");

            //Queue 1
            queueLevel1.removeAll(queueLevel2_week);
            queueLevel1.removeAll(queueLevel3_week);
            queueLevel1.removeAll(queuePublication_week);
            queueLevel1.addAll(queueLevel1_week);

            //Queue 2
            queueLevel2.removeAll(queueLevel3_week );
            queueLevel2.removeAll(queuePublication_week);
            queueLevel2.addAll(queueLevel2_week);

            //Queue 3
            queueLevel3.removeAll(queuePublication_week);
            queueLevel3.addAll(queueLevel3_week);

            Integer row[]={year, week, queueLevel1.size(), queueLevel2.size(), queueLevel3.size(),
                           queuePublication_week.size()};

            progressiveQueues.add(row);

            // Next Year
            if (week == 52) {
                week = 1;
                year = year + 1;
            }
            else { week = week +1; }

        }

        // Print the IDs in the Level 1 queue. Debug
        // Iterator iterator = queueLevel1.iterator();
        // while (iterator.hasNext()){ System.out.println(iterator.next() + ","); }

        return progressiveQueues;
    }


    public ArrayList<Integer[]> calculateProgressiveQueues2() {
        ArrayList<Object[]> progressionQueues = weeklyTrackingService.getMinYearWeek();
        BigDecimal yearBigDecimal = (BigDecimal) progressionQueues.get(0)[0];
        int year = yearBigDecimal.intValue();
        BigDecimal weekBigDecimal = (BigDecimal) progressionQueues.get(0)[1];
        int week = weekBigDecimal.intValue();
        ArrayList<Integer[]> progressiveQueues = new ArrayList<Integer[]>();
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, 1);

        Integer maxYear = calendar.get(Calendar.YEAR);
        Integer maxWeek = calendar.get(Calendar.WEEK_OF_YEAR);

        Set<Long> queueLevel1  = new HashSet<Long>();
        Set<Long> queueLevel2  = new HashSet<Long>();
        Set<Long> queueLevel3  = new HashSet<Long>();

        List<WeeklyTracking>  weeklyTrackingList = weeklyTrackingService.findAll();

        while (!((year == maxYear) && (week == maxWeek))) {
            //HashSet<Long>  queueLevel1_week = weeklyTrackingService.findStudyByStatusAndYearAndWeek("In level 1 queue", year,week);
            Integer weekQuery = new Integer(week);
            Integer yearQuery = new Integer(year);
            Set<Long> queueLevel1_week  = weeklyTrackingList.stream()
                    .filter(weeklyTracking -> weeklyTracking.getWeek().equals(weekQuery))
                    .filter(weeklyTracking -> weeklyTracking.getYear().equals(yearQuery))
                    .filter(weeklyTracking -> weeklyTracking.getStatus().equals(
                            "In level 1 queue"))
                    .map(WeeklyTracking::getStudyId)
                    .collect(Collectors.toSet());

            Set<Long> queueLevel2_week  = weeklyTrackingList.stream()
                    .filter(weeklyTracking -> weeklyTracking.getWeek().equals(weekQuery))
                    .filter(weeklyTracking -> weeklyTracking.getYear().equals(yearQuery))
                    .filter(weeklyTracking -> weeklyTracking.getStatus().equals(
                            "In level 2 queue"))
                    .map(WeeklyTracking::getStudyId)
                    .collect(Collectors.toSet());

            Set<Long> queueLevel3_week  = weeklyTrackingList.stream()
                    .filter(weeklyTracking -> weeklyTracking.getWeek().equals(weekQuery))
                    .filter(weeklyTracking -> weeklyTracking.getYear().equals(yearQuery))
                    .filter(weeklyTracking -> weeklyTracking.getStatus().equals(
                            "In level 3 queue"))
                    .map(WeeklyTracking::getStudyId)
                    .collect(Collectors.toSet());

            Set<Long> queuePublication_week = weeklyTrackingList.stream()
                    .filter(weeklyTracking -> weeklyTracking.getWeek().equals(weekQuery))
                    .filter(weeklyTracking -> weeklyTracking.getYear().equals(yearQuery))
                    .filter(weeklyTracking -> weeklyTracking.getStatus().equals(
                            "Publication Study"))
                    .map(WeeklyTracking::getStudyId)
                    .collect(Collectors.toSet());



            queueLevel1.removeAll(queueLevel2_week );
            queueLevel1.removeAll(queueLevel3_week );
            queueLevel1.removeAll(queuePublication_week);
            queueLevel1.addAll(queueLevel1_week);
            queueLevel2.removeAll(queueLevel3_week );
            queueLevel2.removeAll(queuePublication_week);
            queueLevel2.addAll(queueLevel2_week);
            queueLevel3.removeAll(queuePublication_week);
            queueLevel3.addAll(queueLevel3_week);

            Integer row[]={year, week, queueLevel1.size(), queueLevel2.size(), queueLevel3.size(),queuePublication_week.size()};
            progressiveQueues.add(row);
            // to change
            if (week == 52) {
                week = 1;
                year = year + 1;
            }
            else { week = week +1; }

        }

        return progressiveQueues;
    }

}
