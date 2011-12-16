package uk.ac.ebi.fgpt.goci.factory;

import uk.ac.ebi.fgpt.goci.model.GociStudy;
import uk.ac.ebi.fgpt.goci.model.GociUser;

/**
 * An abstract factory that is capable of generating fully formed {@link uk.ac.ebi.fgpt.goci.model.GociStudy} objects.
 *
 * @author Tony Burdett
 * @date 26/10/11
 */
public interface GociStudyFactory {
    /**
     * Creates a new study that can be tracked via the GOCI Tracking system.  You must supply some simple summary
     * information about the paper.  This factory method results in a new GociStudy object that is initially unassigned
     * to any user.
     *
     * @param pubMedID     the ID of the paper in PubMed
     * @param title        the title of the paper
     * @param abstractText the abstract of the paper
     * @return a new GociStudy representing this study
     */
    GociStudy createStudy(String pubMedID, String title, String abstractText);

    /**
     * Creates a new study that can be tracked via the GOCI Tracking system.  You must supply some simple summary
     * information about the paper.  This factory method results in a new GociStudy object that is initially assigned to
     * the supplied user.
     *
     * @param pubMedID     the ID of the paper in PubMed
     * @param title        the title of the paper
     * @param abstractText the abstract of the paper
     * @param initialOwner the initial owner of this study
     * @return a new GociStudy representing this study
     */
    GociStudy createStudy(String pubMedID, String title, String abstractText, GociUser initialOwner);
}
