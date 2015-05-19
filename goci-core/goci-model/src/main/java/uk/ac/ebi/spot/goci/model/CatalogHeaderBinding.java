package uk.ac.ebi.spot.goci.model;

import java.util.Optional;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 20/02/15
 */
public enum CatalogHeaderBinding {
    ID("ID", new Inclusion.Excluded(), new Inclusion.Excluded(), new Inclusion.Excluded()),
    CATALOG_ADDED_DATE("CATALOG_ADDED_DATE",
                       new Inclusion.FieldIncluded("DATE ADDED TO CATALOG"),
                       new Inclusion.FieldIncluded("DATE ADDED TO CATALOG"),
                       new Inclusion.Excluded(),
                       true),
    PUBMED_ID("PUBMED_ID",
              new Inclusion.FieldIncluded("PUBMEDID"),
              new Inclusion.UniqueIncluded("PUBMEDID"),
              new Inclusion.Excluded()),
    AUTHOR("AUTHOR",
           new Inclusion.FieldIncluded("FIRST AUTHOR"),
           new Inclusion.FieldIncluded("FIRST AUTHOR"),
           new Inclusion.Excluded()),
    PUBLICATION_DATE("PUBLICATION_DATE",
                     new Inclusion.FieldIncluded("DATE"),
                     new Inclusion.FieldIncluded("DATE"),
                     new Inclusion.Excluded(),
                     true),
    JOURNAL("JOURNAL",
            new Inclusion.FieldIncluded("JOURNAL"),
            new Inclusion.FieldIncluded("JOURNAL"),
            new Inclusion.Excluded()),
    LINK("LINK",
         new Inclusion.FieldIncluded("LINK"),
         new Inclusion.FieldIncluded("LINK"),
         new Inclusion.Excluded()),
    STUDY("STUDY",
          new Inclusion.FieldIncluded("STUDY"),
          new Inclusion.FieldIncluded("STUDY"),
          new Inclusion.Excluded()),
    DISEASE_TRAIT("DISEASE_TRAIT",
                  new Inclusion.FieldIncluded("DISEASE/TRAIT"),
                  new Inclusion.UniqueIncluded("DISEASE/TRAIT"),
                  new Inclusion.Excluded()),
    INITIAL_SAMPLE_DESCRIPTION("INITIAL_SAMPLE_DESCRIPTION",
                               new Inclusion.FieldIncluded("INITIAL SAMPLE SIZE"),
                               new Inclusion.UniqueIncluded("INITIAL SAMPLE DESCRIPTION"),
                               new Inclusion.Excluded()),
    REPLICATE_SAMPLE_DESCRIPTION("REPLICATE_SAMPLE_DESCRIPTION",
                                 new Inclusion.FieldIncluded("REPLICATION SAMPLE SIZE"),
                                 new Inclusion.UniqueIncluded("REPLICATION SAMPLE DESCRIPTION"),
                                 new Inclusion.Excluded()),
    REGION("REGION",
           new Inclusion.CommaSeparatedIncluded("REGION"),
           new Inclusion.CommaSeparatedIncluded("REGION"),
           new Inclusion.FieldIncluded("cytogenetic_loc")),
    FUNCTIONAL_CLASS("FUNCTIONAL_CLASS",
                     new Inclusion.Excluded(),
                     new Inclusion.Excluded(),
                     new Inclusion.FieldIncluded("functional_class")),
    CHROMOSOME_NAME("CHROMOSOME_NAME",
                    new Inclusion.Excluded(),
                    new Inclusion.FieldIncluded("CHR_ID"),
                    new Inclusion.FieldIncluded("chr_id")),
    CHROMOSOME_POSITION("CHROMOSOME_POSITION",
                        new Inclusion.Excluded(),
                        new Inclusion.FieldIncluded("CHR_POS"),
                        new Inclusion.FieldIncluded("chr_pos_1based")),
    REPORTED_GENE("REPORTED_GENE",
                  new Inclusion.CommaSeparatedIncluded("REPORTED GENE(S)"),
                  new Inclusion.CommaSeparatedIncluded("REPORTED GENE(S)"),
                  new Inclusion.Excluded()),
    MAPPED_GENE("MAPPED_GENE",
                new Inclusion.Excluded(),
                new Inclusion.Extracted(),
                new Inclusion.FieldIncluded("snp_gene_symbols")),
    DOWNLOAD_MAPPED_GENE(new Inclusion.Excluded(),
                         new Inclusion.CommaSeparatedIncluded("MAPPED_GENE"),
                         new Inclusion.Excluded()),
    ENTREZ_GENE_ID("ENTREZ_GENE_ID",
                   new Inclusion.Excluded(),
                   new Inclusion.CommaSeparatedIncluded("SNP_GENE_IDS"),
                   new Inclusion.FieldIncluded("snp_gene_ids")),
    UPSTREAM_MAPPED_GENE("UPSTREAM_MAPPED_GENE",
                         new Inclusion.Excluded(),
                         new Inclusion.Extracted(),
                         new Inclusion.FieldIncluded("upstream_gene_symbol")),
    UPSTREAM_ENTREZ_GENE_ID("UPSTREAM_ENTREZ_GENE_ID",
                            new Inclusion.Excluded(),
                            new Inclusion.FieldIncluded("UPSTREAM_GENE_ID"),
                            new Inclusion.FieldIncluded("upstream_gene")),
    UPSTREAM_GENE_DISTANCE("UPSTREAM_GENE_DISTANCE",
                           new Inclusion.Excluded(),
                           new Inclusion.FieldIncluded("UPSTREAM_GENE_DISTANCE"),
                           new Inclusion.FieldIncluded("upstream_gene_distance")),
    DOWNSTREAM_MAPPED_GENE("DOWNSTREAM_MAPPED_GENE",
                           new Inclusion.Excluded(),
                           new Inclusion.Extracted(),
                           new Inclusion.FieldIncluded("downstream_gene_symbol")),
    DOWNSTREAM_ENTREZ_GENE_ID("DOWNSTREAM_ENTREZ_GENE_ID",
                              new Inclusion.Excluded(),
                              new Inclusion.FieldIncluded("DOWNSTREAM_GENE_ID"),
                              new Inclusion.FieldIncluded("downstream_gene")),
    DOWNSTREAM_GENE_DISTANCE("DOWNSTREAM_GENE_DISTANCE",
                             new Inclusion.Excluded(),
                             new Inclusion.FieldIncluded("DOWNSTREAM_GENE_DISTANCE"),
                             new Inclusion.FieldIncluded("downstream_gene_distance")),
    STRONGEST_SNP_RISK_ALLELE("STRONGEST_SNP_RISK_ALLELE",
                              new Inclusion.CommaSeparatedIncluded("STRONGEST SNP-RISK ALLELE"),
                              new Inclusion.UniqueIncluded("STRONGEST SNP-RISK ALLELE"),
                              new Inclusion.Excluded()),
    SNP_RSID("SNP_RSID",
             new Inclusion.FieldIncluded("SNPS"),
             new Inclusion.UniqueIncluded("SNPS"),
             new Inclusion.Excluded()),
    SNP_RSID_FOR_ID("SNP_RSID", new Inclusion.Excluded(), new Inclusion.Extracted(), new Inclusion.Excluded()),
    DOWNLOAD_SNP_ID(new Inclusion.Excluded(),
                    new Inclusion.FieldIncluded("SNP_ID_CURRENT"),
                    new Inclusion.Excluded()),
    MERGED("MERGED",
           new Inclusion.Excluded(),
           new Inclusion.FieldIncluded("MERGED"),
           new Inclusion.FieldIncluded("merged")),
    SNP_ID("SNP_ID", new Inclusion.UniqueExtracted(), new Inclusion.Excluded(), new Inclusion.FieldIncluded("snp_id")),
    INTERGENIC_CONTEXT("IS_INTERGENIC",
                       new Inclusion.Excluded(),
                       new Inclusion.Extracted(),
                       new Inclusion.Excluded()),
    CONTEXT("CONTEXT", new Inclusion.Excluded(), new Inclusion.Extracted(), new Inclusion.Excluded()),
    DOWNLOAD_CONTEXT(new Inclusion.Excluded(),
                     new Inclusion.FieldIncluded("CONTEXT"),
                     new Inclusion.Excluded()),
    IS_INTERGENIC("IS_INTERGENIC",
                  new Inclusion.Excluded(),
                  new Inclusion.FieldIncluded("INTERGENIC"),
                  new Inclusion.FieldIncluded("intergenic")),
    RISK_ALLELE_FREQUENCY("RISK_ALLELE_FREQUENCY",
                          new Inclusion.FieldIncluded("RISK ALLELE FREQUENCY"),
                          new Inclusion.UniqueIncluded("RISK ALLELE FREQUENCY"),
                          new Inclusion.Excluded()),
    P_VALUE_MANTISSA("P_VALUE_MANTISSA",
                     new Inclusion.Extracted(),
                     new Inclusion.Extracted(),
                     new Inclusion.Excluded()),
    P_VALUE_EXPONENT("P_VALUE_EXPONENT",
                     new Inclusion.Extracted(),
                     new Inclusion.Extracted(),
                     new Inclusion.Excluded()),
    P_VALUE(new Inclusion.FieldIncluded("P-VALUE"),
            new Inclusion.UniqueIncluded("P-VALUE"),
            new Inclusion.Excluded()),
    DOWNLOAD_P_VALUE_MLOG(new Inclusion.Excluded(),
                          new Inclusion.FieldIncluded("PVALUE_MLOG"),
                          new Inclusion.Excluded()),
    P_VALUE_QUALIFIER("P_VALUE_QUALIFIER",
                      new Inclusion.FieldIncluded("P-VALUE (TEXT)"),
                      new Inclusion.UniqueIncluded("P-VALUE (TEXT)"),
                      new Inclusion.Excluded()),
    OR_BETA("OR_BETA",
            new Inclusion.FieldIncluded("OR OR BETA"),
            new Inclusion.UniqueIncluded("OR or BETA"),
            new Inclusion.Excluded()),
    CI("CI", new Inclusion.Extracted(), new Inclusion.Extracted(), new Inclusion.Excluded()),
    CI_QUALIFIER("CI_QUALIFIER", new Inclusion.Extracted(), new Inclusion.Extracted(), new Inclusion.Excluded()),
    SPREADSHEET_CI(new Inclusion.FieldIncluded("95% CI (TEXT)"),
                   new Inclusion.UniqueIncluded("95% CI (TEXT)"),
                   new Inclusion.Excluded()),
    PLATFORM("PLATFORM",
             new Inclusion.FieldIncluded("PLATFORM [SNPS PASSING QC]"),
             new Inclusion.FieldIncluded("PLATFORM [SNPS PASSING QC]"),
             new Inclusion.Excluded()),
    CNV("CNV", new Inclusion.Extracted(), new Inclusion.Excluded(), new Inclusion.Excluded()),
    SPREADSHEET_CNV(new Inclusion.FieldIncluded("CNV"),
                    new Inclusion.Excluded(),
                    new Inclusion.Excluded()),
    ASSOCIATION_ID("ASSOCIATION_ID",
                   new Inclusion.UniqueIncluded("GWASTUDIESSNPID"),
                   new Inclusion.Excluded(),
                   new Inclusion.FieldIncluded("studies_snp_id")),
    STUDY_ID("STUDY_ID",
             new Inclusion.UniqueIncluded("GWASTUDYID"),
             new Inclusion.Excluded(),
             new Inclusion.FieldIncluded("gwas_study_id")),
    RESULT_PUBLISHED("RESULT_PUBLISHED",
                     new Inclusion.Extracted(),
                     new Inclusion.Excluded(),
                     new Inclusion.Excluded()),
    NCBI_RESULT_PUBLISHED(new Inclusion.FieldIncluded("RESULTPUBLISHED"),
                          new Inclusion.Excluded(),
                          new Inclusion.Excluded()),
    CURATION_STATUS("CURATION_STATUS",
                    new Inclusion.Excluded(),
                    new Inclusion.Excluded(),
                    new Inclusion.Excluded()),
    UNIQUE_KEY(new Inclusion.FieldIncluded("UNIQUE_KEY"), new Inclusion.Excluded(), new Inclusion.Excluded()),
    // Added to deal with file returned from NCBI
    PUBMED_ID_ERROR("PUBMED_ID_ERROR",
                    new Inclusion.Excluded(),
                    new Inclusion.Excluded(),
                    new Inclusion.FieldIncluded("pubmdID_error")),
    NCBI_PAPER_TITLE("NCBI_PAPER_TITLE",
                     new Inclusion.Excluded(),
                     new Inclusion.Excluded(),
                     new Inclusion.FieldIncluded("paper_title")),
    NCBI_FIRST_AUTHOR("NCBI_FIRST_AUTHOR",
                      new Inclusion.Excluded(),
                      new Inclusion.Excluded(),
                      new Inclusion.FieldIncluded("author_first")),
    NCBI_NORMALISED_FIRST_AUTHOR("NCBI_NORMALIZED_FIRST_AUTHOR",
                                 new Inclusion.Excluded(),
                                 new Inclusion.Excluded(),
                                 new Inclusion.FieldIncluded("firstauthor")),
    NCBI_FIRST_UPDATE_DATE("NCBI_FIRST_UPDATE_DATE",
                           new Inclusion.Excluded(),
                           new Inclusion.Excluded(),
                           new Inclusion.Excluded()),
    SNP_ERROR("SNP_ERROR",
              new Inclusion.Excluded(),
              new Inclusion.Excluded(),
              new Inclusion.FieldIncluded("snp_id_error")),
    GENE_ERROR("GENE_ERROR",
               new Inclusion.Excluded(),
               new Inclusion.Excluded(),
               new Inclusion.FieldIncluded("gene_error")),
    SNP_GENE_ON_DIFF_CHR("SNP_GENE_ON_DIFF_CHR",
                         new Inclusion.Excluded(),
                         new Inclusion.Excluded(),
                         new Inclusion.FieldIncluded("snp_gene_on_diff_chr")),
    NO_GENE_FOR_SYMBOL("NO_GENE_FOR_SYMBOL",
                       new Inclusion.Excluded(),
                       new Inclusion.Excluded(),
                       new Inclusion.FieldIncluded("no_geneid_for_symbol")),
    GENE_NOT_ON_GENOME("GENE_NOT_ON_GENOME",
                       new Inclusion.Excluded(),
                       new Inclusion.Excluded(),
                       new Inclusion.FieldIncluded("gene_not_on_genome")),
    // Added for the alternative spreadsheet download
    EFO_TRAIT("EFO_TRAIT",
              new Inclusion.Excluded(),
              new Inclusion.CommaSeparatedIncluded("MAPPED_TRAIT"),
              new Inclusion.Excluded()),
    EFO_URI("EFO_URI",
            new Inclusion.Excluded(),
            new Inclusion.CommaSeparatedIncluded("MAPPED_TRAIT_URI"),
            new Inclusion.Excluded());

