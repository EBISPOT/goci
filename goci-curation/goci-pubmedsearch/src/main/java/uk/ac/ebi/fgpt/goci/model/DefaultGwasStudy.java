package uk.ac.ebi.fgpt.goci.model;

import java.sql.Date;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 25/04/13
 * Time: 14:44
 * To change this template use File | Settings | File Templates.
 */
public class DefaultGwasStudy implements GwasStudy{

    private int id;
    private String pubMedId;
    private String author;
    private String title;
    private Date studydate;
    private String publication;


    public DefaultGwasStudy(String pubMedId, String author, Date studydate, String publication, String title){
        this.pubMedId = pubMedId;
        this.author = author;
        this.studydate = studydate;
        this.publication = publication;
        this.title = title;
    }

    public void setId(int id){
        this.id = id;
    }

     public int getID() {
        return id;
    }

    public String getPubMedID() {
        return pubMedId;
    }

    @Override
    public String getTitle() {
        return title;
    }



    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public Date getPublicationDate() {
        return studydate;
    }

    @Override
    public String getPublication() {
        return publication;
    }
}
