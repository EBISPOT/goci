package uk.ac.ebi.fgpt.goci.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An object that can do some initialization work in parallel at startup.  Implementing classes should extend {@link
 * #doInitialization} with the work they wish to do.  All methods that require initialization to complete can check the
 * current state with a call to {@link #isReady()} which returns true or false, or a call to {@link #waitUntilReady()},
 * which blocks whilst initialization completes.
 *
 * @author Tony Burdett
 * Date 25/01/12
 */
public abstract class Initializable {
    private Thread initThread;

    private boolean initializing;
    private boolean ready;
    private Throwable initializationException;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    protected synchronized void setInitializationException(Throwable t) {
        this.initializationException = t;
        notifyAll();
    }

    protected synchronized void setInitializing(boolean initializing) {
        this.initializing = initializing;
        notifyAll();
    }

    public boolean isInitializing() {
        return initializing;
    }

    protected synchronized void setReady(boolean ready) {
        this.ready = ready;
        notifyAll();
    }

    protected synchronized boolean isReady() throws IllegalStateException {
        if (initializationException != null) {
            throw new IllegalStateException(
                    "Initialization of " + getClass().getSimpleName() + " failed", initializationException);
        }
        else {
            return ready;
        }
    }

    protected synchronized void waitUntilReady() throws IllegalStateException, InterruptedException {
        synchronized (this) {
            while (!isReady()) {
                getLog().debug("Waiting until " + getClass().getSimpleName() + " is ready...");
                wait();
            }
            getLog().debug(getClass().getSimpleName() + " is now ready");
        }
    }

    protected synchronized void interrupt() {
        // if initializing, then interrupt
        if (initThread != null) {
            initThread.interrupt();
        }
        else {
            setInitializationException(new InterruptedException("Initialization was forcibly interrupted"));
        }
    }

    public void init() {
        // create new thread to do initialization
        if (!isInitializing() && !isReady()) {
            setInitializing(true);
            initThread = new Thread((new Runnable() {
                public void run() {
                    // call doInitialization() provided by subclasses
                    try {
                        getLog().debug("Initializing " + Initializable.this.getClass().getSimpleName() + "...");
                        doInitialization();
                        setReady(true);
                        setInitializing(false);
                        getLog().debug("..." + Initializable.this.getClass().getSimpleName() + " initialized ok");
                    }
                    catch (Exception e) {
                        getLog().error("Failed to initialize " + Initializable.this.getClass().getSimpleName(), e);
                        setInitializationException(e);
                    }
                }
            }));
            initThread.start();
        }
    }

    protected abstract void doInitialization() throws Exception;
}
