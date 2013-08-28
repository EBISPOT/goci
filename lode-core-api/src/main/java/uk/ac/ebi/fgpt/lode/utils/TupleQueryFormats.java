package uk.ac.ebi.fgpt.lode.utils;

/**
 * @author Simon Jupp
 * @date 21/02/2013
 * Functional Genomics Group EMBL-EBI
 */
public enum TupleQueryFormats{


    XML ("XML"),
    CSV ("CSV"),
    TSV ("TSV"),
    JSON ("JSON");


    private final String format;

    private TupleQueryFormats(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return format;
    }
}
