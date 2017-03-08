package uk.ac.ebi.spot.goci.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by cinzia on 07/03/2017.
 */

@Service
@Component
public class SQLDateUtilityService implements DateUtilityService{

    public long daysBetweenTwoDates(Date created, Date published) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(created);
        c2.setTime(published);
        long days = (c2.getTime().getTime() - c1.getTime()
                .getTime()) / (24 * 3600 * 1000);
        return days;
    }

    public Calendar getCalendarDate(Date eventSQLDate) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(eventSQLDate);
        return calendar;
    }

    public Date getXWeekBefore(Date eventSQLDate, int numberOfWeek) {
        Calendar calendar = this.getCalendarDate(eventSQLDate);
        calendar.add(Calendar.DATE, numberOfWeek);
        Date weekBefore= new java.sql.Date(calendar.getTimeInMillis());

        return weekBefore;
    }

    // The stats starts from Sunday to Saturday. Jmorales
    public List<Integer> getLastWeekStats() {
        List<Integer> lastWeekYear = new ArrayList<>();

        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, 1);
        int week = calendar.get(Calendar.WEEK_OF_YEAR) - 1;
        int year = calendar.get(Calendar.YEAR);

        if (week == 0) {
            week = 52;
            year = year - 1;
        }

        lastWeekYear.add(week);
        lastWeekYear.add(year);
        return lastWeekYear;
    }

}
