package uk.ac.ebi.spot.goci.lang;

/**
 * Created by dwelter on 12/02/14.
 */
public class ImporterProperties {

    public static String saveToTable;

    public static void setOutputTable(String table){
        saveToTable = table;
    }


    public static String getOutputTable(){
        return saveToTable;
    }

}
