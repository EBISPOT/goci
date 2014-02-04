package uk.ac.ebi.fgpt.goci.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }



    public DefaultGwasStudy(String pubMedId, String author, Date studydate, String publication, String title){
        if(author.length() > 96){
            author = author.substring(0, 95);
            getLog().debug("Author for study " + pubMedId + " was truncated");
        }
        if(publication.length() > 64){
            publication = publication.substring(0, 63);
            getLog().debug("Publication for study " + pubMedId + " was truncated");
        }
        if(title.length() > 255){
            title = title.substring(0, 254);
            getLog().debug("Title for study " + pubMedId + " was truncated");
        }

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
