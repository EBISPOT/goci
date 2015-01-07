package uk.ac.ebi.spot.goci.utils;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.semanticweb.owlapi.model.IRI;
import uk.ac.ebi.spot.goci.lang.UniqueID;

/**
 * Javadocs go here.
 *
 * @author Junit Generation Plugin for Maven, written by Tony Burdett
 * Date 13-02-2012
 */
public class TestReflexiveIRIMinter extends TestCase {
    private ReflexiveIRIMinter minter;
    private String base = "http://www.test.com/test/ontology/base";

    public void setUp() {
        minter = new ReflexiveIRIMinter();
    }

    public void tearDown() {
        minter = null;
    }

    public void testMint() {
        try {
            String id = "myId";
            IRI expected = IRI.create(base.concat("/MockObject/").concat(minter.encodeToURI(id)));
            IRI iri = minter.mint(base, new MockObject(id));
            Assert.assertEquals(expected, iri);
            iri.toURI();

            String id2 = "\\?/@\\$/#02uk jnfdlkaj7_--=\\=/::;;ad";
            IRI expected2 = IRI.create(base.concat("/MockObject/").concat(minter.encodeToURI(id2)));
            IRI iri2 = minter.mint(base, new MockObject(id2));
            Assert.assertEquals(expected2, iri2);
            System.out.println(iri2);
            iri2.toURI();

            try {
                String id3 = null;
                minter.mint(base, new MockObject(id3));
                fail();
            }
            catch (NullPointerException e) {
                // correctly caught null id
            }

            MockObjectWithoutStringID o = new MockObjectWithoutStringID();
            IRI expected4 = IRI.create(base.concat("/MockObjectWithoutStringID/").concat(o.getID().toString()));
            IRI iri4 = minter.mint(base, new MockObjectWithoutStringID());
            iri4.toURI();
            Assert.assertEquals(expected4, iri4);

            String id5 = "myId";
            IRI expected5 = IRI.create(base.concat("/OverriddenPrefix/").concat(minter.encodeToURI(id5)));
            IRI iri5 = minter.mint(base, "OverriddenPrefix", new MockObject(id5));
            Assert.assertEquals(expected5, iri5);
            iri.toURI();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    private class MockObject {
        private String id;

        public MockObject(String id) {
            this.id = id;
        }

        @UniqueID
        public String getID() {
            return id;
        }
    }

    private class MockObjectWithoutStringID {
        @UniqueID
        public Object getID() {
            return new Object() {
                @Override public String toString() {
                    return "cast_to_id";
                }
            };
        }
    }
}
