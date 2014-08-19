package uk.ac.ebi.fgpt.lode.utils;

/**
 * @author Simon Jupp
 * @date 21/02/2013
 * Functional Genomics Group EMBL-EBI
 */
public enum GraphQueryFormats {

    RDFXML ("RDF/XML"),
    N3 ("N3"),
    JSON ("JSON-LD"),
    TURTLE ("TURTLE");

    private final String format;

    private GraphQueryFormats(final String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return format;
    }
}
