package uk.ac.ebi.spot.goci.lang;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Javadocs go here.
 *
 * @author Junit Generation Plugin for Maven, written by Tony Burdett
 * Date 26-01-2012
 */
public class TestInitializable extends TestCase {
    private SuccessfulInitializable succeedCase;

    private Logger log = LoggerFactory.getLogger(getClass());

    public void setUp() {
        succeedCase = new SuccessfulInitializable();
    }

    public void tearDown() {
        succeedCase = null;
    }

    public void testIsReady() {
        assertFalse("Not initialized, ready should be false", succeedCase.isReady());
        succeedCase.init();
        try {
            assertTrue("Should be able to get results after init()", succeedCase.getAfterInitializing());
            assertTrue("Initialized, should be ready", succeedCase.isReady());
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testWaitUntilReady() {
        try {
            // create a thread that will interrupt succeed case after a time
            new Thread(new Runnable() {
                public void run() {
                    synchronized (this) {
                        try {
                            wait(500);
                            succeedCase.interrupt();
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            // call wait until ready
            succeedCase.waitUntilReady();

            // should be blocked until interrupted exception, so shouldn't get to here
            fail();
        }
        catch (Exception e) {
            e.printStackTrace();
            assertTrue(e instanceof IllegalStateException);
            log.info("Caught expected IllegalStateException");
        }
    }

    public void testInit() {
        try {
            // create a thread that will interrupt succeed case after a time
            new Thread(new Runnable() {
                public void run() {
                    synchronized (this) {
                        try {
                            wait(500);
                            succeedCase.init();
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            // call wait until ready
            succeedCase.waitUntilReady();

            // should be able to get a true response from getAfterInitializing()
            assertTrue(succeedCase.getAfterInitializing());
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    private class SuccessfulInitializable extends Initializable {
        @Override protected void doInitialization() throws Exception {
            getLog().debug("Doing some work...");
        }

        public boolean getAfterInitializing() throws InterruptedException {
            waitUntilReady();
            return true;
        }
    }
}
