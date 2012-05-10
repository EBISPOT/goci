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
    public static final String CONGENITAL = EFO_ONTOLOGY_BASE_IRI + "/EFO_0003915";
    public static final String GENETIC = EFO_ONTOLOGY_BASE_IRI + "/EFO_0000508";
    public static final String DISEASE = EFO_ONTOLOGY_BASE_IRI + "/EFO_0000408";

    public static final String MEASUREMENT = EFO_ONTOLOGY_BASE_IRI + "/EFO_0001444";
    public static final String SIGN_SYMPTOM = EFO_ONTOLOGY_BASE_IRI + "/EFO_0003765";
    public static final String BEHAVIOUR = EFO_ONTOLOGY_BASE_IRI + "/EFO_0000282";
    public static final String MENTAL_PROCESS = EFO_ONTOLOGY_BASE_IRI + "/EFO_0004323";
    public static final String PHYSIO_PROCESS = EFO_ONTOLOGY_BASE_IRI + "/EFO_0003936";
    public static final String MATERIAL_ENTITY = "http://www.ifomis.org/bfo/1.1/snap#MaterialEntity";
    public static final String FUNCTION = "http://www.ifomis.org/bfo/1.1/snap#Function";
    public static final String QUALITY = "http://www.ifomis.org/bfo/1.1/snap#Quality";


    public static final HashMap<String, String> COLOUR_MAP = new HashMap<String, String>();

//    static {
//        COLOUR_MAP.put(EXPERIMENTAL_FACTOR, "#2166AC");
//        COLOUR_MAP.put(CARDIO_VASCULAR, "#A50026");
//        COLOUR_MAP.put(NEURO, "#FDAE61");
//        COLOUR_MAP.put(MENTAL, "#F46D43");
//        COLOUR_MAP.put(SKELETAL, "#D9EF8B");
//        COLOUR_MAP.put(NEOPLASM, "#D73027");
//        COLOUR_MAP.put(MEASUREMENT, "#006837");
//    }

    static {
        COLOUR_MAP.put(EXPERIMENTAL_FACTOR, /*"#663300"*/ "#FFFF33");
        COLOUR_MAP.put(CARDIO_VASCULAR, "#CC0000");
        COLOUR_MAP.put(NEURO, "#660099");
        COLOUR_MAP.put(MENTAL, "#6600FF");
        COLOUR_MAP.put(SKELETAL, "#FF6666");
        COLOUR_MAP.put(NEOPLASM, "#CC33FF");
        COLOUR_MAP.put(CONGENITAL, "#FF00CC");
        COLOUR_MAP.put(GENETIC,"#990099");
        COLOUR_MAP.put(DISEASE, "#660033");
        COLOUR_MAP.put(MEASUREMENT, "#FF6314");
        COLOUR_MAP.put(SIGN_SYMPTOM, "#FFFF00");
        COLOUR_MAP.put(MATERIAL_ENTITY, "#003399");
        COLOUR_MAP.put(FUNCTION, "#006699");
        COLOUR_MAP.put(QUALITY, "#66CCFF");
        COLOUR_MAP.put(BEHAVIOUR, "#336600");
        COLOUR_MAP.put(MENTAL_PROCESS, "#66CC33");
        COLOUR_MAP.put(PHYSIO_PROCESS, "#CCFF00");
    }
          /*
    public static HashMap<String, String> getColourMap(){
        return COLOUR_MAP;
    }
            */

}
