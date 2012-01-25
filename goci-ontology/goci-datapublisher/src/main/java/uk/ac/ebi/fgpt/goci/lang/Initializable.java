package uk.ac.ebi.fgpt.goci.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * An object that can do some initialization work in parallel at startup.  Implementing classes should extend {@link
 * #doInitialization} with the work they wish to do.  All methods that require initialization to complete can check the
 * current state with a call to {@link #isReady()} which returns true or false, or a call to {@link #waitUntilReady()},
 * which blocks whilst initialization completes.
 *
 * @author Tony Burdett
 * @date 25/01/12
 */
public abstract class Initializable {
    private ExecutorService service;

    private boolean ready;
    private Throwable initializationException;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Initializable() {
        this(1);
    }

    protected Initializable(int numberOfStartupThreads) {
        this.service = Executors.newFixedThreadPool(numberOfStartupThreads);
    }

    protected Logger getLog() {
        return log;
    }

    protected synchronized void setReady(boolean ready) {
        this.ready = ready;
        notifyAll();
    }

    protected synchronized void setInitializationException(Throwable t) {
        this.initializationException = t;
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
                wait();
            }
        }
    }

    protected void init() {
        service.submit(new Runnable() {
            public void run() {
                // call doInitialization() provided by subclasses
                doInitialization();
            }
        });
    }

    public abstract void doInitialization();
}
