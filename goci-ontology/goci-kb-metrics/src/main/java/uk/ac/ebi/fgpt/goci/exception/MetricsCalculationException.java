package uk.ac.ebi.fgpt.goci.exception;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 09/08/12
 */
public class MetricsCalculationException extends Throwable {
    public MetricsCalculationException() {
    }

    public MetricsCalculationException(String message) {
        super(message);
    }

    public MetricsCalculationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetricsCalculationException(Throwable cause) {
        super(cause);
    }

    public MetricsCalculationException(String message,
                                       Throwable cause,
                                       boolean enableSuppression,
                                       boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
