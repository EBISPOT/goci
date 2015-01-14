package uk.ac.ebi.spot.goci.lang;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 31/07/12
 * Time: 14:32
 *
 * A settings class for values that might be used to filter SQL queries
 */
public class FilterProperties {

    public static String pvalueFilter;
    public static String dateFilter;

    public static void setPvalueFilter(String pvalue){
        pvalueFilter = pvalue;
    }

    public static void setDateFilter(String date){
        dateFilter = date;
    }

    public static String getPvalueFilter(){
        return pvalueFilter;
    }

    public static String getDateFilter(){
        return dateFilter;
    }
}
