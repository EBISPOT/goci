package uk.ac.ebi.spot.goci.curation.model;

/**
 * Created by emma on 16/01/15.
 *
 * @author emma
 *         <p>
 *         Service class used to deal with curator inputed pubmed id,
 *         the id will be used to query pubmed. In order to return this value
 *         from a html form it must be warapped in an object.
 */
public class PubmedIdForImport {

    private String pubmedId;

    public PubmedIdForImport() {

    }

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }
}