    private Optional<String> databaseName;
    private Inclusion ncbiInclusion;
    private Inclusion downloadInclusion;
    private Inclusion loadInclusion;
    private boolean isDate;

    CatalogHeaderBinding(Inclusion ncbiInclusion,
                         Inclusion downloadInclusion,
                         Inclusion loadInclusion) {
        this(ncbiInclusion, downloadInclusion, loadInclusion, false);
    }

    CatalogHeaderBinding(Inclusion ncbiInclusion,
                         Inclusion downloadInclusion,
                         Inclusion loadInclusion,
                         boolean isDate) {
        this.databaseName = Optional.empty();
        this.ncbiInclusion = ncbiInclusion;
        this.downloadInclusion = downloadInclusion;
        this.loadInclusion = loadInclusion;
        this.isDate = isDate;
    }

    CatalogHeaderBinding(String databaseName,
                         Inclusion ncbiInclusion,
                         Inclusion downloadInclusion,
                         Inclusion loadInclusion) {
        this(databaseName, ncbiInclusion, downloadInclusion, loadInclusion, false);
    }

    CatalogHeaderBinding(String databaseName,
                         Inclusion ncbiInclusion,
                         Inclusion downloadInclusion,
                         Inclusion loadInclusion,
                         boolean isDate) {
        this.databaseName = Optional.of(databaseName);
        this.ncbiInclusion = ncbiInclusion;
        this.downloadInclusion = downloadInclusion;
        this.loadInclusion = loadInclusion;
        this.isDate = isDate;
    }

