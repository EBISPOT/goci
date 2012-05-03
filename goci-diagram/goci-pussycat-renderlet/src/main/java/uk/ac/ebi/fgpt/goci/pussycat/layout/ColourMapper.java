package uk.ac.ebi.fgpt.goci.pussycat.layout;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 03/05/12
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
public class ColourMapper {

    public static final String EFO_ONTOLOGY_BASE_IRI = "http://www.ebi.ac.uk/efo";
    public static final String EXPERIMENTAL_FACTOR = EFO_ONTOLOGY_BASE_IRI + "/EFO_0000001";
    public static final String CARDIO_VASCULAR = EFO_ONTOLOGY_BASE_IRI + "/EFO_0000319";
    public static final String NEURO = EFO_ONTOLOGY_BASE_IRI + "/EFO_0000618";
    public static final String MENTAL = EFO_ONTOLOGY_BASE_IRI + "/EFO_0000677";
    public static final String SKELETAL = EFO_ONTOLOGY_BASE_IRI + "/EFO_0000616";
    public static final String NEOPLASM = EFO_ONTOLOGY_BASE_IRI + "/EFO_0002461";
    public static final String MEASUREMENT = EFO_ONTOLOGY_BASE_IRI + "/EFO_0001444";


    public static final HashMap<String, String> COLOUR_MAP = new HashMap<String, String>();

    static {
        COLOUR_MAP.put(EXPERIMENTAL_FACTOR, "#2166AC");
        COLOUR_MAP.put(CARDIO_VASCULAR, "#A50026");
        COLOUR_MAP.put(NEURO, "#FDAE61");
        COLOUR_MAP.put(MENTAL, "#F46D43");
        COLOUR_MAP.put(SKELETAL, "#D9EF8B");
        COLOUR_MAP.put(NEOPLASM, "#D73027");
        COLOUR_MAP.put(MEASUREMENT, "#006837");


    }
          /*
    public static HashMap<String, String> getColourMap(){
        return COLOUR_MAP;
    }
            */

}
