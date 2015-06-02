package uk.ac.ebi.spot.goci.utils;

/**
 *
 * Some constants for IRIs from the ontology
 *
 * Created by catherineleroy on 13/05/2015.
 */


public class OntologyConstants {
    // Ontology IRIs
    public static final String EFO_ONTOLOGY_SCHEMA_IRI = "http://www.ebi.ac.uk/efo";
    public static final String GWAS_ONTOLOGY_SCHEMA_IRI = "http://rdf.ebi.ac.uk/terms/gwas";
    public static final String GWAS_ONTOLOGY_BASE_IRI = "http://rdf.ebi.ac.uk/dataset/gwas";

    // Class IRIs
    public static final String STUDY_CLASS_IRI = GWAS_ONTOLOGY_SCHEMA_IRI + "/Study";
    public static final String TRAIT_ASSOCIATION_CLASS_IRI = GWAS_ONTOLOGY_SCHEMA_IRI + "/TraitAssociation";
    public static final String SNP_CLASS_IRI = GWAS_ONTOLOGY_SCHEMA_IRI + "/SingleNucleotidePolymorphism";
    public static final String CYTOGENIC_REGION_CLASS_IRI = GWAS_ONTOLOGY_SCHEMA_IRI + "/CytogeneticRegion";
    public static final String CHROMOSOME_CLASS_IRI = GWAS_ONTOLOGY_SCHEMA_IRI + "/Chromosome";
    public static final String EXPERIMENTAL_FACTOR_CLASS_IRI = "http://www.ebi.ac.uk/efo/EFO_0000001";

    // Data Property IRIs
    public static final String HAS_AUTHOR_PROPERTY_IRI = GWAS_ONTOLOGY_SCHEMA_IRI + "/has_author";
    public static final String HAS_BP_POSITION_PROPERTY_IRI = GWAS_ONTOLOGY_SCHEMA_IRI + "/has_basepair_position";
    public static final String HAS_GWAS_TRAIT_NAME_PROPERTY_IRI = GWAS_ONTOLOGY_SCHEMA_IRI + "/has_gwas_trait_name";
    public static final String HAS_LENGTH_PROPERTY_IRI = GWAS_ONTOLOGY_SCHEMA_IRI + "/has_length";
    public static final String HAS_NAME_PROPERTY_IRI = GWAS_ONTOLOGY_SCHEMA_IRI + "/has_name";
    public static final String HAS_P_VALUE_PROPERTY_IRI = GWAS_ONTOLOGY_SCHEMA_IRI + "/has_p_value";
    public static final String HAS_PUBLICATION_DATE_PROPERTY_IRI = GWAS_ONTOLOGY_SCHEMA_IRI + "/has_publication_date";
    public static final String HAS_PUBMED_ID_PROPERTY_IRI = GWAS_ONTOLOGY_SCHEMA_IRI + "/has_pubmed_id";
    public static final String HAS_SNP_REFERENCE_ID_PROPERTY_IRI = GWAS_ONTOLOGY_SCHEMA_IRI + "/has_snp_reference_id";

    // Object Property IRIs
    public static final String ASSOCIATED_WITH_PROPERTY_IRI = GWAS_ONTOLOGY_SCHEMA_IRI + "/associated_with";
    public static final String HAS_PART_PROPERTY_IRI = "http://www.obofoundry.org/ro/ro.owl#has_part";
    public static final String HAS_SUBJECT_IRI = "http://purl.org/oban/has_subject"; // todo - check when finalised
    public static final String IS_SUBJECT_OF_IRI = "http://purl.org/oban/is_subject_of"; // todo - check when finalised
    public static final String HAS_OBJECT_IRI = "http://purl.org/oban/has_object"; // todo - check when finalised
    public static final String IS_OBJECT_OF_IRI = "http://purl.org/oban/is_object_of"; // todo - check when finalised
    public static final String LOCATED_IN_PROPERTY_IRI = "http://www.obofoundry.org/ro/ro.owl#located_in";
    public static final String LOCATION_OF_PROPERTY_IRI =  "http://www.obofoundry.org/ro/ro.owl#location_of";
    public static final String PART_OF_PROPERTY_IRI = "http://www.obofoundry.org/ro/ro.owl#part_of";

    //Datatype IRIs
    public static final String DATE_TIME_DATA_TYPE_IRI = "http://www.w3.org/2001/XMLSchema#dateTime";
}
