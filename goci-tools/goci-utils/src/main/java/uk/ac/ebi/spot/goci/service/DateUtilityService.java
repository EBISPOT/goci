package uk.ac.ebi.spot.goci.service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

/**
 * A service that let us manipulate date.
 * sql.date vs util.date
 *
 * @author Cinzia Malangone Date 7/03/2017
 */
public interface DateUtilityService {

    long daysBetweenTwoDates(Date created, Date published);

    Calendar getCalendarDate(Date eventSQLDate);

    Date getXWeekBefore(Date eventSQLDate, int numberOfWeek);

    List<Integer> getLastWeekStats();
}
