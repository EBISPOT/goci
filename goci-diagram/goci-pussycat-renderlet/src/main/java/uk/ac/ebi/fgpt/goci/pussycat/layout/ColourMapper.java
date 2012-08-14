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
    public static final String OTHER = EFO_ONTOLOGY_BASE_IRI + "/EFO_0000001";
    public static final String CARDIO_VASCULAR = EFO_ONTOLOGY_BASE_IRI + "/EFO_0000319";
    public static final String NEURO = EFO_ONTOLOGY_BASE_IRI + "/EFO_0000618";
    public static final String NEOPLASM = EFO_ONTOLOGY_BASE_IRI + "/EFO_0000616";
    public static final String DISEASE = EFO_ONTOLOGY_BASE_IRI + "/EFO_0000408";
    public static final String IMMUNE = EFO_ONTOLOGY_BASE_IRI + "/EFO_0000540";
    public static final String DIGESTIVE = EFO_ONTOLOGY_BASE_IRI + "/EFO_0000405";
    public static final String METABOLIC = EFO_ONTOLOGY_BASE_IRI + "/EFO_0000589";


    public static final String MEASUREMENT = EFO_ONTOLOGY_BASE_IRI + "/EFO_0001444";
    public static final String BODY_MSR = EFO_ONTOLOGY_BASE_IRI + "/EFO_0004324";
    public static final String CARDIO_MSR = EFO_ONTOLOGY_BASE_IRI + "/EFO_0004298";
    public static final String LIVER_ENZYME = EFO_ONTOLOGY_BASE_IRI + "/EFO_0004582";
    public static final String BIO_PROCESS = "http://purl.obolibrary.org/obo/GO_0008150";
    public static final String CHEMICAL = "http://purl.obolibrary.org/obo/CHEBI_37577";

    public static final String HEMATOLIGICAL_MSR = EFO_ONTOLOGY_BASE_IRI + "/EFO_0004503";
    public static final String INFLAMMATORY_MSR = EFO_ONTOLOGY_BASE_IRI + "/EFO_0004872";

    public static final String LIPID_MSR = EFO_ONTOLOGY_BASE_IRI + "/EFO_0004529";
    public static final String LIPOPROT_MSR = EFO_ONTOLOGY_BASE_IRI + "/EFO_0004732";


    public static final HashMap<String, String> COLOUR_MAP = new HashMap<String, String>();


    static {
        COLOUR_MAP.put(OTHER, "#A10000");
        COLOUR_MAP.put(CARDIO_VASCULAR, "#8DD3C7");
        COLOUR_MAP.put(NEURO, "#FFFFB3");
        COLOUR_MAP.put(IMMUNE, "#BEBADA");
        COLOUR_MAP.put(DIGESTIVE, "#80B1D3");
        COLOUR_MAP.put(NEOPLASM, "#FB8072");
        COLOUR_MAP.put(METABOLIC, "#FDB462");
        COLOUR_MAP.put(DISEASE, "#FCCDE5");
        COLOUR_MAP.put(MEASUREMENT,"#B3DE69");
        COLOUR_MAP.put(BODY_MSR, "#66CCFF");
        COLOUR_MAP.put(CARDIO_MSR, "#BC80BD");
        COLOUR_MAP.put(CHEMICAL, "#CCEBC5");
        COLOUR_MAP.put(HEMATOLIGICAL_MSR, "#FFED6F");
        COLOUR_MAP.put(INFLAMMATORY_MSR, "#006699");
        COLOUR_MAP.put(LIVER_ENZYME, "#669900");
        COLOUR_MAP.put(LIPID_MSR, "#FF3399");
        COLOUR_MAP.put(LIPOPROT_MSR, "#FF3399");
        COLOUR_MAP.put(BIO_PROCESS, "#B7704C");

/*        Behaviour       = liver enzyme
        Biological Process
        Cancer
        Cardiovascular disorder
        Congenital Disorder    = metabolic
        Function             = hemato
        Genetic Disorder     = measurement
        Material entity          = chemical
        Measurement     =  body_msr
        Mental Disorder            = immune
        Mental process              = lipid
        Neurological Disorder
        Other disease
        Quality                = inflammatory
        sign or symptom         = cardio_msr
        Skeletal Disorder  = digestive            */


        //       Jackie
/*        COLOUR_MAP.put(LIVER_ENZYME, "#6C7F00");
        COLOUR_MAP.put(BIO_PROCESS, "#7E412D");
        COLOUR_MAP.put(NEOPLASM, "#D4FFFF");
        COLOUR_MAP.put(CARDIO_VASCULAR, "#FF904E");
        COLOUR_MAP.put(METABOLIC, "#A296BF");
        COLOUR_MAP.put(HEMATOLIGICAL_MSR, "#FFFFA9");
        COLOUR_MAP.put(MEASUREMENT, "#4069B2");
        COLOUR_MAP.put(CHEMICAL, "#DEFFBF");
        COLOUR_MAP.put(BODY_MSR, "#71B2D0");
        COLOUR_MAP.put(IMMUNE, "#E1AFEB");
        COLOUR_MAP.put(LIPID_MSR, "#898989");
        COLOUR_MAP.put(LIPOPROT_MSR, "#898989");
        COLOUR_MAP.put(NEURO, "#BF0067");
        COLOUR_MAP.put(DISEASE, "#81009F");
        COLOUR_MAP.put(INFLAMMATORY_MSR, "#EE282B");
        COLOUR_MAP.put(CARDIO_MSR, "#FFD67F");
        COLOUR_MAP.put(DIGESTIVE, "#6ABD45");   */




        //Joannella
/*        COLOUR_MAP.put(LIVER_ENZYME, "#4069B2");
        COLOUR_MAP.put(BIO_PROCESS, "#715149");
        COLOUR_MAP.put(NEOPLASM, "#C4583D");
        COLOUR_MAP.put(CARDIO_VASCULAR, "#DEFFBF");
        COLOUR_MAP.put(METABOLIC, "#FF9092");
        COLOUR_MAP.put(HEMATOLIGICAL_MSR, "#F6EB16");
        COLOUR_MAP.put(MEASUREMENT, "#DEE338");
        COLOUR_MAP.put(CHEMICAL, "#6ABD45");
        COLOUR_MAP.put(BODY_MSR, "#0091BA");
        COLOUR_MAP.put(IMMUNE, "#8500DF");
        COLOUR_MAP.put(LIPID_MSR, "#A296BF");
        COLOUR_MAP.put(LIPOPROT_MSR, "#A296BF");
        COLOUR_MAP.put(NEURO, "#FEBF10");
        COLOUR_MAP.put(DISEASE, "#CD4B9B");
        COLOUR_MAP.put(INFLAMMATORY_MSR, "#CBC8C8");
        COLOUR_MAP.put(CARDIO_MSR, "#DFC7E1");
        COLOUR_MAP.put(DIGESTIVE, "#BDCFFF");
        */

        //Dani
        /*COLOUR_MAP.put(LIVER_ENZYME, "#A296BF");
        COLOUR_MAP.put(BIO_PROCESS, "#007F74");
        COLOUR_MAP.put(NEOPLASM, "#4463AE");
        COLOUR_MAP.put(CARDIO_VASCULAR, "#EE282B");
        COLOUR_MAP.put(METABOLIC, "#C8473F");
        COLOUR_MAP.put(HEMATOLIGICAL_MSR, "#996633");
        COLOUR_MAP.put(MEASUREMENT, "#F3FFBF");
        COLOUR_MAP.put(CHEMICAL, "#BD0DF");
        COLOUR_MAP.put(BODY_MSR, "#99CC33");
        COLOUR_MAP.put(IMMUNE, "#71B2D0");
        COLOUR_MAP.put(LIPID_MSR, "#FCD700");
        COLOUR_MAP.put(LIPOPROT_MSR, "#FCD700");
        COLOUR_MAP.put(NEURO, "#6B005B");
        COLOUR_MAP.put(DISEASE, "#F3961F");
        COLOUR_MAP.put(INFLAMMATORY_MSR, "#D4FFFF");
        COLOUR_MAP.put(CARDIO_MSR, "#7F044");
        COLOUR_MAP.put(DIGESTIVE, "#BF7C9C");
        */
    }
          /*
    public static HashMap<String, String> getColourMap(){
        return COLOUR_MAP;
    }
    
         */

}
