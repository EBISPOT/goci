package uk.ac.ebi.fgpt.goci.pussycat.exception;

/**
 * Created by dwelter on 19/05/14.
 */
public class SPARQLQueryException extends RuntimeException{
    public SPARQLQueryException(Exception e) {
        super(e);
    }

    public SPARQLQueryException(String s, Exception e) {
        super(s, e);
    }

    public SPARQLQueryException(String s) {
        super(s);
    }
}
