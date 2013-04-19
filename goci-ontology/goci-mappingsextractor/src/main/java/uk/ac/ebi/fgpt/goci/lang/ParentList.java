package uk.ac.ebi.fgpt.goci.lang;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 26/11/12
 * Time: 14:12
 * To change this template use File | Settings | File Templates.
 */
public class ParentList {

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



    public static final HashMap<String, String> PARENT_URI = new HashMap<String, String>();

    static {

        PARENT_URI.put(CARDIO_VASCULAR, "Cardiovascular disease");
        PARENT_URI.put(HEMATOLOGICAL_MSR, "Hematological measurement");
        PARENT_URI.put(NEURO, "Nervous system disease");
        PARENT_URI.put(BIO_PROCESS, "Biological process");
        PARENT_URI.put(CARDIO_MSR, "Cardiovascular measurement");
        PARENT_URI.put(OTHER, "Other trait");
        PARENT_URI.put(METABOLIC, "Metabolic disease");
        PARENT_URI.put(DRUG_RESP, "Response to drug");
        PARENT_URI.put(LIPID_MSR, "Lipid or lipoprotein measurement");
        PARENT_URI.put(BODY_MSR, "Body measurement");
        PARENT_URI.put(NEOPLASM, "Cancer");
        PARENT_URI.put(INFLAMMATORY_MSR, "Inflammatory measurement");
        PARENT_URI.put(IMMUNE, "Immune system disease");
        PARENT_URI.put(MEASUREMENT, "Other measurement");
        PARENT_URI.put(LIVER_ENZYME, "Liver enzyme measurement");
        PARENT_URI.put(DISEASE, "Other disease");
        PARENT_URI.put(DIGESTIVE, "Digestive system disease");
    }
}
