package uk.ac.ebi.fgpt.goci.pussycat.session;

/**
 * An interface that defines a factory method for generating {@link PussycatSession}s
 *
 * @author Tony Burdett
 * @date 04/06/14
 */
public interface PussycatSessionFactory {
    PussycatSession createPussycatSession();
}