    public Optional<String> getDatabaseName() {
        return this.databaseName;
    }

    public Inclusion getNcbiInclusion() {
        return this.ncbiInclusion;
    }

    public Inclusion getDownloadInclusion() {
        return this.downloadInclusion;
    }

    public Inclusion getLoadInclusion() {
        return this.loadInclusion;
    }

    public boolean isDate() {
        return this.isDate;
    }

    public interface Inclusion {
        boolean isRequired();

        boolean isIdentifier();

        boolean isConcatenatable();

        boolean mapsToColumn();

        Optional<String> columnName();

        class FieldIncluded implements Inclusion {
            private String columnName;

            FieldIncluded(String columnName) {
                this.columnName = columnName;
            }

            @Override public boolean isRequired() {
                return true;
            }

            @Override public boolean isIdentifier() {
                return false;
            }

            @Override public boolean isConcatenatable() {
                return false;
            }

            @Override public boolean mapsToColumn() {
                return true;
            }

            @Override public Optional<String> columnName() {
                return Optional.of(columnName);
            }
        }

        class CommaSeparatedIncluded implements Inclusion {
            private String columnName;

            CommaSeparatedIncluded(String columnName) {
                this.columnName = columnName;
            }

            @Override public boolean isRequired() {
                return true;
            }

