package uk.ac.ebi.spot.goci.model;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 20/02/15
 */
public enum CatalogHeaderBinding {
    ID("ID", false, false, false),
    CATALOG_ADDED_DATE("CATALOG_ADDED_DATE", "DATE ADDED TO CATALOG", "DATE ADDED TO CATALOG", false, true),
    PUBMED_ID("PUBMED_ID", "PUBMEDID", "PUBMEDID", false),
    AUTHOR("AUTHOR", "FIRST AUTHOR", "FIRST AUTHOR", false),
    PUBLICATION_DATE("PUBLICATION_DATE", "DATE", "DATE", false, true),
    JOURNAL("JOURNAL", "JOURNAL", "JOURNAL", false),
    LINK("LINK", "LINK", "LINK", false),
    STUDY("STUDY", "STUDY", "STUDY", false),
    DISEASE_TRAIT("DISEASE_TRAIT", "DISEASE/TRAIT", "DISEASE/TRAIT", false),
    INITIAL_SAMPLE_DESCRIPTION("INITIAL_SAMPLE_DESCRIPTION", "INITIAL SAMPLE SIZE", "INITIAL SAMPLE DESCRIPTION", false),
    REPLICATE_SAMPLE_DESCRIPTION("REPLICATE_SAMPLE_DESCRIPTION",
                                 "REPLICATION SAMPLE SIZE",
                                 "REPLICATION SAMPLE DESCRIPTION",
                                 false),
    REGION("REGION", "REGION", "REGION", "cytogenetic_loc"),
    CHROMOSOME_NAME("CHROMOSOME_NAME", false, "CHR_ID", "chr_id"),
    CHROMOSOME_POSITION("CHROMOSOME_POSITION", false, "CHR_POS", "chr_pos"),
    REPORTED_GENE("REPORTED_GENE", "REPORTED GENE(S)", "REPORTED GENE(S)", false),
    MAPPED_GENE("MAPPED_GENE", false, true, false),
    DOWNLOAD_MAPPED_GENE(null, false, "MAPPED_GENE", false),
    ENTREZ_GENE_ID("ENTREZ_GENE_ID", false, "SNP_GENE_IDS", false),
    UPSTREAM_MAPPED_GENE("UPSTREAM_MAPPED_GENE", false, true, "upstream_gene_symbol"),
    UPSTREAM_ENTREZ_GENE_ID("UPSTREAM_ENTREZ_GENE_ID", false, "UPSTREAM_GENE_ID", "upstream_gene"),
    UPSTREAM_GENE_DISTANCE("UPSTREAM_GENE_DISTANCE", false, "UPSTREAM_GENE_DISTANCE", "upstream_gene_distance"),
    DOWNSTREAM_MAPPED_GENE("DOWNSTREAM_MAPPED_GENE", false, true, "downstream_gene_symbol"),
    DOWNSTREAM_ENTREZ_GENE_ID("DOWNSTREAM_ENTREZ_GENE_ID", false, "DOWNSTREAM_GENE_ID", "downstream_gene"),
    DOWNSTREAM_GENE_DISTANCE("DOWNSTREAM_GENE_DISTANCE", false, "DOWNSTREAM_GENE_DISTANCE", "downstream_gene_distance"),
    STRONGEST_SNP_RISK_ALLELE("STRONGEST_SNP_RISK_ALLELE",
                              "STRONGEST SNP-RISK ALLELE",
                              "STRONGEST SNP-RISK ALLELE",
                              false),
    SNP_RSID("SNP_RSID", "SNPS", "SNPS", false),
    SNP_RSID_FOR_ID("SNP_RSID", false, true, false),
    DOWNLOAD_SNP_ID(null, false, "SNP_ID_CURRENT", false),
    MERGED("MERGED", false, "MERGED", false),
    SNP_ID("SNP_ID", false, false, false),
    CONTEXT("CONTEXT", false, "CONTEXT", false),
    IS_INTERGENIC("IS_INTERGENIC", false, true, "intergenic"),
    DOWNLOAD_INTERGENIC(null, false, "INTERGENIC", false),
    RISK_ALLELE_FREQUENCY("RISK_ALLELE_FREQUENCY", "RISK ALLELE FREQUENCY", "RISK ALLELE FREQUENCY", false),
    P_VALUE("P_VALUE", "P-VALUE", "P-VALUE", false),
    P_VALUE_FOR_MLOG("P_VALUE", false, true, false),
    DOWNLOAD_P_VALUE_MLOG(null, false, "PVALUE_MLOG", false),
    P_VALUE_QUALIFIER("P_VALUE_QUALIFIER", "P-VALUE (TEXT)", "P-VALUE (TEXT)", false),
    OR_BETA("OR_BETA", "OR OR BETA", "OR or BETA", false),
    CI("CI", true, true, false),
    CI_QUALIFIER("CI_QUALIFIER", true, true, false),
    SPREADSHEET_CI(null, "95% CI (TEXT)", "95% CI (TEXT)", false),
    PLATFORM("PLATFORM", "PLATFORM [SNPS PASSING QC]", "PLATFORM [SNPS PASSING QC]", false),
    CNV("CNV", true, true, false),
    SPREADSHEET_CNV(null, "CNV", "CNV", false),
    ASSOCIATION_ID("ASSOCIATION_ID", "GWASTUDIESSNPID", false, "studies_snp_id"),
    STUDY_ID("STUDY_ID", "GWASTUDYID", false, "gwas_study_id"),
    RESULT_PUBLISHED("RESULT_PUBLISHED", true, false, false, false),
    NCBI_RESULT_PUBLISHED(null, "RESULTPUBLISHED", false, false),
    CURATION_STATUS("CURATION_STATUS", false, false, false),
    // Added to deal with file returned from NCBI
    PUBMED_ID_ERROR("PUBMED_ID_ERROR", false, false, "pubmdID_error"),
    NCBI_PAPER_TITLE("NCBI_PAPER_TITLE", false, false, "paper_title"),
    NCBI_FIRST_AUTHOR("NCBI_FIRST_AUTHOR", false, false, "author_first"),
    NCBI_NORMALISED_FIRST_AUTHOR("NCBI_NORMALIZED_FIRST_AUTHOR", false, false, "normalized_author_first"),
    NCBI_FIRST_UPDATE_DATE("NCBI_FIRST_UPDATE_DATE", false, false, "pub_date"),
    SNP_ERROR("SNP_ERROR", false, false, "snp_id_error"),
    GENE_ERROR("GENE_ERROR", false, false, "gene_id_error"),
    SNP_GENE_ON_DIFF_CHR("SNP_GENE_ON_DIFF_CHR", false, false, "snp_gene_on_diff_chr"),
    NO_GENE_FOR_SYMBOL("NO_GENE_FOR_SYMBOL", false, false, "no_geneid_for_symbol"),
    GENE_NOT_ON_GENOME("GENE_NOT_ON_GENOME", false, false, "gene_not_on_genome");

