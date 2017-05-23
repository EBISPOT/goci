package uk.ac.ebi.spot.goci.model;

/**
 * Created by Dani on 01/07/16.
 */
public class PublishedStudy {

    private String author;
    private String title;
    private String pubmedId;
    private String publicationDate;
    private String catalogPublishDate;
    private String journal;
    private String associationCount;
    private String trait;

    public PublishedStudy(String author,
                          String title,
                          String pubmedId,
                          String publicationDate,
                          String catalogPublishDate,
                          String journal,
                          String associationCount,
                          String trait) {

        this.author = author;
        this.title = title;
        this.pubmedId = pubmedId;
        this.publicationDate = publicationDate;
        this.catalogPublishDate = catalogPublishDate;
        this.journal = journal;
        this.associationCount = associationCount;
        this.trait = trait;
    }


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getCatalogPublishDate() {
        return catalogPublishDate;
    }

    public void setCatalogPublishDate(String catalogPublishDate) {
        this.catalogPublishDate = catalogPublishDate;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getAssociationCount() {
        return associationCount;
    }

    public void setAssociationCount(String associationCount) {
        this.associationCount = associationCount;
    }

    public String getTrait() {
        return trait;
    }

    public void setTrait(String trait) {
        this.trait = trait;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }
}