            @Override public boolean isIdentifier() {
                return false;
            }

            @Override public boolean isConcatenatable() {
                return true;
            }

            @Override public boolean mapsToColumn() {
                return true;
            }

            @Override public Optional<String> columnName() {
                return Optional.of(columnName);
            }
        }

        class UniqueIncluded implements Inclusion {
            private String columnName;

            UniqueIncluded(String columnName) {
                this.columnName = columnName;
            }

            @Override public boolean isRequired() {
                return true;
            }

            @Override public boolean isIdentifier() {
                return true;
            }

            @Override public boolean isConcatenatable() {
                return false;
            }

            @Override public boolean mapsToColumn() {
                return true;
            }

            @Override public Optional<String> columnName() {
                return Optional.of(columnName);
            }
        }

        class UniqueExtracted implements Inclusion {
            @Override public boolean isRequired() {
                return true;
            }

            @Override public boolean isIdentifier() {
                return true;
            }

            @Override public boolean isConcatenatable() {
                return false;
            }

            @Override public boolean mapsToColumn() {
                return false;
            }

            @Override public Optional<String> columnName() {
                return Optional.empty();
            }
        }
        class Extracted implements Inclusion {
            @Override public boolean isRequired() {
                return true;
            }

            @Override public boolean isIdentifier() {
                return false;
            }

            @Override public boolean isConcatenatable() {
                return false;
            }

            @Override public boolean mapsToColumn() {
                return false;
            }

            @Override public Optional<String> columnName() {
                return Optional.empty();
            }
        }

        class Excluded implements Inclusion {
            @Override public boolean isRequired() {
                return false;
            }

            @Override public boolean isIdentifier() {
                return false;
            }

            @Override public boolean isConcatenatable() {
                return false;
            }

            @Override public boolean mapsToColumn() {
                return false;
            }

            @Override public Optional<String> columnName() {
                return Optional.empty();
            }
        }
    }
}
