package uk.ac.ebi.spot.goci.curation.service.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.WeeklyTracking;
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

    @Autowired
    public WeeklyProgressReportService(WeeklyTrackingService weeklyTrackingService) {
           this.weeklyTrackingService = weeklyTrackingService;
    }


    public ArrayList<Integer[]> calculateProgressiveQueues() {
        ArrayList<Object[]> progressionQueues = weeklyTrackingService.getMinYearWeek();
        BigDecimal yearBigDecimal = (BigDecimal) progressionQueues.get(0)[0];
        int year = yearBigDecimal.intValue();
        BigDecimal weekBigDecimal = (BigDecimal) progressionQueues.get(0)[1];
        int week = weekBigDecimal.intValue();
        ArrayList<Integer[]> ProgressiveQueues = new ArrayList<Integer[]>();
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

            Integer row[]={year, week, queueLevel1.size(), queueLevel2.size(), queueLevel3.size()};
            ProgressiveQueues.add(row);
            // to change
            if (week == 52) {
                week = 1;
                year = year + 1;
            }
            else { week = week +1; }

        }

        return ProgressiveQueues;
    }

}
