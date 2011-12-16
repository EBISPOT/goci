package uk.ac.ebi.fgpt.goci.model;

import uk.ac.ebi.fgpt.goci.dao.GociEvent;
import uk.ac.ebi.fgpt.goci.dao.GociStudyListener;

import java.util.HashSet;
import java.util.Set;

/**
 * An abstract class that provides methods for listening to and updating GociStudies.
 *
 * @author Tony Burdett
 * @date 01/11/11
 */
public abstract class AbstractGociStudy implements GociStudy {
    private Set<GociStudyListener> listeners = new HashSet<GociStudyListener>();

    public boolean addListener(GociStudyListener listener) {
        return this.listeners.add(listener);
    }

    public boolean removeListener(GociStudyListener listener) {
        return this.listeners.remove(listener);
    }

    protected void fireStudyUpdatedEvent(GociEvent evt) {
        for (GociStudyListener listener : listeners) {
            listener.studyUpdated(evt);
        }
    }

    @Override public String toString() {
        return "GociStudy {\n" +
                "\tid='" + getID() + "\',\n " +
                "\tpubmedID='" + getPubMedID() + "\',\n " +
                "\ttitle='" + getTitle() + "\',\n " +
                "\tpaperAbstract='" + getPaperAbstract() + "\',\n " +
                "\towner='" + getOwner() + "\',\n " +
                "\tstate='" + getState() + "\',\n " +
                "\teligibility=" + getGwasEligibility() + "\',\n " +
                '}';
    }
}
