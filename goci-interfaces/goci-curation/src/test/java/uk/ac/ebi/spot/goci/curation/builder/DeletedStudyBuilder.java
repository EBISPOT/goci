package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.model.DeletedStudy;

/**
 * Created by emma on 31/05/2016.
 *
 * @author emma
 *         <p>
 *         Deleted study builder used in testing
 */
public class DeletedStudyBuilder {

    private DeletedStudy deletedStudy = new DeletedStudy();

    public DeletedStudyBuilder setId(Long id) {
        deletedStudy.setId(id);
        return this;
    }

    public DeletedStudyBuilder setTitle(String title) {
        deletedStudy.setTitle(title);
        return this;
    }

    public DeletedStudyBuilder setPubmedId(String pubmedId) {
        deletedStudy.setPubmedId(pubmedId);
        return this;
    }

    public DeletedStudy build() {
        return deletedStudy;
    }
}