    private String databaseName;
    private boolean isNcbiRequired;
    private String ncbiName;
    private boolean isDownloadRequired;
    private String downloadName;
    private boolean isLoadRequired;
    private String loadName;
    private boolean isDate;

    CatalogHeaderBinding(String databaseName, String ncbiName, String downloadName, String loadName) {
        this(databaseName, true, ncbiName, true, downloadName, true, loadName, false);
    }

    CatalogHeaderBinding(String databaseName, String ncbiName, String downloadName, String loadName, boolean isDate) {
        this(databaseName, true, ncbiName, true, downloadName, true, loadName, isDate);
    }

    CatalogHeaderBinding(String databaseName, boolean isNcbiRequired, String downloadName, String loadName) {
        this(databaseName, isNcbiRequired, null, true, downloadName, true, loadName, false);
    }

    CatalogHeaderBinding(String databaseName,
                         boolean isNcbiRequired,
                         String downloadName,
                         String loadName,
                         boolean isDate) {
        this(databaseName, isNcbiRequired, null, true, downloadName, true, loadName, isDate);
    }

    CatalogHeaderBinding(String databaseName, String ncbiName, boolean isDownloadRequired, String loadName) {
        this(databaseName, true, ncbiName, isDownloadRequired, null, true, loadName, false);
    }

