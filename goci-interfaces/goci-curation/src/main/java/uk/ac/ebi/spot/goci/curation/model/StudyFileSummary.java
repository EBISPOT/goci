package uk.ac.ebi.spot.goci.curation.model;

/**
 * Created by emma on 15/04/2016.
 *
 * @author emma
 *         <p>
 *         An object to represent details we have about a study file
 */
public class StudyFileSummary {

    private String fileName;

    private String link;
    
    public StudyFileSummary(String fileName, String link) {
        this.fileName = fileName;
        this.link = link;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
