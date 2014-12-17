package uk.ac.ebi.spot.goci.pussycat.session;

import net.sourceforge.fluxion.spi.Spi;

/**
 * An interface that defines a factory method for generating {@link PussycatSession}s
 *
 * @author Tony Burdett
 * @date 04/06/14
 */
@Spi
public interface PussycatSessionFactory {
    PussycatSession createPussycatSession();
}
