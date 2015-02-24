package uk.ac.ebi.spot.goci.model;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 20/02/15
 */
public enum CatalogHeaderBinding {
    ID("ID", false, false),
    CATALOG_ADDED_DATE("CATALOG_ADDED_DATE", "DATE ADDED TO CATALOG", "DATE ADDED TO CATALOG", true),
    PUBMED_ID("PUBMED_ID", "PUBMEDID", "PUBMEDID"),
    AUTHOR("AUTHOR", "FIRST AUTHOR", "FIRST AUTHOR"),
    PUBLICATION_DATE("PUBLICATION_DATE", "DATE", "DATE", true),
    JOURNAL("JOURNAL", "JOURNAL", "JOURNAL"),
    LINK("LINK", "LINK", "LINK"),
    STUDY("STUDY", "STUDY", "STUDY"),
    DISEASE_TRAIT("DISEASE_TRAIT", "DISEASE/TRAIT", "DISEASE/TRAIT"),
    INITIAL_SAMPLE_DESCRIPTION("INITIAL_SAMPLE_DESCRIPTION", "INITIAL SAMPLE SIZE", "INITIAL SAMPLE SIZE"),
    REPLICATE_SAMPLE_DESCRIPTION("REPLICATE_SAMPLE_DESCRIPTION", "REPLICATION SAMPLE SIZE", "REPLICATION SAMPLE SIZE"),
    REGION("REGION", "REGION", "CYTOGENETIC_LOC"),
    CHROMOSOME_NAME("CHROMOSOME_NAME", false, "CHR_ID"),
    CHROMOSOME_POSITION("CHROMOSOME_POSITION", false, "CHR_POS"),
    REPORTED_GENE("REPORTED_GENE", "REPORTED GENE(S)", "REPORTED GENE(S)"),
    MAPPED_GENE("MAPPED_GENE", false, true),
    DOWNLOAD_MAPPED_GENE(null, false, "MAPPED GENE"),
    ENTREZ_GENE_ID("ENTREZ_GENE_ID", false, "SNP_GENE_IDS"),
    UPSTREAM_MAPPED_GENE("UPSTREAM_MAPPED_GENE", false, true),
    UPSTREAM_ENTREZ_GENE_ID("UPSTREAM_ENTREZ_GENE_ID", false, "UPSTREAM_GENE_ID"),
    UPSTREAM_GENE_DISTANCE("UPSTREAM_GENE_DISTANCE", false, "UPSTREAM_GENE_DISTANCE"),
    DOWNSTREAM_MAPPED_GENE("DOWNSTREAM_MAPPED_GENE", false, true),
    DOWNSTREAM_ENTREZ_GENE_ID("DOWNSTREAM_ENTREZ_GENE_ID", false, "DOWNSTREAM_GENE_ID"),
    DOWNSTREAM_GENE_DISTANCE("DOWNSTREAM_GENE_DISTANCE", false, "DOWNSTREAM GENE DISTANCE"),
    STRONGEST_SNP_RISK_ALLELE("STRONGEST_SNP_RISK_ALLELE", "STRONGEST SNP-RISK ALLELE", "STRONGEST SNP-RISK ALLELE"),
    SNP_RSID("SNP_RSID", "SNPS", "SNPS"),
    MERGED("MERGED", false, "MERGED"),
    SNP_ID("SNP_ID", false, false),
    CONTEXT("CONTEXT", false, "CONTEXT"),
    IS_INTERGENIC("IS_INTERGENIC", false, true),
    DOWNLOAD_INTERGENIC(null, false, "INTERGENIC"),
    RISK_ALLELE_FREQUENCY("RISK_ALLELE_FREQUENCY", "RISK ALLELE FREQUENCY", "RISK ALLELE FREQUENCY"),
    P_VALUE("P_VALUE", "P-VALUE", "P-VALUE"),
    P_VALUE_QUALIFIER("P_VALUE_QUALIFIER", "P-VALUE (TEXT)", "P-VALUE (TEXT)"),
    OR_BETA("OR_BETA", "OR OR BETA", "OR or BETA"),
    CI("CI", true, true),
    CI_QUALIFIER("CI_QUALIFIER", true, true),
    SPREADSHEET_CI(null, "95% CI (TEXT)", "95% CI (TEXT)"),
    PLATFORM("PLATFORM", "PLATFORM [SNPS PASSING QC]", "PLATFORM [SNPS PASSING QC]"),
    CNV("CNV", true, true),
    SPREADSHEET_CNV(null, "CNV", "CNV"),
    ASSOCIATION_ID("ASSOCIATION_ID", "GWASTUDIESSNPID", false),
    STUDY_ID("STUDY_ID", "GWASTUDYID", false),
    RESULT_PUBLISHED("RESULT_PUBLISHED", true, false, true),
    NCBI_RESULT_PUBLISHED(null, "RESULTPUBLISHED", false),
    CURATION_STATUS("CURATION_STATUS", false, false);

    private String databaseName;
    private boolean isNcbiRequired;
    private String ncbiName;
    private boolean isDownloadRequired;
    private String downloadName;
    private boolean isDate;

    CatalogHeaderBinding(String databaseName, String ncbiName, String downloadName) {
        this(databaseName, true, ncbiName, true, downloadName, false);
    }

    CatalogHeaderBinding(String databaseName, String ncbiName, String downloadName, boolean isDate) {
        this(databaseName, true, ncbiName, true, downloadName, isDate);
    }

    CatalogHeaderBinding(String databaseName, boolean isNcbiRequired, String downloadName) {
        this(databaseName, isNcbiRequired, null, true, downloadName, false);
    }

    CatalogHeaderBinding(String databaseName, boolean isNcbiRequired, String downloadName, boolean isDate) {
        this(databaseName, isNcbiRequired, null, true, downloadName, isDate);
    }

    CatalogHeaderBinding(String databaseName, String ncbiName, boolean isDownloadRequired) {
        this(databaseName, true, ncbiName, isDownloadRequired, null, false);
    }

    CatalogHeaderBinding(String databaseName, String ncbiName, boolean isDownloadRequired, boolean isDate) {
        this(databaseName, true, ncbiName, isDownloadRequired, null, isDate);
    }

    CatalogHeaderBinding(String databaseName, boolean isNcbiRequired, boolean isDownloadRequired) {
        this(databaseName, isNcbiRequired, null, isDownloadRequired, null, false);
    }

    CatalogHeaderBinding(String databaseName, boolean isNcbiRequired, boolean isDownloadRequired, boolean isDate) {
        this(databaseName, isNcbiRequired, null, isDownloadRequired, null, isDate);
    }

    CatalogHeaderBinding(String databaseName,
                         boolean isNcbiRequired,
                         String ncbiName,
                         boolean isDownloadRequired,
                         String downloadName,
                         boolean isDate) {
        this.databaseName = databaseName;
        this.isNcbiRequired = isNcbiRequired;
        if (isNcbiRequired) {
            this.ncbiName = ncbiName;
        }
        this.isDownloadRequired = isDownloadRequired;
        if (isDownloadRequired) {
            this.downloadName = downloadName;
        }
        this.isDate = isDate;
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public String getNcbiName() {
        return this.ncbiName;
    }

    boolean requiredByNcbi() {
        return this.isNcbiRequired;
    }

    public String getDownloadName() {
        return this.downloadName;
    }

    boolean requiredByDownload() {
        return this.isDownloadRequired;
    }

    public boolean isDate() {
        return this.isDate;
    }
}
