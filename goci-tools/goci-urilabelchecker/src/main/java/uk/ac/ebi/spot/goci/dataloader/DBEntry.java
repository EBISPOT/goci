package uk.ac.ebi.spot.goci.dataloader;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 22/04/13
 * Time: 14:23
 * To change this template use File | Settings | File Templates.
 */
public class DBEntry {

    private int dbid;
    private String efotrait, efouri;

    public DBEntry(int dbid, String efotrait, String efouri){
        this.dbid = dbid;
        this.efotrait = efotrait;
        this.efouri = efouri;
    }

    public int getID(){
        return dbid;
    }

    public String getEfotrait(){
        return efotrait;
    }

    public String getEfouri(){
        return efouri;
    }
}

