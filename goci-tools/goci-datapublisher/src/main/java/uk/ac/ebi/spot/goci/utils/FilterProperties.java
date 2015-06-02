package uk.ac.ebi.spot.goci.utils;

/**
 * Created by catherineleroy on 13/05/2015.
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
