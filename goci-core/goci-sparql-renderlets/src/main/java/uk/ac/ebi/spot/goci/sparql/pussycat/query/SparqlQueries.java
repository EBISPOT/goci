package uk.ac.ebi.spot.goci.sparql.pussycat.query;

/**
 * Created by dwelter on 02/09/15.
 */
public class SparqlQueries {

    public static final String BAND_FOR_ASSOCIATION =
            "SELECT ?band " +
                    "WHERE { ?association a gt:TraitAssociation ; oban:has_subject ?snp . " +
                    "?snp ro:located_in ?band . " +
                    "FILTER (?association = ??) }";
    public static final String ASSOCIATIONS_IN_BAND =
            "SELECT ?association " +
                    "WHERE { ?association a gt:TraitAssociation ; oban:has_subject ?snp . " +
                    "?snp ro:located_in ?band . " +
                    "FILTER (?band = ??) }";
        public static final String ASSOCIATIONS_IN_BAND_PVALUE_FILTER =
                "SELECT ?association " +
                        "WHERE { ?association a gt:TraitAssociation ; oban:has_subject ?snp ; gt:has_p_value ?pvalue . " +
                        "?snp ro:located_in ?band . " +
                        "FILTER (?band = ??) " +
                        "FILTER (?pvalue < ??) " +
                        "FILTER (?pvalue >= ??) }";
    public static final String ASSOCIATIONS_IN_BAND_NAME =
            "SELECT ?association " +
                    "WHERE { ?association a gt:TraitAssociation ; oban:has_subject ?snp . " +
                    "?snp ro:located_in ?bandUri . " +
                    "?bandUri rdfs:label ?band . " +
                    "FILTER ( ?band = ?? ) }";
        public static final String ASSOCIATIONS_IN_BAND_NAME_PVALUE_FILTER =
                "SELECT ?association " +
                        "WHERE { ?association a gt:TraitAssociation ; oban:has_subject ?snp ; gt:has_p_value ?pvalue . " +
                        "?snp ro:located_in ?bandUri . " +
                        "?bandUri rdfs:label ?band . " +
                        "FILTER (?band = ??) " +
                        "FILTER (?pvalue < ??) " +
                        "FILTER (?pvalue >= ??) }";
        public static final String TRAITS_IN_BAND =
            "SELECT ?trait ?band " +
                    "WHERE { ?association a gt:TraitAssociation ; oban:has_subject ?snp ; oban:has_object ?trait . " +
                    "?snp ro:located_in ?band ; " +
                    "FILTER (?band = ??) }";
        public static final String TRAITS_IN_BAND_PVALUE_FILTER =
                "SELECT ?trait ?band " +
                        "WHERE { ?association a gt:TraitAssociation ; oban:has_subject ?snp ; oban:has_object ?trait ; gt:has_p_value ?pvalue . " +
                        "?snp ro:located_in ?band ; " +
                        "FILTER (?band = ??)" +
                        "FILTER (?pvalue < ??) " +
                        "FILTER (?pvalue >= ??) }";
    public static final String TRAITS_IN_BAND_NAME =
            "SELECT ?trait ?band " +
                    "WHERE { ?association a gt:TraitAssociation ; oban:has_subject ?snp ; oban:has_object ?trait . " +
                    "?snp ro:located_in ?bandUri . " +
                    "?bandUri rdfs:label ?band . " +
                    "FILTER (?band = ??) }";
        public static final String TRAITS_IN_BAND_NAME_PVALUE_FILTER =
                "SELECT ?trait ?band " +
                        "WHERE { ?association a gt:TraitAssociation ; oban:has_subject ?snp ; oban:has_object ?trait ; gt:has_p_value ?pvalue . " +
                        "?snp ro:located_in ?bandUri . " +
                        "?bandUri rdfs:label ?band . " +
                        "FILTER (?band = ??) " +
                        "FILTER (?pvalue < ??) " +
                        "FILTER (?pvalue >= ??) }";
    public static final String DATE_OF_TRAIT_ID_FOR_BAND =
            "SELECT DISTINCT ?trait (min(?date) as ?first) " +
                    "WHERE { " +
                    "?association a gt:TraitAssociation ; oban:has_subject ?snp ; oban:has_object ?trait ; ro:part_of ?study . " +
                    "?study gt:has_publication_date ?date . " +
                    "?snp ro:located_in ?band . " +
                    "FILTER ( ?band = ?? ) } " +
                    "GROUP BY ?trait " +
                    "ORDER BY ?first";
        public static final String DATE_OF_TRAIT_ID_FOR_BAND_PVALUE_FILTER  =
                "SELECT DISTINCT ?trait (min(?date) as ?first) " +
                        "WHERE { " +
                        "?association a gt:TraitAssociation ; oban:has_subject ?snp ; oban:has_object ?trait ; ro:part_of ?study ; gt:has_p_value ?pvalue. " +
                        "?study gt:has_publication_date ?date . " +
                        "?snp ro:located_in ?band . " +
                        "FILTER ( ?band = ?? ) " +
                        "FILTER (?pvalue < ??) " +
                        "FILTER (?pvalue >= ??)} " +
                        "GROUP BY ?trait " +
                        "ORDER BY ?first";
    public static final String ASSOCIATIONS_FOR_TRAIT =
            "SELECT ?association " +
                    "WHERE { ?association a gt:TraitAssociation ; oban:has_object ?trait . " +
                    "FILTER (?trait = ??) }";
        public static final String ASSOCIATIONS_FOR_TRAIT_PVALUE_FILTER =
                "SELECT ?association " +
                        "WHERE { ?association a gt:TraitAssociation ; oban:has_object ?trait ; gt:has_p_value ?pvalue . " +
                        "FILTER (?trait = ??) " +
                        "FILTER (?pvalue < ??) " +
                        "FILTER (?pvalue >= ??) }";
    public static final String TRAITS_BY_NAME =
            "SELECT DISTINCT ?trait " +
                    "WHERE {{ " +
                    "  ?trait rdfs:label ?label ." +
                    "  FILTER (?label = ??) " +
                    "} " +
                    "UNION { " +
                    "  ?trait efo:alternative_term ?synonym . " +
                    "  FILTER (?synonym = ??) " +
                    "}}";
    public static final String PARENTS_AND_DISTANCE_BY_TRAIT =
            "SELECT ?type (count(DISTINCT ?ancestor) as ?count) " +
                    "WHERE { " +
                    "?? rdf:type ?trait . " +
                    "?trait rdfs:subClassOf* ?type . " +
                    "?type rdfs:subClassOf* ?ancestor . " +
                    "FILTER ( ?trait != owl:Class ) .  " +
                    "FILTER ( ?trait != owl:NamedIndividual ) . } " +
                    "GROUP BY ?type " +
                    "ORDER BY desc(?count)";

}
