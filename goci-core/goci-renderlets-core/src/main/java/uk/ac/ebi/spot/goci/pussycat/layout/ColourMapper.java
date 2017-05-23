package uk.ac.ebi.spot.goci.pussycat.layout;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA. User: dwelter Date: 03/05/12 Time: 14:45 To change this template use File | Settings |
 * File Templates.
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
    public static final String DRUG_RESP = "http://purl.obolibrary.org/obo/GO_0042493";

    public static final String HEMATOLOGICAL_MSR = EFO_ONTOLOGY_BASE_IRI + "/EFO_0004503";
    public static final String INFLAMMATORY_MSR = EFO_ONTOLOGY_BASE_IRI + "/EFO_0004872";

    public static final String LIPID_MSR = EFO_ONTOLOGY_BASE_IRI + "/EFO_0005105";

    public static final HashMap<String, String> COLOUR_MAP = new HashMap<String, String>();

    static {
        COLOUR_MAP.put(CARDIO_VASCULAR, "#B33232");
        COLOUR_MAP.put(HEMATOLOGICAL_MSR, "#8DD3C7");
        COLOUR_MAP.put(NEURO, "#FFFFB3");
        COLOUR_MAP.put(BIO_PROCESS, "#BEBADA");
        COLOUR_MAP.put(CARDIO_MSR, "#80B1D3");
        COLOUR_MAP.put(OTHER, "#FB8072");
        COLOUR_MAP.put(METABOLIC, "#FDB462");
        COLOUR_MAP.put(DRUG_RESP, "#FCCDE5");
        COLOUR_MAP.put(LIPID_MSR, "#B3DE69");
        COLOUR_MAP.put(BODY_MSR, "#66CCFF");
        COLOUR_MAP.put(NEOPLASM, "#BC80BD");
        COLOUR_MAP.put(INFLAMMATORY_MSR, "#CCEBC5");
        COLOUR_MAP.put(IMMUNE, "#FFED6F");
        COLOUR_MAP.put(MEASUREMENT, "#006699");
        COLOUR_MAP.put(LIVER_ENZYME, "#669900");
        COLOUR_MAP.put(DISEASE, "#FF3399");
        COLOUR_MAP.put(DIGESTIVE, "#B7704C");
    }
}
