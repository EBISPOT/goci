package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.curation.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.model.Association;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by emma on 03/03/2016.
 *
 * @author emma
 *         <p>
 *         Test class for AssociationOperationsService.java
 */
@RunWith(MockitoJUnitRunner.class)
public class AssociationOperationsServiceTest {

    private static final Association ASS_BETA =
            new AssociationBuilder().setId(600L).setBetaNum((float) 0.012).build();


    private static final Association ASS_OR =
            new AssociationBuilder().setId(601L).setOrPerCopyNum((float) 5.97).build();


    private AssociationOperationsService associationOperationsService;

    @Before
    public void setUpMock() {
        associationOperationsService = new AssociationOperationsService();
    }

    @Test
    public void testDetermineIfAssociationIsOrType() {
        assertFalse(associationOperationsService.determineIfAssociationIsOrType(ASS_BETA));
        assertTrue(associationOperationsService.determineIfAssociationIsOrType(ASS_OR));
    }
}
