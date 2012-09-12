package uk.ac.ebi.fgpt.goci.pussycat.metrics;

import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 11/09/12
 * Time: 16:31
 * To change this template use File | Settings | File Templates.
 */
public class DateTimeStamp {

    public static String getCurrentTimeStamp(){
        java.util.Date date= new java.util.Date();
        Timestamp current = new Timestamp(date.getTime());
        return current.toString();

    }
}
