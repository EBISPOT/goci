package uk.ac.ebi.fgpt.goci.factory;

import uk.ac.ebi.fgpt.goci.model.GociStudy;
import uk.ac.ebi.fgpt.goci.model.GociUser;
import uk.ac.ebi.fgpt.goci.model.UserCreatedGociStudy;

/**
 * A basic implementation of a Study Factory.
 * <p/>
 * TODO: register listeners on creation to track changes to studies
 *
 * @author Tony Burdett
 * Date 26/10/11
 */
public class DefaultGociStudyFactory implements GociStudyFactory {
    public GociStudy createStudy(String pubMedID, String title, String abstractText) {
        return new UserCreatedGociStudy(pubMedID, title, abstractText);
    }

    public GociStudy createStudy(String pubMedID, String title, String abstractText, GociUser initialOwner) {
        return new UserCreatedGociStudy(pubMedID, title, abstractText, initialOwner);
    }
}
