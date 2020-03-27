package uk.ac.ebi.spot.goci.utils;

import uk.ac.ebi.spot.goci.model.Author;
import uk.ac.ebi.spot.goci.model.Publication;

import java.util.Collection;

/**
 * Created by cinzia on 20/09/2017.
 */
public class EuropePMCData {

    private Boolean error;

    private Publication publication = null;

    private Collection<Author> authors = null;

    private Author firstAuthor = null;

    private String doi = null;

    public Boolean getError() { return error; }

    public void setError(Boolean error) { this.error = error; }

    public Publication getPublication() { return publication; }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public Collection<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Collection<Author> authors) {
        this.authors = authors;
    }

    public void setFirstAuthor(Author firstAuthor) { this.firstAuthor = firstAuthor; }

    public Author getFirstAuthor() { return firstAuthor; }

    public void setDoi(String doi){
        this.doi = doi;
    }

    public String getDoi(){
        return doi;
    }
}
