package uk.ac.ebi.spot.goci.curation.model.errors;

/**
 * Created by xinhe on 12/04/2017.
 */
public class NoteIsLockedError extends Error {

    public NoteIsLockedError() {
        super("System note is locked.");
    }

    public NoteIsLockedError(Long noteId) {
        super("System note  " + noteId + " is locked.");
    }
}