    CatalogHeaderBinding(String databaseName,
                         String ncbiName,
                         boolean isDownloadRequired,
                         String loadName,
                         boolean isDate) {
        this(databaseName, true, ncbiName, isDownloadRequired, null, true, loadName, isDate);
    }

    CatalogHeaderBinding(String databaseName, boolean isNcbiRequired, boolean isDownloadRequired, String loadName) {
        this(databaseName, isNcbiRequired, null, isDownloadRequired, null, true, loadName, false);
    }

    CatalogHeaderBinding(String databaseName,
                         boolean isNcbiRequired,
                         boolean isDownloadRequired,
                         String loadName,
                         boolean isDate) {
        this(databaseName, isNcbiRequired, null, isDownloadRequired, null, true, loadName, isDate);
    }


    CatalogHeaderBinding(String databaseName, String ncbiName, String downloadName, boolean isLoadRequired) {
        this(databaseName, true, ncbiName, true, downloadName, isLoadRequired, null, false);
    }

    CatalogHeaderBinding(String databaseName,
                         String ncbiName,
                         String downloadName,
                         boolean isLoadRequired,
                         boolean isDate) {
        this(databaseName, true, ncbiName, true, downloadName, isLoadRequired, null, isDate);
    }

    CatalogHeaderBinding(String databaseName, boolean isNcbiRequired, String downloadName, boolean isLoadRequired) {
        this(databaseName, isNcbiRequired, null, true, downloadName, isLoadRequired, null, false);
    }

    CatalogHeaderBinding(String databaseName,
                         boolean isNcbiRequired,
                         String downloadName,
                         boolean isLoadRequired,
                         boolean isDate) {
        this(databaseName, isNcbiRequired, null, true, downloadName, isLoadRequired, null, isDate);
    }

    CatalogHeaderBinding(String databaseName, String ncbiName, boolean isDownloadRequired, boolean isLoadRequired) {
        this(databaseName, true, ncbiName, isDownloadRequired, null, isLoadRequired, null, false);
    }

    CatalogHeaderBinding(String databaseName,
                         String ncbiName,
                         boolean isDownloadRequired,
                         boolean isLoadRequired,
                         boolean isDate) {
        this(databaseName, true, ncbiName, isDownloadRequired, null, isLoadRequired, null, isDate);
    }

    CatalogHeaderBinding(String databaseName,
                         boolean isNcbiRequired,
                         boolean isDownloadRequired,
                         boolean isLoadRequired) {
        this(databaseName, isNcbiRequired, null, isDownloadRequired, null, isLoadRequired, null, false);
    }

    CatalogHeaderBinding(String databaseName,
                         boolean isNcbiRequired,
                         boolean isDownloadRequired,
                         boolean isLoadRequired,
                         boolean isDate) {
        this(databaseName, isNcbiRequired, null, isDownloadRequired, null, isLoadRequired, null, isDate);
    }


    CatalogHeaderBinding(String databaseName,
                         boolean isNcbiRequired,
                         String ncbiName,
                         boolean isDownloadRequired,
                         String downloadName,
                         boolean isLoadRequired,
                         String loadName,
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
        this.isLoadRequired = isLoadRequired;
        if (isLoadRequired) {
            this.loadName = loadName;
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

    public String getLoadName() {
        return this.loadName;
    }

    public boolean requiredByLoad() {
        return this.isLoadRequired;
    }

    public boolean isDate() {
        return this.isDate;
    }
}
