package uk.ac.ebi.spot.goci.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by catherineleroy on 13/05/2015.
 *
 * A settings class for values that might be used to filter SQL queries
 */
public class FilterProperties {

    public static String pvalueFilter;
//    public static String dateFilter;
    public static Date dateFilter;
    public static int pvalueMant;
    public static int pvalueExp;

    public static void setPvalueFilter(String pvalue){
        pvalueFilter = pvalue;
        tokenisePvalueString();
    }

    public static void setDateFilter(String date) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateFilter = formatter.parse(date);
            System.out.println("Your date filter is " + dateFilter);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static String getPvalueFilter(){
        return pvalueFilter;
    }

//    public static String getDateFilter(){
//        return dateFilter;
//    }

    public static Date getDateFilter(){
        return dateFilter;
    }

    public static void tokenisePvalueString(){
        String[] values = pvalueFilter.split("E");
        pvalueMant = Integer.parseInt(values[0]);
        pvalueExp = Integer.parseInt(values[1]);
        System.out.println("Your p-value elements are " + pvalueMant + " and " + pvalueExp);
    }

    public static int getPvalueMant(){ return pvalueMant; }

    public static int getPvalueExp(){ return pvalueExp; }
}
