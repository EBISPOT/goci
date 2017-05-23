/*
################################################################################
Create shortcut views between various concepts for use in the REST API

author: Dani Welter
date:    Nov 24th 2016
version: 2.2.0.013
################################################################################
*/
--------------------------------------------------------
-- Generate ASSOCIATION_SNP view
--------------------------------------------------------

CREATE OR REPLACE VIEW ASSOCIATION_SNP_VIEW AS
    SELECT DISTINCT A.ID AS ASSOCIATION_ID, S.ID AS SNP_ID
       FROM ASSOCIATION A, ASSOCIATION_LOCUS AL,
           LOCUS_RISK_ALLELE LRA,
           RISK_ALLELE_SNP RAS, SINGLE_NUCLEOTIDE_POLYMORPHISM S
       WHERE AL.ASSOCIATION_ID = A.ID AND
              AL.LOCUS_ID=LRA.LOCUS_ID AND
              LRA.RISK_ALLELE_ID=RAS.RISK_ALLELE_ID AND
              S.ID = RAS.SNP_ID
       ORDER BY A.ID;

--------------------------------------------------------
-- Generate ASSOCIATION_GENE view
--------------------------------------------------------

CREATE OR REPLACE VIEW ASSOCIATION_GENE_VIEW AS
    SELECT DISTINCT A.ID AS ASSOCIATION_ID, G.ID AS GENE_ID
       FROM ASSOCIATION A, ASSOCIATION_LOCUS AL,
           LOCUS_RISK_ALLELE LRA, RISK_ALLELE_SNP RAS,
           GENOMIC_CONTEXT GC, GENE G
       WHERE  AL.ASSOCIATION_ID = A.ID AND
              AL.LOCUS_ID=LRA.LOCUS_ID AND
              LRA.RISK_ALLELE_ID=RAS.RISK_ALLELE_ID AND
              GC.SNP_ID = RAS.SNP_ID AND
              G.ID = GC.GENE_ID
       ORDER BY A.ID;


--------------------------------------------------------
-- Generate SNP_GENE view
--------------------------------------------------------

CREATE OR REPLACE VIEW SNP_GENE_VIEW AS
    SELECT DISTINCT S.ID AS SNP_ID, G.ID AS GENE_ID
       FROM SINGLE_NUCLEOTIDE_POLYMORPHISM S,
           GENOMIC_CONTEXT GC, GENE G
       WHERE  GC.SNP_ID = S.ID AND
              G.ID = GC.GENE_ID
       ORDER BY S.ID;