/*
################################################################################
Create a view table that links SNPs and studies

author: Dani Welter
date:    December 16th 2016
version: 2.2.0.015
################################################################################
*/
--------------------------------------------------------
-- Generate view
--------------------------------------------------------

CREATE OR REPLACE VIEW STUDY_SNP_VIEW (
        STUDY_ID,
        SNP_ID)
  AS SELECT DISTINCT A.STUDY_ID AS STUDY_ID, S.RS_ID
   FROM ASSOCIATION A, ASSOCIATION_LOCUS AL,
       LOCUS L, LOCUS_RISK_ALLELE LRA, RISK_ALLELE R,
       RISK_ALLELE_SNP RAS, SINGLE_NUCLEOTIDE_POLYMORPHISM S
   WHERE  AL.ASSOCIATION_ID = A.ID AND
          AL.LOCUS_ID=L.ID AND
          LRA.LOCUS_ID = L.ID AND
          LRA.RISK_ALLELE_ID=R.ID AND
          S.ID = RAS.SNP_ID AND
          R.ID =RAS.RISK_ALLELE_ID
   ORDER BY A.STUDY_ID;
