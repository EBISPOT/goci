package uk.ac.ebi.fgpt.goci.checker;

import java.util.Date;

/**
    * User: dwelter
    * Date: 18/04/13
    * Time: 15:45
    */
  public class Mapping {

    private String diseasetrait;
    private String efotrait;
    private String efouri;
    private String pmid;
    private String author;
    private Date date;
    private String journal;
    private String parent;


    public Mapping(String diseasetrait, String efotrait, String efouri, String pmid, String author,Date date, String journal){
        this.diseasetrait = diseasetrait;
        this.efotrait = efotrait;
        this.efouri = efouri;
        this.pmid = pmid;
        this.author = author;
        this.date = date;
        this.journal = journal;
    }

    public String getDiseasetrait() {
        return diseasetrait;
    }

    public String getEfotrait() {
        return efotrait;
    }

    public String getEfouri() {
        return efouri;
    }

    public String getPmid() {
        return pmid;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    public String getJournal() {
        return journal;
    }

    public void setParent(String parent){
        this.parent = parent;
    }

    public String getParent(){
        return parent;
    }
}
