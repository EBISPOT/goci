/*################################################################################

Fixing bug header NCBI_CATALOG_SUMMARY_VIEW

author:  C Malangone
date:    March 2018
version: 2.3.0.003
################################################################################
*/

CREATE OR REPLACE  VIEW NCBI_CATALOG_SUMMARY_VIEW AS
  SELECT
    ROWNUM as ID,
    V."STUDY_ADDED_DATE",
    V."PUBMED_ID",
    V."AUTHOR",
    V."PUBLICATION_DATE",
    V."JOURNAL",
    V."LINK",
    V."STUDY",
    V."DISEASE_TRAIT",
    V."INITIAL_SAMPLE_DESCRIPTION",
    V."REPLICATE_SAMPLE_DESCRIPTION",
    V."REGION",
    V."REPORTED_GENE",
    V."STRONGEST_SNP_RISK_ALLELE",
    V."SNP_RS_ID" AS SNP_RSID,
    V."SNP_ID",
    V."RISK_ALLELE_FREQUENCY",
    V."P_VALUE_MANTISSA",
    V."P_VALUE_EXPONENT",
    V."P_VALUE_QUALIFIER",
    V."OR_BETA",
    V."CI",
    V."CI_QUALIFIER",
    V."MANUFACTURER",
    V."QUALIFIER",
    V."SNP_COUNT",
    V."IMPUTED",
    V."STUDY_DESIGN_COMMENT",
    V."PLATFORM",
    V."CNV",
    V."ASSOCIATION_ID",
    V."STUDY_ID",
    V."CATALOG_PUBLISH_DATE",
    V."CATALOG_UNPUBLISH_DATE",
    V."TRAIT" AS EFO_TRAIT,
    V."URI" AS EFO_URI
  FROM
    (SELECT
       h.STUDY_ADDED_DATE,
       pub.PUBMED_ID,
       auth.FULLNAME_STANDARD AS author,
       pub.PUBLICATION_DATE,
       pub.PUBLICATION                                             AS JOURNAL,
       CONCAT('http://europepmc.org/abstract/MED/', pub.PUBMED_ID) AS LINK,
       pub.TITLE                                                   AS STUDY,
       dt.TRAIT                                                  AS DISEASE_TRAIT,
       s.INITIAL_SAMPLE_SIZE                                     AS INITIAL_SAMPLE_DESCRIPTION,
       s.REPLICATE_SAMPLE_SIZE                                   AS REPLICATE_SAMPLE_DESCRIPTION,
       r.NAME                                                    AS REGION,
       rg.GENE_NAME                                              AS REPORTED_GENE,
       ra.RISK_ALLELE_NAME                                       AS STRONGEST_SNP_RISK_ALLELE,
       snp.RS_ID                                                 AS SNP_RS_ID,
       snp.ID                                                    AS SNP_ID,
       a.RISK_FREQUENCY                                          AS RISK_ALLELE_FREQUENCY,
       a.PVALUE_MANTISSA                                         AS P_VALUE_MANTISSA,
       a.PVALUE_EXPONENT                                         AS P_VALUE_EXPONENT,
       a.PVALUE_DESCRIPTION                                      AS P_VALUE_QUALIFIER,
       (CASE WHEN a.OR_PER_COPY_NUM IS NOT NULL
         THEN a.OR_PER_COPY_NUM
        ELSE a.BETA_NUM END)                                     AS OR_BETA,
       a.RANGE                                                   AS CI,
       (CASE WHEN a.BETA_UNIT IS NOT NULL AND a.BETA_DIRECTION IS NOT NULL
         THEN CONCAT(CONCAT(a.BETA_UNIT, ' '), a.BETA_DIRECTION)
        ELSE a.DESCRIPTION END)                                  AS CI_QUALIFIER,
       p.MANUFACTURER,
       s.QUALIFIER,
       s.SNP_COUNT,
       s.IMPUTED,
       s.STUDY_DESIGN_COMMENT,
       s.PLATFORM,
       s.CNV,
       a.ID                                                      AS ASSOCIATION_ID,
       s.ID                                                      AS STUDY_ID,
       h.CATALOG_PUBLISH_DATE,
       h.CATALOG_UNPUBLISH_DATE,
       e.TRAIT,
       e.URI
     FROM STUDY s
       JOIN PUBLICATION pub ON s.PUBLICATION_ID = pub.ID
       JOIN AUTHOR auth on pub.FIRST_AUTHOR_ID = auth.ID
       JOIN HOUSEKEEPING h ON h.ID = s.HOUSEKEEPING_ID
       JOIN CURATION_STATUS cs ON h.CURATION_STATUS_ID = cs.ID
       LEFT JOIN STUDY_DISEASE_TRAIT sdt ON sdt.STUDY_ID = s.ID
       LEFT JOIN DISEASE_TRAIT dt ON dt.ID = sdt.DISEASE_TRAIT_ID
       LEFT JOIN ASSOCIATION a ON a.STUDY_ID = s.ID
       LEFT JOIN ASSOCIATION_LOCUS al ON al.ASSOCIATION_ID = a.ID
       LEFT JOIN LOCUS_RISK_ALLELE lra ON lra.LOCUS_ID = al.LOCUS_ID
       LEFT JOIN RISK_ALLELE ra ON ra.ID = lra.RISK_ALLELE_ID
       LEFT JOIN RISK_ALLELE_SNP ras ON ras.RISK_ALLELE_ID = lra.RISK_ALLELE_ID
       LEFT JOIN SINGLE_NUCLEOTIDE_POLYMORPHISM snp ON snp.ID = ras.SNP_ID
       LEFT JOIN SNP_LOCATION ls ON ls.SNP_ID = snp.ID
       LEFT JOIN LOCATION loc ON ls.LOCATION_ID = loc.id
       LEFT JOIN REGION r ON r.ID = loc.REGION_ID
       LEFT JOIN AUTHOR_REPORTED_GENE arg ON arg.LOCUS_ID = al.LOCUS_ID
       LEFT JOIN GENE rg ON rg.ID = arg.REPORTED_GENE_ID
       LEFT JOIN ASSOCIATION_EFO_TRAIT ae ON ae.ASSOCIATION_ID = a.ID
       LEFT JOIN STUDY_PLATFORM sp ON sp.STUDY_ID = s.ID
       LEFT JOIN PLATFORM p ON p.ID = sp.PLATFORM_ID
       LEFT JOIN EFO_TRAIT e ON e.ID = ae.EFO_TRAIT_ID
     ORDER BY pub.PUBLICATION_DATE DESC) V;