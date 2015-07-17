package uk.ac.ebi.spot.goci.pussycat.session;

import org.springframework.stereotype.Component;

/**
 * An enum that encapsulates strategies for dealing with {@link PussycatSession}s.  {@link #JOIN} indicates that new
 * HttpSessions should join an existing serverside PussycatSession, if one is available.  {@link #CREATE} indicates that
 * a new serverside PussycatSession should always be created.
 *
 * @author Tony Burdett
 * Date 02/03/12
 */
@Component
public enum PussycatSessionStrategy {
    JOIN,
    CREATE
}
