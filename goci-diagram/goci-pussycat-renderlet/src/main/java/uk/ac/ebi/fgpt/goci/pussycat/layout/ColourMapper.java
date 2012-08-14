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
    public static final String SKELETAL = EFO_ONTOLOGY_BASE_IRI + "/EFO_0002461";
    public static final String NEOPLASM = EFO_ONTOLOGY_BASE_IRI + "/EFO_0000616";
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


    static {
        COLOUR_MAP.put(EXPERIMENTAL_FACTOR, /*"#663300" "#FFFF33" "#BEBEBE"*/ "#FFFFFF");
        COLOUR_MAP.put(CARDIO_VASCULAR, "#8DD3C7");
        COLOUR_MAP.put(NEURO, "#FFFFB3");
        COLOUR_MAP.put(MENTAL, "#BEBADA");
        COLOUR_MAP.put(SKELETAL, "#80B1D3");
        COLOUR_MAP.put(NEOPLASM, "#FB8072");
        COLOUR_MAP.put(CONGENITAL, "#FDB462");
        COLOUR_MAP.put(GENETIC,"#B3DE69");
        COLOUR_MAP.put(DISEASE, "#FCCDE5");
        COLOUR_MAP.put(MEASUREMENT, "#66CCFF");
        COLOUR_MAP.put(SIGN_SYMPTOM, "#BC80BD");
        COLOUR_MAP.put(MATERIAL_ENTITY, "#CCEBC5");
        COLOUR_MAP.put(FUNCTION, "#FFED6F");
        COLOUR_MAP.put(QUALITY, "#006699");
        COLOUR_MAP.put(BEHAVIOUR, "#669900");
        COLOUR_MAP.put(MENTAL_PROCESS, "#FF3399");
        COLOUR_MAP.put(PHYSIO_PROCESS, "#993300");
    }
          /*
    public static HashMap<String, String> getColourMap(){
        return COLOUR_MAP;
    }
    
    Behaviour
Biological Process 
Cancer
Cardiovascular disorder 
Congenital Disorder 
Function
Genetic Disorder 
Material entity
Measurement
Mental Disorder 
Mental process
Neurological Disorder 
Other disease 
Quality
sign or symptom 
Skeletal Disorder 
            */
            
    /* Jackie
    COLOUR_MAP.put(BEHAVIOUR, "#6C7F00");
COLOUR_MAP.put(PHYSIO_PROCESS, "#7E412D");
COLOUR_MAP.put(NEOPLASM, "#D4FFFF");
COLOUR_MAP.put(CARDIO_VASCULAR, "#FF904E");
COLOUR_MAP.put( , "#A296BF");
COLOUR_MAP.put( , "#FFFFA9");
COLOUR_MAP.put( , "#4069B2");
COLOUR_MAP.put( , "#DEFFBF");
COLOUR_MAP.put( , "#71B2D0");
COLOUR_MAP.put( , "#E1AFEB");
COLOUR_MAP.put( , "#898989");
COLOUR_MAP.put( , "#BF0067");
COLOUR_MAP.put( , "#81009F");
COLOUR_MAP.put( , "#EE282B");
COLOUR_MAP.put( , "#FFD67F");
COLOUR_MAP.put( , "#6ABD45");
    
    */        
    
    
    /*Joannella
    COLOUR_MAP.put(BEHAVIOUR, "#4069B2");
COLOUR_MAP.put(PHYSIO_PROCESS, "#715149");
COLOUR_MAP.put(NEOPLASM, "#C4583D");
COLOUR_MAP.put(CARDIO_VASCULAR, "#DEFFBF");
COLOUR_MAP.put( , "#FF9092");
COLOUR_MAP.put( , "#F6EB16");
COLOUR_MAP.put( , "#DEE338");
COLOUR_MAP.put( , "#6ABD45");
COLOUR_MAP.put( , "#0091BA");
COLOUR_MAP.put( , "#8500DF");
COLOUR_MAP.put( , "#A296BF");
COLOUR_MAP.put( , "#FEBF10");
COLOUR_MAP.put( , "#CD4B9B");
COLOUR_MAP.put( , "#CBC8C8");
COLOUR_MAP.put( , "#DFC7E1");
COLOUR_MAP.put( , "#BDCFFF");
    */
    
    /*Dani
    COLOUR_MAP.put(BEHAVIOUR, "#A296BF");
COLOUR_MAP.put(PHYSIO_PROCESS, "#007F74");
COLOUR_MAP.put(NEOPLASM, "#4463AE");
COLOUR_MAP.put(CARDIO_VASCULAR, "#EE282B");
COLOUR_MAP.put( , "#C8473F");
COLOUR_MAP.put( , "#996633");
COLOUR_MAP.put( , "#F3FFBF");
COLOUR_MAP.put( , "#BD0DF");
COLOUR_MAP.put( , "#99CC33");
COLOUR_MAP.put( , "#71B2D0");
COLOUR_MAP.put( , "#FCD700");
COLOUR_MAP.put( , "#6B005B");
COLOUR_MAP.put( , "#F3961F");
COLOUR_MAP.put( , "#D4FFFF");
COLOUR_MAP.put( , "#7F044");
COLOUR_MAP.put( , "#BF7C9C");
    */

}
